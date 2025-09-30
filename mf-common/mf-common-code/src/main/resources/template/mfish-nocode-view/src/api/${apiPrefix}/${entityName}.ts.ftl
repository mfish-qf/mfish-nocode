import { defHttp } from "@mfish/core/utils/http/axios";
import { ${entityName}, Req${entityName}, ${entityName}PageModel } from "@/api/${apiPrefix}/model/${entityName}Model";

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V2.2.0
 */
enum Api {
  ${entityName} = "/${apiPrefix}/${entityName?uncap_first}"
}

/**
 * 分页列表查询
 *
 * @param req${entityName}
 * @return 返回分页列表
 */
export const get${entityName}List = (req${entityName}?: Req${entityName}) => {
  return defHttp.get<${entityName}PageModel>({ url: Api.${entityName}, params: req${entityName} });
};

/**
 * 通过id查询
 *
 * @param id 唯一ID
 * @return 返回分页列表
 */
export function get${entityName}ById(id: string) {
  return defHttp.get<${entityName}>({ url: `<#noparse>${</#noparse>Api.${entityName}<#noparse>}</#noparse>/<#noparse>${id}</#noparse>` });
}

/**
 * 导出${tableInfo.tableComment}
 * @param req${entityName} 请求参数
 */
export function export${entityName}(req${entityName}?: Req${entityName}) {
  return defHttp.download({ url: `<#noparse>${</#noparse>Api.${entityName}<#noparse>}</#noparse>/export`, params: req${entityName} });
}

/**
 * 新增${tableInfo.tableComment}
 *
 * @param ${entityName?uncap_first} 请求参数
 * @return 返回结果
 */
export function insert${entityName}(${entityName?uncap_first}: ${entityName}) {
  return defHttp.post<${entityName}>({ url: Api.${entityName}, params: ${entityName?uncap_first} }, { successMessageMode: "message" });
}

/**
 * 修改${tableInfo.tableComment}
 *
 * @param ${entityName?uncap_first}
 * @return 返回结果
 */
export function update${entityName}(${entityName?uncap_first}: ${entityName}) {
  return defHttp.put<${entityName}>({ url: Api.${entityName}, params: ${entityName?uncap_first} }, { successMessageMode: "message" });
}

/**
 * 删除${tableInfo.tableComment}
 *
 * @param id 唯一ID
 * @return 返回结果
 */
export function delete${entityName}(id: <#if tableInfo.idType==''||tableInfo.idType=='String'>string<#else>number</#if>) {
  return defHttp.delete<boolean>({ url: `<#noparse>${</#noparse>Api.${entityName}<#noparse>}</#noparse>/<#noparse>${id}</#noparse>` }, { successMessageMode: "message" });
}

/**
 * 批量删除${tableInfo.tableComment}
 *
 * @param ids 唯一ID多个逗号隔开
 * @return 返回结果
 */
export function deleteBatch${entityName}(ids: string) {
  return defHttp.delete<boolean>({ url: `<#noparse>${</#noparse>Api.${entityName}<#noparse>}</#noparse>/batch/<#noparse>${ids}</#noparse>` }, { successMessageMode: "message" });
}
