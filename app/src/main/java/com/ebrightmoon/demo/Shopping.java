package com.ebrightmoon.demo;

/**
 * Time: 2019-09-06
 * Author:wyy
 * Description:
 */
public class Shopping {


    /**
     * errorMessage : 采购单增减数量成功
     * errorCode : 1
     * baseResponse : {"code":0,"message":"SUCCESS","data":null}
     * token : 1f41c45931205bb9d8b65f945ba0d811
     */

    private String errorMessage;
    private int errorCode;
    private BaseResponseBean baseResponse;
    private String token;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public BaseResponseBean getBaseResponse() {
        return baseResponse;
    }

    public void setBaseResponse(BaseResponseBean baseResponse) {
        this.baseResponse = baseResponse;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class BaseResponseBean {
        /**
         * code : 0
         * message : SUCCESS
         * data : null
         */

        private int code;
        private String message;
        private Object data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    @Override
    public String toString() {
        return "Shopping{" +
                "errorMessage='" + errorMessage + '\'' +
                ", errorCode=" + errorCode +
                ", baseResponse=" + baseResponse +
                ", token='" + token + '\'' +
                '}';
    }
}
