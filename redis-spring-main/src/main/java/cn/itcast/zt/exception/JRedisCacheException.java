package cn.itcast.zt.exception;

/**
 * Created by zhangtian on 2017/4/25.
 */
public class JRedisCacheException extends RuntimeException {
    public JRedisCacheException() {
        super();
    }

    public JRedisCacheException(String message) {
        super(message);
    }

    public JRedisCacheException(Throwable cause) {
        super(cause);
    }
}
