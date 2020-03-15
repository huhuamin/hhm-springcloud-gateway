package com.huhuamin.service.check.impl;

import com.huhuamin.req.ReqHeader;
import com.huhuamin.result.JsonResult;
import com.huhuamin.service.ICheckRange;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 13:34
 * @Description: app 系统 网关校验
 */
public class AppCheckRangeService implements ICheckRange {
    /**
     * { 代理的方式传入公共的权限 @link AuthCheckService }
     */
    private ICheckRange delegateAuthCheck;

    public AppCheckRangeService(ICheckRange delegateAuthCheck) {
        this.delegateAuthCheck = delegateAuthCheck;
    }

    public AppCheckRangeService() {

    }

    @Override
    public boolean check(ReqHeader reqHeader, JsonResult jsonResult) {
        byte deviceType = reqHeader.getDeviceType();
        if (deviceType != 3 && deviceType != 4) {
            jsonResult.setMessage("X-Request-DeviceType:3-Android 4-iOS ");
            return false;
        }
        if (StringUtils.isEmpty(reqHeader.getDeviceId())) {
            jsonResult.setMessage("X-Request-DeviceType不能为空");
            return false;
        }

        boolean result = delegateAuthCheck.check(reqHeader, jsonResult);
        return result;

    }
}
