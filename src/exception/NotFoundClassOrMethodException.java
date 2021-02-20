package exception;

/**
 * 无法找到类或方法异常
 */
public class NotFoundClassOrMethodException extends RuntimeException {

    public NotFoundClassOrMethodException(String msg) {
        super(msg);
    }

}
