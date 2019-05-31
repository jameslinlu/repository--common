package com.commons.metadata.exception;


import com.commons.metadata.code.ResultCode;

public class ServiceException extends Exception {
    public ServiceException() {
        super();
    }

    /**
     * 异常码  shield.clean.failed
     */
    private String exceptionCode;
    /**
     * 描述型异常码 shield.clean.ip.running = 在message配置  防护IP:{0} 已经在运行中
     */
    private String exceptionDescCode;
    /**
     * 描述型异常码对应内容
     */
    private String[] exceptionDescValue;// ["192.168.1.1"]

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionDescCode() {
        return exceptionDescCode;
    }

    public void setExceptionDescCode(String exceptionDescCode) {
        this.exceptionDescCode = exceptionDescCode;
    }

    public String[] getExceptionDescValue() {
        return exceptionDescValue;
    }

    public void setExceptionDescValue(String[] exceptionDescValue) {
        this.exceptionDescValue = exceptionDescValue;
    }

    public ServiceException(String message) {
        super(message);
        this.setExceptionCode(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
        if (isCausedBy(cause, ServiceException.class)) {
            this.setExceptionCode(((ServiceException) cause).getExceptionCode());
            this.setExceptionDescCode(((ServiceException) cause).getExceptionDescCode());
            this.setExceptionDescValue(((ServiceException) cause).getExceptionDescValue());
        } else {
            this.setExceptionCode(ResultCode.ERROR_INNER);
        }

    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.setExceptionCode(message);
        if (isCausedBy(cause, ServiceException.class)) {
            this.setExceptionCode(((ServiceException) cause).getExceptionCode());
            this.setExceptionDescCode(((ServiceException) cause).getExceptionDescCode());
            this.setExceptionDescValue(((ServiceException) cause).getExceptionDescValue());
        }
    }

    public ServiceException(String message, String descCode, String[] descValues) {
        super(message);
        this.setExceptionCode(message);
        this.setExceptionDescCode(descCode);
        this.setExceptionDescValue(descValues);
    }

    public ServiceException(String message, String descCode, String[] descValues, Throwable cause) {
        super(message, cause);
        this.setExceptionCode(message);
        this.setExceptionDescCode(descCode);
        this.setExceptionDescValue(descValues);
    }


    /**
     * 判断底层异常工具方法
     *
     * @param ex
     * @param causeExceptionClasses
     * @return
     */
    public boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex.getCause();
        return isCausedBy(cause, causeExceptionClasses);
    }

    public boolean isCausedBy(Throwable cause, Class<? extends Exception>... causeExceptionClasses) {
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
}
