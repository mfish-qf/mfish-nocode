package cn.com.mfish.common.oauth.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 租户内「姓名→用户ID」解析结果
 * <p>
 * 解析规则（租户内按 nickname 精确匹配，绝不猜测）:
 * <ul>
 *   <li>RESOLVED: 唯一命中，userId 为匹配到的用户ID</li>
 *   <li>UNRESOLVED: 未匹配到任何用户，userId 为 null</li>
 *   <li>AMBIGUOUS: 租户内多人同名，userId 为 null (不允许随机挑选，避免挂错人)</li>
 * </ul>
 * 调用方约定: 仅当 {@link #isResolved()} 为真时才使用 userId; 其余情况一律降级为按姓名处理。
 *
 * @author: mfish
 * @date: 2026-10-27
 */
@Schema(description = "租户内姓名→用户ID解析结果")
@Data
@Accessors(chain = true)
public class UserIdResolveVo {
    /**
     * 解析状态常量
     */
    public static final String RESOLVED = "RESOLVED";
    public static final String UNRESOLVED = "UNRESOLVED";
    public static final String AMBIGUOUS = "AMBIGUOUS";

    @TableField(exist = false)
    @Schema(description = "输入的姓名(已清洗)")
    private String name;

    @TableField(exist = false)
    @Schema(description = "唯一命中时的用户ID; UNRESOLVED/AMBIGUOUS 时为 null")
    private String userId;

    @TableField(exist = false)
    @Schema(description = "解析状态: RESOLVED 唯一命中 / UNRESOLVED 未匹配 / AMBIGUOUS 租户内多人同名")
    private String status;

    /**
     * 是否成功解析出唯一用户ID
     *
     * @return true 表示可安全使用 userId 作为人员主键
     */
    public boolean isResolved() {
        return RESOLVED.equals(status) && userId != null && !userId.isEmpty();
    }
}
