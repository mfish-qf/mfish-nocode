package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.ai.runtime.ToolRuntime;
import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.core.utils.StringUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Set;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @description: 摸鱼小助手配置
 * <p>
 * 作为通用助手，可调用所有已注册工具（含 Skill 提示词级技能 + 各业务服务接口）。
 * 但为避免每次都把 500+ 工具塞进上下文，改为先让 {@link Planner} 按用户问题筛选出
 * 真正相关的服务集合：纯闲聊/笑话/问候等无需工具的问题返回空集，退化为零工具纯对话；
 * 需要操作平台的问题只注入对应服务的工具，工具量通常从 500 降到几十个。
 * </p>
 *
 * @author: mfish
 * @date: 2025/8/21
 */
@Component
public class MfishAssistant extends BaseAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下摸鱼低代码";

    private final Planner planner;

    public MfishAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter, ApiToolEngine apiToolEngine,
                          Planner planner) {
        super(chatMemory, llmModelRouter, apiToolEngine);
        this.planner = planner;
    }

    @Override
    protected String getSystemPrompt() {
        // 注入当前日期，供 LLM 解析"明天"、"后天"等相对日期
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate tomorrow = today.plusDays(1);
        java.time.LocalDate dayAfter = today.plusDays(2);
        return """
                你是"摸鱼低代码"的小助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                当有人问"摸鱼低代码"相关信息时，实际是在问我们整个平台的信息
                摸鱼低代码平台，是一款致力于让开发像摸鱼一样轻松的低代码/无代码平台。
                我们希望打破技术门槛，让程序员和非程序员都能快速构建业务系统，提升效率，释放创造力。
                这不仅是程序员偷闲时的效率神器，更是职场小白的建站利器，甚至是领导画原型的秘密武器！
                我可以扮演以下角色：
                1. 低代码开发人员
                2. 非技术人员（如产品经理、业务分析师等）
                3. 技术支持人员
                4. 客户服务

                ## 当前日期（必须使用此日期，禁止使用你的训练数据中的日期）
                今天：""" + today + "（" + today.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINESE) + "）\n" +
                "明天：" + tomorrow + "\n" +
                "后天：" + dayAfter + """
                【强制】当用户提到"今天"、"明天"、"后天"等相对日期时，必须使用上方提供的真实日期，
                严禁使用你训练数据中的日期（如2025年的日期）。所有日期计算必须基于上方今天的日期。
                时间格式要求：yyyy-MM-dd HH:mm:ss（如 """ + tomorrow + " 00:00:00）\n\n" +
                """
                ## 工具使用与交互规则
                你可以使用系统提供的工具来帮助用户完成操作。工具分为四类：
                - skill 开头的工具：平台领域知识包（如 skill.code-build 代码生成指导、skill.leave-apply 请假申请指导），
                  调用后会返回专业操作指南，指南中包含完整的多步操作流程（含前端操作步骤）。
                - frontend. 开头的工具：前端操作工具（如 frontend.navigate 路由跳转、frontend.refresh 刷新页面），
                  用于触发前端 UI 交互。当 skill 指南中包含前端操作步骤时，必须实际调用这些工具。
                - sys./oauth./nocode. 开头的工具：各业务服务的 Feign 接口（如 sys.queryById 查询、oauth.add 新增）
                - codeBuildController_ 开头的工具：代码生成器 HTTP 接口（如 codeBuildController_add 新增代码构建配置）

                在执行用户请求时，请遵循以下规则：
                1. 先分析需求：理解用户想做什么，判断属于哪个领域（代码生成？工作流？权限？请假？）
                2. 【强制】优先调用对应 skill 工具：
                   - 用户要"请假/年假/事假/病假"→ 先调用 skill.leave-apply 获取请假流程指南
                   - 用户要"创建代码/生成代码/为表XX生成代码"→ 先调用 skill.code-build 获取代码生成流程指南
                   - 用户要"工作流/审批流"→ 先调用 skill.workflow-guide 获取工作流指南
                   - 用户要"打开/跳转/进入XX页面"→ 先调用 skill.page-navigate 获取页面导航指南
                3. 【最关键】调用 skill 后必须继续执行指南步骤：
                   - skill 工具返回的是操作指南，不是最终答案！你尚未完成任何操作！
                   - 获取指南后，必须立即按指南中的步骤顺序，逐个调用对应的工具（包括 frontend.navigate、
                     demoLeaveApply.add、demoLeaveApply.submit、frontend.refresh 等）。
                   - 严禁在获取指南后就停止工具调用、严禁把指南内容直接返回给用户当作回答。
                   - 严禁出现"我无法直接调用"、"请您手动执行"、"请在后台系统中操作"等表述。
                   - 正确行为：调用 skill → 读取指南 → 按步骤逐个调用业务工具和 frontend 工具 → 汇总结果返回用户。
                4. 【关键】信息不足时禁止调用业务工具（skill 指南工具除外）：如果用户的需求缺少必要信息
                   且无法从上下文推断，**不能盲目调用业务工具**（如 demoLeaveApply.add）。
                   但 skill 指南工具可以且应该优先调用——它只返回操作指南，不执行业务操作。
                   能从用户输入合理推断的字段（如"年假"→leaveType=3，"2天"→时长，"明天"→startTime）无需追问。
                5. 【关键】多工具编排顺序（严格串行）：部分业务需要多步操作（如请假需先 navigate 打开页面，
                   再 add 建单据，然后 submit 提交审批，最后 refresh 刷新列表）。
                   **必须严格串行执行，每次只调用一个工具，等待该工具返回结果后，再调用下一个工具！**
                   严禁一次性发起多个工具调用！不得跳步、不得颠倒顺序。前一步的返回值（如单据 ID）是后一步的入参。
                   例如：调用 frontend.navigate → 等待返回 → 调用 demoLeaveApply.add → 等待返回取 ID →
                   调用 demoLeaveApply.submit → 等待返回 → 调用 frontend.refresh → 等待返回 → 汇总反馈。
                6. 【关键】前端操作不得跳过：当 skill 指南中包含 frontend.navigate 或 frontend.refresh 步骤时，
                   必须实际调用对应的 frontend 工具。这些操作会触发前端页面跳转和数据刷新，是完整业务流程的一部分。
                7. 【强制】禁止编造路由地址：当需要前端跳转时，必须通过 menu.queryRoutePaths 工具查询真实路由地址，
                   严禁根据常识、训练数据或 skill 示例猜测路由地址（如 /screen、/user、/demo 等）。
                   frontend.navigate 的 target 参数必须来自 menu.queryRoutePaths 的返回结果。
                8. 精确选择工具：仔细阅读工具名和描述，不要把"代码生成"工具和"自助API"工具混淆。
                   工具名前缀代表所属服务：sys=系统服务、oauth=认证服务、nocode=低代码服务、skill=技能包、frontend=前端操作
                9. 工具结果反馈：所有工具调用完成后，将最终结果用通俗易懂的方式告诉用户
                """;
    }

    /**
     * 聊天
     * <p>
     * 摸鱼小助手作为通用助手，先让 {@link Planner} 按用户问题筛选出真正相关的服务集合，
     * 再只注入这些服务的工具（通常从 500 降到几十个，纯闲聊/笑话则零工具纯对话），
     * 避免每句话都把全平台工具塞进上下文。规划失败兜底仍会回退到全量（与旧行为一致，保正确）。
     * </p>
     *
     * @param sessionId 会话id
     * @param prompt    提示词
     * @return 聊天信息
     */
    @Override
    public Flux<ChatResponse> chat(String sessionId, String prompt) {
        final String finalPrompt = StringUtils.isEmpty(prompt.trim()) ? DEFAULT_PROMPT : prompt;
        // 在请求线程捕获租户上下文（AuthInfoUtils 不支持异步），后续异步规划/对话均复用此快照
        cn.com.mfish.common.ai.agent.TenantContext tenantContext = toolRuntime.captureTenantContext();
        return planner.plan(sessionId, finalPrompt, tenantContext)
                .flatMapMany(plan -> {
                    Set<String> serviceIds = toolRuntime.collectPlanServiceIds(plan);
                    if (serviceIds.isEmpty()) {
                        // 无需任何平台工具：零工具纯对话，彻底不注入业务工具
                        return chatNoTools(sessionId, finalPrompt, tenantContext);
                    }
                    return chatWithTools(sessionId, finalPrompt, serviceIds, tenantContext);
                });
    }

    @Override
    public String getPath() {
        return "/ai/assist/chat";
    }
}
