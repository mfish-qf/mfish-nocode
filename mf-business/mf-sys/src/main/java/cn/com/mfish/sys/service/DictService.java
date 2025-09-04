package cn.com.mfish.sys.service;

import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.req.ReqDict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @version: V2.1.1
 */
public interface DictService extends IService<Dict> {
    Result<Dict> updateDict(Dict dict);

    /**
     * 字典编码是否存在
     *
     * @param id id
     * @param dictCode 字典编码
     * @return 返回是否存在
     */
    boolean isDictCodeExist(String id, String dictCode);

    /**
     * 删除字典
     *
     * @param id id
     * @return 返回删除结果
     */
    Result<Boolean> deleteDict(String id);

    /**
     * 查询字典列表
     * @param reqDict 查询参数
     * @param reqPage 分页参数
     * @return
     */
    List<Dict> queryList(ReqDict reqDict, ReqPage reqPage);
}
