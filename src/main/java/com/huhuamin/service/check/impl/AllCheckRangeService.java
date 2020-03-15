package com.huhuamin.service.check.impl;

import com.huhuamin.req.ReqHeader;
import com.huhuamin.result.JsonResult;
import com.huhuamin.service.ICheckRange;
import org.apache.commons.lang3.StringUtils;


/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 13:35
 * @Description: html+app 网关校验，小程序，wap，公共号，统称 web
 */
public class AllCheckRangeService implements ICheckRange {
    /**
     * { 代理的方式传入公共的权限 @link AuthCheckService }
     */
    private ICheckRange delegateAuthCheck;


    public AllCheckRangeService(ICheckRange delegateAuthCheck) {
        this.delegateAuthCheck = delegateAuthCheck;
    }

    public AllCheckRangeService() {

    }


    @Override
    public boolean check(ReqHeader reqHeader, JsonResult jsonResult) {
        byte deviceType = reqHeader.getDeviceType();
        if (deviceType < 1 && deviceType > 7) {
            jsonResult.setMessage("X-Request-DeviceType:1-微信 2-WAP 3-Android 4-iOS 5-pc 6-手动创建 7-小程序 ");
            return false;
        }
        if (deviceType == 3 || deviceType == 4) {
            if (StringUtils.isEmpty(reqHeader.getDeviceId())) {
                jsonResult.setMessage("X-Request-DeviceId不能为空");
                return false;
            }
        }
        boolean result = delegateAuthCheck.check(reqHeader, jsonResult);
        return result;
    }
}
