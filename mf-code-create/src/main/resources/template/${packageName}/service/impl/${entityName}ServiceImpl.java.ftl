package ${packageName}.service.impl;

import ${packageName}.entity.${entityName};
import ${packageName}.mapper.${entityName}Mapper;
import ${packageName}.service.${entityName}Service;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: ${tableInfo.tableComment}
 * @Author: mfish
 * @Date: ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0
 */
@Service
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}> implements ${entityName}Service {

}
