package cn.com.mfish.common.scheduler.config.utils;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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
     * @param className 类名，用于获取对应的Bean实例
     * @param methodName 方法名，指定要调用的方法
     * @param params 方法参数列表，可能影响方法的执行结果
     * @return 调用方法后的返回值，类型依赖于被调用的方法
     * @throws NoSuchMethodException 当找不到指定的方法时抛出
     * @throws InvocationTargetException 调用方法时，如果方法抛出异常，则包装在该异常中抛出
     * @throws IllegalAccessException 如果访问方法时存在访问限制，则抛出
     * @throws ClassNotFoundException 如果类名找不到对应的类时抛出
     */
    public static <T> Object invokeMethod(String className, String methodName, List<T> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //本地方法直接通过类名无法获取到需要class.forName转换
        Object bean = SpringBeanFactory.getBean(Class.forName(className));
        return invokeMethod(bean, methodName, params);
    }

    /**
     * 调用 Feign方法
     *
     * @param className 类名，用于获取对应的Bean实例
     * @param methodName 方法名，指定要调用的方法
     * @param params 方法参数列表，可能影响方法的执行结果
     * @return 调用方法后的返回值，类型依赖于被调用的方法
     * @throws NoSuchMethodException 当找不到指定的方法时抛出
     * @throws InvocationTargetException 调用方法时，如果方法抛出异常，则包装在该异常中抛出
     * @throws IllegalAccessException 如果访问方法时存在访问限制，则抛出
     * @throws ClassNotFoundException 如果类名找不到对应的类时抛出
     */
    public static <T> Object invokeFeignMethod(String className, String methodName, List<T> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //Feign方法直接通过类名能够获取到
        Object bean = SpringBeanFactory.getBean(className);
        return invokeMethod(bean, methodName, params);
    }

    /**
     * 通用调用方法
     *
     * @param bean Bean实例
     * @param methodName 方法名，指定要调用的方法
     * @param params 方法参数列表，可能影响方法的执行结果
     * @return 调用方法后的返回值，类型依赖于被调用的方法
     * @throws NoSuchMethodException 当找不到指定的方法时抛出
     * @throws InvocationTargetException 调用方法时，如果方法抛出异常，则包装在该异常中抛出
     * @throws IllegalAccessException 如果访问方法时存在访问限制，则抛出
     * @throws ClassNotFoundException 如果类名找不到对应的类时抛出
     */
    public static <T> Object invokeMethod(Object bean, String methodName, List<T> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        if (params != null && !params.isEmpty()) {
            List<Class<?>> listTypes = new ArrayList<>();
            List<Object> listValues = new ArrayList<>();
            for (T param : params) {
                //支持invokeParams数组形式参数和简单类型普通数组形式参数，类似["aaaa",1,2L]
                if (param instanceof InvokeParams iParam) {
                    //如果为设置类型，直接根据值获取类型。复杂类型不准确
                    if (StringUtils.isEmpty(iParam.getType())) {
                        listTypes.add(iParam.getValue().getClass());
                    } else {
                        listTypes.add(Class.forName(iParam.getType()));
                    }
                    if (iParam.getValue() instanceof JSONObject) {
                        listValues.add(JSONObject.parseObject(JSON.toJSONString(iParam.getValue()), Class.forName(iParam.getType())));
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

    /**
     * 调用参数转换成对象
     *
     * @param params 参数
     * @return 返回转换后的参数对象
     */
    public static List<?> strParams2Obj(String params) {
        List<?> list = null;
        if (!StringUtils.isEmpty(params)) {
            //默认转化为invokeParams类型，如果转换失败，直接转换为普通数组
            try {
                list = JSON.parseArray(params, InvokeUtils.InvokeParams.class);
            } catch (Exception e) {
                list = JSON.parseArray(params);
            }
        }
        return list;
    }
}
