package com.huhuamin.service.check.impl;

import com.huhuamin.jedis.RedisService;
import com.huhuamin.req.ReqHeader;
import com.huhuamin.result.JsonResult;
import com.huhuamin.service.ICheckRange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;


/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 14:25
 * @Description: 权限认证 appId,appSecret
 */

public class AuthCheckService implements ICheckRange {

    private ObjectProvider<RedisService> redisService;
    /**
     * 多个 可以用list
     */
    @Value("${sys.appId}")
    private String appId;
    @Value("${sys.appSecret}")
    private String appSecret;

    public AuthCheckService() {
    }

    public AuthCheckService(ObjectProvider<RedisService> redisService) {
        this.redisService = redisService;
    }

    public boolean check(ReqHeader reqHeader, JsonResult jsonResult) {
        /**
         * 系统级别的appId和appSecret 校验
         */
        if (StringUtils.isEmpty(reqHeader.getAppId()) || StringUtils.isEmpty(reqHeader.getAppSecret()) || (!reqHeader.getAppId().equals(appId) || !reqHeader.getAppSecret().equals(appSecret))) {
            jsonResult.setMessage("appId与appSecret不匹配");
            return false;
        }
        if (StringUtils.isNotEmpty(reqHeader.getToken())) {
            return checkDie(reqHeader.getToken(), jsonResult);
        } else {
            if (!reqHeader.getUri().contains("/option")) {
                jsonResult.setMessage("token 不能为空");
                return false;
            }
        }

        return true;
    }

    /**
     * 登录鉴权
     *
     * @param token
     * @param jsonResult
     * @return
     */
    private boolean checkDie(String token, JsonResult jsonResult) {
        String custId = redisService.getIfAvailable().getCustIdByToken(token);
        if (StringUtils.isEmpty(custId)) {
            jsonResult.setMessage("token过期，重新登录");
            jsonResult.setCode(301);
            return false;
        }
        if (custId.equalsIgnoreCase("303")) {
            jsonResult.setMessage("账户在其他设备登录!");
            jsonResult.setCode(303);
            return false;

        }
        //下游系统自己要判断 cust是否被都结了
//        Byte freezeStatue = customerMapper.getIfAvailable().selectFreezeStatusByCustId(custId);
//        if (null != freezeStatue && freezeStatue.intValue() == 3) {
//            // 未查询到账号
//            jsonResult.setStatusCode(false);
//            jsonResult.setCode(304);
//            jsonResult.setMessage("无使用权限，注:您的使用权限已被冻结，请联系客服");
//            return true;
//        }
        jsonResult.setCustId(custId);

        return true;
    }
}
