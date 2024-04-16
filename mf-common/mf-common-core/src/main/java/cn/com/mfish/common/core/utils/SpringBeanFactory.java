package cn.com.mfish.common.core.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.openfeign.FeignContext;

/**
 * @author: mfish
 * @description: spring对象创建类
 * @date: 2022/12/6 17:08
 */
public final class SpringBeanFactory implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
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
        return beanFactory.getBean(clz);
    }

    /**
     * 通过名称和类型获取bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return beanFactory.getBean(name, clazz);
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

    /**
     * 获取FeignClient的bean
     *
     * @param name
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getFeignBean(String name, Class<T> tClass) {
        FeignContext feignContext = beanFactory.getBean(FeignContext.class);
        return feignContext.getInstance(name, tClass);
    }
}
