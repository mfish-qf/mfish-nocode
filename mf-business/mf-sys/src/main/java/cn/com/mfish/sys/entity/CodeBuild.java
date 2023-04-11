package cn.com.mfish.sys.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.0.0
 */
@Data
@TableName("sys_code_build")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sys_code_build对象", description = "代码构建")
public class CodeBuild extends BaseEntity<Long> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    @ApiModelProperty(value = "数据库连接ID")
	private String connectId;
    @ApiModelProperty(value = "表名")
	private String tableName;
    @ApiModelProperty(value = "接口路径前缀 例如:/oauth2/user接口前缀为oauth2(不传会使用packageName，最底层包名 例如:cn.com.mfish.sys包会使用sys)")
	private String apiPrefix;
    @ApiModelProperty(value = "实体类名(不传会使用表名驼峰化)")
	private String entityName;
    @ApiModelProperty(value = "项目包名(不传使用默认包名 cn.com.mfish.web)")
	private String packageName;
    @ApiModelProperty(value = "表描述(不传会获取数据库表中的中文描述，如果也为空则使用表名)")
	private String tableComment;
    @ApiModelProperty(value = "Form查询条件(json串)")
	private String queryParams;
}
