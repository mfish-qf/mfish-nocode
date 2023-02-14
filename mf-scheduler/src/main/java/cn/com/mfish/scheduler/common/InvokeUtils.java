package cn.com.mfish.scheduler.common;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 反射调用通用类
 * @author: mfish
 * @date: 2023/2/13 22:47
 */
public class InvokeUtils {
    @Data
    @Accessors(chain = true)
    public static class InvokeParams {
        /**
         * 参数类型
         */
        private String type;
        /**
         * 参数值
         */
        private Object value;
    }

    /**
     * 反射调用方法
     *
     * @param className
     * @param methodName
     * @param params
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> Object invokeMethod(String className, String methodName, List<T> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //本地方法直接通过类名无法获取到需要class.forName转换
        Object bean = SpringBeanFactory.getBean(Class.forName(className));
        return invokeMethod(bean, methodName, params);
    }

    /**
     * 调用 Feign方法
     *
     * @param className
     * @param methodName
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static <T> Object invokeFeignMethod(String className, String methodName, List<T> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //Feign方法直接通过类名能够获取到
        Object bean = SpringBeanFactory.getBean(className);
        return invokeMethod(bean, methodName, params);
    }

    /**
     * 通用调用方法
     *
     * @param bean
     * @param methodName
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static <T> Object invokeMethod(Object bean, String methodName, List<T> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        if (params != null && !params.isEmpty()) {
            List<Class<?>> listTypes = new ArrayList<>();
            List<Object> listValues = new ArrayList<>();
            for (T param : params) {
                //支持invokeParams数组形式参数和简单类型普通数组形式参数，类似["aaaa",1,2L]
                if (param instanceof InvokeParams) {
                    InvokeParams iParam = (InvokeParams) param;
                    //如果为设置类型，直接根据值获取类型。复杂类型不准确
                    if (StringUtils.isEmpty(iParam.getType())) {
                        listTypes.add(iParam.getValue().getClass());
                    } else {
                        listTypes.add(Class.forName(iParam.getType()));
                    }
                    if (iParam.getValue() instanceof JSONObject) {
                        listValues.add(JSONObject.toJavaObject((JSONObject)iParam.getValue(), Class.forName(iParam.getType())));
                        continue;
                    }
                    listValues.add(iParam.getValue());
                    continue;
                }
                listTypes.add(param.getClass());
                listValues.add(param);
            }
            Method method = bean.getClass().getMethod(methodName, listTypes.toArray(new Class[params.size()]));
            return method.invoke(bean, listValues.toArray());
        }
        Method method = bean.getClass().getMethod(methodName);
        return method.invoke(bean);
    }
}
