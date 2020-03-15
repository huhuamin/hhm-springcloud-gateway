package com.huhuamin.service.check.impl;

import com.huhuamin.req.ReqHeader;
import com.huhuamin.result.JsonResult;
import com.huhuamin.service.ICheckRange;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 13:35
 * @Description: web 系统 网关 小程序，wap，公共号，统称 web
 */
public class WebCheckRangeService implements ICheckRange {
    /**
     * { 代理的方式传入公共的权限 @link AuthCheckService }
     */
    private ICheckRange delegateAuthCheck;

    public WebCheckRangeService(ICheckRange delegateAuthCheck) {
        this.delegateAuthCheck = delegateAuthCheck;
    }

    public WebCheckRangeService() {
    }

    @Override
    public boolean check(ReqHeader reqHeader, JsonResult jsonResult) {
        byte deviceType = reqHeader.getDeviceType();
        if (deviceType != 1 && deviceType != 2 && deviceType != 5 && deviceType != 7) {
            jsonResult.setMessage("X-Request-DeviceType:1-微信 2-WAP  5-pc 7-小程序");
            return false;
        }
        boolean result = delegateAuthCheck.check(reqHeader, jsonResult);
        return result;
    }
}
