package exception;

/**
 * 类名重复异常（为了能够准确定位某一个方法，提供服务的类名不允许重复）
 * @author JayFeng
 * @date 2021/2/19
 */
public class ClassNameRepeatException extends RuntimeException {

    public ClassNameRepeatException(String msg) {
        super(msg);
    }

}
