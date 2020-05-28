package com.jndv.avajxr2.bean;

public class BaseEntity<T> {
    private static int SUCCESS_CODE = 0;//成功的code
    private String reason;//请求成功或失败描述
    private int error_code;//错误码
    private T result;//数据集
    private String status;
    private String token;

    public boolean isSuccess() {
        return getError_code() == SUCCESS_CODE||getStatus()=="success";
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
