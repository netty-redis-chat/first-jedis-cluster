package com.wangzai.nettywebsocket.error;

public class BusinessException extends Exception implements CommonError {
    //包装模式？

    CommonError commonError;

    public BusinessException(CommonError commonError) {
        this.commonError = commonError;
    }

    public BusinessException(CommonError commonError,String errMsg) {
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        commonError.setErrMsg(errMsg);
        return this;
    }
}
