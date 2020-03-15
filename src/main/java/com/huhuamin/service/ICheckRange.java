package com.huhuamin.service;

import com.huhuamin.req.ReqHeader;
import com.huhuamin.result.JsonResult;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 13:31
 * @Description:
 */
public interface ICheckRange {
//            | Header | Vaule | Remark |
//            | --- | --- | --- |
//            | X-Request-AppId |001 | 必填 开发系统是分配账号 |
    //            | X-Request-AppSecret |0xxx3| 必填 开发系统是分配秘钥 |
//            | X-Request-deviceType | 1 |必填 [1-微信 2-WAP 3-Android 4-iOS 5-pc 6-手动创建 7-小程序] |
//            | X-Request-DeviceId | 342223 | 选填 设备id；3，4必填，即安卓和ios 的设备id |
//            | X-Request-Token | xx | 选填（/anonymous/可以必填）(/option/可填可不填) (其他场景是必传的) |
    boolean check(ReqHeader reqHeader, JsonResult jsonResult);
}
