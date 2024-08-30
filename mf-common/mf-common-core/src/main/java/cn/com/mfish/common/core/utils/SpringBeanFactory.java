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
     * @param name bean的名称
     * @param <T> 泛型标记，表示返回的bean的类型
     * @return 获取到的bean实例
     * @throws BeansException 如果无法获取bean时抛出的异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 通过类型获取bean
     * 该方法通过传入的类类型，从Spring的bean工厂中获取对应的bean实例
     * 这是实现依赖注入功能的一个重要入口方法，允许在应用程序中根据需要获取各种bean实例
     *
     * @param clz 期望获取的bean的类类型
     * @param <T> 泛型标记，表示方法可以适用于任何类型
     * @return 返回对应类型的bean实例如果bean工厂中没有找到对应类型的bean，将抛出相应的异常
     */
    public static <T> T getBean(Class<T> clz) {
        return beanFactory.getBean(clz);
    }

    /**
     * 通过名称和类型获取bean
     * 此方法旨在通过Spring的beanFactory获取特定名称和类型的bean实例
     * 它提供了类型安全的方式获取bean，避免了强制类型转换
     *
     * @param name 需要获取的bean的名称
     * @param clazz 需要获取的bean的类型
     * @param <T> 泛型标记，表示任何类型
     * @return 返回指定名称和类型的bean实例，如果找不到则抛出NoSuchBeanDefinitionException异常
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return beanFactory.getBean(name, clazz);
    }


    /**
     * 通过名称获取bean类型
     *
     * @param name bean的名称
     * @return 返回bean的类型，如果bean不存在或类型无法确定，则返回null
     */
    public static Class<?> getType(String name) {
        return beanFactory.getType(name);
    }

    /**
     * 获取远程服务
     *
     * @param clazz 远程服务类型
     * @param <T> 泛型
     * @return 远程服务bean
     */
    public static <T> T getRemoteService(Class<T> clazz) {
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
