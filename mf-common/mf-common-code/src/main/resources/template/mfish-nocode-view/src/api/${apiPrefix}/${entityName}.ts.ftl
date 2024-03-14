import { defHttp } from "/@/utils/http/axios";
import { ${entityName}, Req${entityName}, ${entityName}PageModel } from "/@/api/${apiPrefix}/model/${entityName}Model";

/**
 * @description: ${tableInfo.tableComment}
 * @author: mfish
 * @date: ${.now?string["yyyy-MM-dd"]}
 * @version: V1.2.0
 */
enum Api {
  ${entityName} = "/${apiPrefix}/${entityName?uncap_first}"
}

/**
 * 分页列表查询
 *
 * @param req${entityName}
 * @return
 */
export const get${entityName}List = (req${entityName}?: Req${entityName}) => {
  return defHttp.get<${entityName}PageModel>({ url: Api.${entityName}, params: req${entityName} });
};

/**
 * 导出${tableInfo.tableComment}
 * @param req${entityName}
 */
export function export${entityName}(req${entityName}?: Req${entityName}) {
  return defHttp.download({ url: Api.${entityName} + "/export", params: req${entityName} });
};

/**
 * 新增${tableInfo.tableComment}
 *
 * @param ${entityName?uncap_first}
 * @return
 */
export function insert${entityName}(${entityName?uncap_first}: ${entityName}) {
  return defHttp.post<${entityName}>({ url: Api.${entityName}, params: ${entityName?uncap_first} }, { successMessageMode: "message" });
};

/**
 * 修改${tableInfo.tableComment}
 *
 * @param ${entityName?uncap_first}
 * @return
 */
export function update${entityName}(${entityName?uncap_first}: ${entityName}) {
  return defHttp.put<${entityName}>({ url: Api.${entityName}, params: ${entityName?uncap_first} }, { successMessageMode: "message" });
};

/**
 * 删除${tableInfo.tableComment}
 *
 * @param id 唯一ID
 * @return
 */
export function delete${entityName}(id: <#if tableInfo.idType==''||tableInfo.idType=='String'>string<#else>number</#if>) {
  return defHttp.delete<${entityName}>({ url: Api.${entityName} + "/" + id }, { successMessageMode: "message" });
};