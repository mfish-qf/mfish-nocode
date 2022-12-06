package cn.com.mfish.common.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author ：qiufeng
 * @description：spring对象创建类
 * @date ：2022/12/6 17:08
 */
public final class SpringBeanFactory implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        SpringBeanFactory.beanFactory = configurableListableBeanFactory;
    }

    /**
     * 通过名称获取bean
     *
     * @param name
     * @param <T>
     * @return
     * @throws BeansException
     */
    public static <T> T getBean(String name) {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 通过类型获取bean
     *
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clz) {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 判断是否包含某个名称的bean
     *
     * @param name
     * @return
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 通过名称获取bean类型
     *
     * @param name
     * @return
     */
    public static Class<?> getType(String name) {
        return beanFactory.getType(name);
    }
}
