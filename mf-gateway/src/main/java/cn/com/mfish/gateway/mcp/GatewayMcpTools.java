package cn.com.mfish.gateway.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 网关MCP工具集
 * 将网关背后的微服务能力暴露为MCP工具，让外部AI Agent可以通过MCP协议调用
 *
 * @author: mfish
 * @date: 2026/06/26
 */
@Component
public class GatewayMcpTools {

    private final ReactiveDiscoveryClient discoveryClient;

    public GatewayMcpTools(ReactiveDiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 查询所有已注册的微服务列表
     * AI Agent可通过此工具了解当前系统中有哪些可用服务
     */
    @McpTool(description = "查询当前网关下所有已注册的微服务列表，返回服务ID列表")
    public String listServices() {
        List<String> services = discoveryClient.getServices().collectList().block();
        if (services == null || services.isEmpty()) {
            return "当前没有已注册的微服务";
        }
        return String.join(", ", services);
    }

    /**
     * 查询指定微服务的实例详情
     * AI Agent可通过此工具了解某个服务的运行实例、端口等信息
     */
    @McpTool(description = "查询指定微服务的实例详情，包括主机、端口、元数据等")
    public String getServiceInstances(
            @McpToolParam(description = "微服务ID，如mf-oauth、mf-sys等") String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId).collectList().block();
        if (instances == null || instances.isEmpty()) {
            return "未找到服务: " + serviceId;
        }
        StringBuilder sb = new StringBuilder();
        for (ServiceInstance inst : instances) {
            sb.append("实例: ").append(inst.getInstanceId())
                    .append(", 地址: ").append(inst.getHost())
                    .append(":").append(inst.getPort())
                    .append(", 元数据: ").append(inst.getMetadata())
                    .append("\n");
        }
        return sb.toString();
    }
}
