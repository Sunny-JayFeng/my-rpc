package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于标识哪些包存在RPC服务类，需要被扫描
 * @author JayFeng
 * @date 2021/2/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCServicePackage {

    String[] value(); // 包名

}
