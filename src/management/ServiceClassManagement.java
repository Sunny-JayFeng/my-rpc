package management;

import annotation.RPCService;
import annotation.RPCServicePackage;
import util.ScanPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供服务的类管理类
 * @author JayFeng
 * @date 2021/2/19
 */
public class ServiceClassManagement {

    private static Map<String, Class> classMap;

    private static Map<String, Object> objectMap = new HashMap<>(64);

    /**
     * 加载所有 RPC 服务类，存到 classMap 集合
     * @param rpcStartClass
     */
    public static void loadClassMap(Class rpcStartClass) {
        RPCServicePackage packageAnnotation = (RPCServicePackage) rpcStartClass.getAnnotation(RPCServicePackage.class);
        String[] packageNames = packageAnnotation.value();
        classMap = ScanPackage.getClassMap(packageNames);
        loadObjectMap();
    }

    /**
     * 创建所有 RPC 服务类对象，存到 objectMap 集合
     */
    private static void loadObjectMap() {
        for (String className : classMap.keySet()) {
            Class clazz = classMap.get(className);
            try {
                objectMap.put(className, clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取类模板
     * @param className
     * @return
     */
    public static Class getClass(String className) {
        return classMap.get(className);
    }

    /**
     * 获取类对象
     * @param className
     * @return
     */
    public static Object getObject(String className) {
        return objectMap.get(className);
    }

}
