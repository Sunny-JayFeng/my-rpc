package handler;

import management.ServiceClassManagement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 方法执行处理
 * @author JayFeng
 * @date 2021/2/19
 */
public class MethodInvokeHandler {

    public static Object methodInvoke(String className, String methodName, Map<String, Object> paramsMap) {
        Class clazz = ServiceClassManagement.getClass(className);
        Object obj = ServiceClassManagement.getObject(className);
        try {
            Method method = clazz.getMethod(methodName, clazz);
            return method.invoke(obj, paramsMap);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
