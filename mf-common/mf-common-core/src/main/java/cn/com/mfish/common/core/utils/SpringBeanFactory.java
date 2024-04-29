package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.ServiceConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

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
     * 获取远程服务
     * @param clazz 远程服务类型
     * @return 远程服务bean
     * @param <T>
     */
    public static <T> T  getRemoteService(Class<T> clazz) {
        T remoteService;
        //单体服务通过name获取RPC的覆盖服务
        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            remoteService = SpringBeanFactory.getBean(StringUtils.firstLowerCase(clazz.getSimpleName()));
        } else {
            remoteService = SpringBeanFactory.getBean(clazz);
        }
        return remoteService;
    }
}
