package cn.com.mfish.sys.mapper;

import cn.com.mfish.sys.api.entity.DictCategory;
import cn.com.mfish.common.sys.req.ReqDictCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 树形分类
 * @author: mfish
 * @date: 2024-03-12
 * @version: V2.3.1
 */
public interface DictCategoryMapper extends BaseMapper<DictCategory> {
    /**
     * 新增分类（使用自定义插入以自动生成树编码）
     *
     * @param dictCategory 分类对象
     * @return 影响行数
     */
    int insertCategory(DictCategory dictCategory);

    /**
     * 查询满足条件的分类最大等级
     *
     * @param reqDictCategory 过滤参数
     * @return 最大分类等级
     */
    Integer queryMaxCategoryLevel(@Param("reqDictCategory") ReqDictCategory reqDictCategory);

    /**
     * 查询分类列表
     *
     * @param reqDictCategory 过滤参数
     * @param levels          父级等级列表
     * @return 分类列表
     */
    List<DictCategory> queryCategory(@Param("reqDictCategory") ReqDictCategory reqDictCategory, @Param("levels") List<Integer> levels);

    /**
     * 查询1级分类列表
     *
     * @param reqDictCategory 过滤参数
     * @param levels          所有父级等级
     * @return 返回1级分类列表
     */
    List<DictCategory> queryOneLevelCategory(@Param("reqDictCategory") ReqDictCategory reqDictCategory, @Param("levels") List<Integer> levels);

    /**
     * 查询子分类包含自己
     *
     * @param list treeCode列表
     * @return 返回子分类列表
     */
    List<DictCategory> queryChildCategory(@Param("reqDictCategory") ReqDictCategory reqDictCategory, @Param("levels") List<Integer> levels, @Param("list") List<String> list);
}
