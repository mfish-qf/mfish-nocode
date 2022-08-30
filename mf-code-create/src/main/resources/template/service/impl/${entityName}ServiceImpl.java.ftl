package ${packageName}.service.impl;

import ${packageName}.entity.${entityName};
import ${packageName}.mapper.${entityName}Mapper;
import ${packageName}.service.I${entityName}Service;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: ${tableInfo.tableDesc}
 * @Author: mfish
 * @Date: ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0
 */
@Service
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}> implements I${entityName}Service {

}
