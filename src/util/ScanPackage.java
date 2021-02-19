package util;

import annotation.RPCService;
import exception.ClassNameRepeatException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 包扫描
 * @author JayFeng
 * @date 2021/2/19
 */
public class ScanPackage {

    private static void putClass(String packageName, Map<String, Class> classMap) throws UnsupportedEncodingException {
        if (packageName == null || packageName.length() == 0 || classMap == null) return;
        // 文件路径，包级路径中的 . 要变为 /，例如 test.server  -- test/server
        packageName = packageName.replaceAll("\\.", "/");
        URL url = Thread.currentThread().getContextClassLoader().getResource(packageName);
        if (url == null) return;
        // 文件路径有可能存在中文, URL编码里没有中文，会导致找不到文件，这里要解码为 UTF-8
        File file = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
        File[] fileList = file.listFiles();
        if (fileList != null && fileList.length != 0) {
            for (File classFile : fileList) {
                // 还原包路径  test/server -- test.server
                String className = packageName.replaceAll("/", ".") + "." + classFile.getName().replaceAll("\\.class", "");
                try {
                    Class clazz = ClassLoader.getSystemClassLoader().loadClass(className);
                    RPCService rpcServiceAnnotation = (RPCService) clazz.getAnnotation(RPCService.class);
                    if (rpcServiceAnnotation != null) {
                        if (classMap.containsKey(rpcServiceAnnotation.value())) {
                            throw new ClassNameRepeatException("类名重复");
                        }
                        classMap.put(rpcServiceAnnotation.value(), clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, Class> getClassMap(String[] packageNames) {
        Map<String, Class> classMap = new HashMap<>(64);
        for (String packageName : packageNames) {
            try {
                putClass(packageName, classMap);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return classMap;
    }

}
