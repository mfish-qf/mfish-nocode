package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.entity.BaseTreeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：树操作通用类
 * @date ：2022/11/11 18:10
 */
@Slf4j
public class TreeUtils {
    /**
     * 构建树
     *
     * @param pId   父ID
     * @param items 源列表
     * @param trees 目标树
     * @param cls   源类型
     * @param <T>   树类型
     * @param <P>   id类型
     */
    public static <T extends BaseTreeEntity<P>, P> void buildTree(P pId, List<T> items, List<T> trees, Class<T> cls) {
        if (items == null || items.size() == 0) {
            return;
        }
        for (T item : items) {
            if (!item.getParentId().equals(pId)) {
                continue;
            }
            try {
                T parent = cls.newInstance();
                BeanUtils.copyProperties(item, parent);
                trees.add(parent);
                List<BaseTreeEntity<P>> child = new ArrayList<>();
                parent.setChildren(child);
                if (items.stream().anyMatch(p -> p.getParentId().equals(item.getId()))) {
                    buildTree(item.getId(), items, (List<T>) child, cls);
                }
                //没有child设置为空防止前端显示多一个折叠符号
                if (child.size() == 0) {
                    parent.setChildren(null);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                String error = "错误:构建树实例化出错";
                log.error(error, e);
                throw new MyRuntimeException(error);
            }
        }
    }
}
