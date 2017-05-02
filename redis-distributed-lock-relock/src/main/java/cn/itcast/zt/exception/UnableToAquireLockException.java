package cn.itcast.zt.exception;

/**
 * 异常类
 * Created by zhangtian on 2017/5/2.
 */
public class UnableToAquireLockException extends RuntimeException {
    public UnableToAquireLockException(){

    }

    public UnableToAquireLockException(String message){
        super(message);
    }

    public UnableToAquireLockException(String message, Throwable cause){
        super(message, cause);
    }
}
