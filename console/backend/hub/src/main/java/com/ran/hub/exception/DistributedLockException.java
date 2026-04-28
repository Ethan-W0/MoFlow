package com.ran.hub.exception;


public class DistributedLockException extends RuntimeException{
    private final String lockKey;
    private final LockErrorType errorType;


    public DistributedLockException(String lockKey , LockErrorType errorType , String message){
        super(message);
        this.lockKey = lockKey;
        this.errorType = errorType;
    }

    public DistributedLockException(String lockKey, LockErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.lockKey = lockKey;
        this.errorType = errorType;
    }
    public String getLockKey() {
        return lockKey;
    }

    public LockErrorType getErrorType() {
        return errorType;
    }

    public enum LockErrorType{
        // 获取锁超时
        ACQUIRE_TIMEOUT("Lock acquisition timeout"),
        //释放锁失败
        RELEASE_FAILED("Lock release failed"),
        //锁解析失败
        KEY_PARSE_FAILED("Lock key parsing failed"),
        REDIS_CONNECTION_ERROR("Redis connection error"),
        CONFIG_ERROR("Lock configuration error"),
        UNKNOWN_ERROR("Unknown error");


        private final String description;
        LockErrorType(String description){
            this.description = description;
        }
    }

    @Override
    public String toString() {
        return String.format("DistributedLockException{lockKey='%s', errorType=%s, message='%s'}", lockKey, errorType, getMessage());
    }
}
