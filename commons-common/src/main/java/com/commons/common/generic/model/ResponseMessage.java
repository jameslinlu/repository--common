package com.commons.common.generic.model;


import com.commons.common.utils.ContextUtil;
import com.commons.common.utils.StringUtil;
import com.commons.metadata.code.ResultCode;
import com.commons.metadata.exception.ServiceException;
import com.commons.metadata.model.Page;

import java.io.Serializable;
import java.util.Arrays;

public class ResponseMessage implements Serializable {
    /**
     * 接口返回码
     */
    private String code = "";

    /**
     * 结果对应描述
     */
    private String message = "";

    /**
     * 返回结果
     */
    private Object body;

    /**
     * 构造函数 ， 默认消息为成功
     */
    public ResponseMessage() {
        this.code = ResultCode.SUCCESS;
        processCode(this.code, null, null);
    }

    /**
     * 构造函数
     */
    public ResponseMessage(Exception e) {
        if (e instanceof ServiceException) {
            this.code = ((ServiceException) e).getExceptionCode();
            processCode(this.code, ((ServiceException) e).getExceptionDescCode(), ((ServiceException) e).getExceptionDescValue());
        } else {
            this.code = ResultCode.ERROR_INNER;
            processCode(this.code, null, null);
        }
    }

    /**
     * 构造函数 ， 默认消息为成功
     *
     * @param body 消息体
     */
    public ResponseMessage(Object body) {
        this.code = ResultCode.SUCCESS;
        this.body = body;
        processCode(this.code, null, null);
    }

    /**
     * 构造函数
     *
     * @param resultCode 异常类型
     */
    public ResponseMessage(String resultCode) {
        this.code = resultCode;
        processCode(this.code, null, null);
    }

    public ResponseMessage(String resultCode, Object body) {
        this.code = resultCode;
        this.body = body;
        processCode(this.code, null, null);
    }


    public boolean isSuccess() {
        return code != null && code.equals("000000");
    }

    /**
     * 取得result
     *
     * @return 返回result。
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置result
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 取得message
     *
     * @return 返回message。
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置message
     *
     * @param message 要设置的message。
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 取得body
     *
     * @return 返回body。
     */
    public Object getBody() {
        return body;
    }

    /**
     * 设置body
     *
     * @param body 要设置的body。
     */
    public void setBody(Object body) {
        this.body = body;
    }

    public void setBody(Page<?> page) {
        this.body = page;
    }

    @Override
    public String toString() {
        return "ResponseMessage [code=" + code + ", message=" + message + ", body=" + body + "]";
    }


    private String[] findMessageByCode(String code) {
        String message[] = null;
        try {
            message = ContextUtil.getMessage(code).split("\\|");
        } catch (Exception e) {
            return null;
        }
        return message;
    }

    private void processCode(String code, String descCode, String[] descValue) {
        if (this.message != null && !this.message.isEmpty()) {
            return;
        }
        if (!StringUtil.isEmpty(code) && code.matches("(\\w+(\\.*)\\w+)*")) {
            String[] message = this.findMessageByCode(code);
            if (message == null) {
                return;
            }
            this.message = message[0];
            this.code = message[1];
            //处理扩展异常描述
            // 如  登录失败 为主描述   您的账号已被锁定需拨打13813888988 为详细描述，号码由服务器返回
            // 详细描述是为了描述内容返回值由服务器决定,若无需服务器决定则使无需使用
            if (!StringUtil.isEmpty(descCode) && code.matches("(\\w+(\\.*)\\w+)*")) {
                message = this.findMessageByCode(descCode);
                if (message == null) {
                    return;
                }
                try {
                    if (descValue == null) {
                        this.message += message[0];
                    }
                    if (descValue != null) {
                        this.message += String.format(message[0], descValue);
                    }
                } catch (Exception e) {
                    this.message += " format " + message[0] + " fail ,please check it!";
                }
                //以扩展信息异常code排查
                this.code = message[1];
            }

        } else {
            this.message = this.code;
            this.code = "-1";
        }
    }

}
