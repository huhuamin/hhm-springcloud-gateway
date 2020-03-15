package com.huhuamin.req;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/15 13:59
 * @Description: http请求头，转对象
 */
public class ReqHeader {
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    //            | Header | Vaule | Remark |
//            | --- | --- | --- |
//            | X-Request-AppId |001 | 必填 开发系统是分配账号 |
//            | X-Request-AppSecret |0xxx3| 必填 开发系统是分配秘钥 |
//            | X-Request-deviceType | 1 |必填 [1-微信 2-WAP 3-Android 4-iOS 5-pc 6-手动创建 7-小程序] |
//            | X-Request-DeviceId | 342223 | 选填 设备id；3，4必填，即安卓和ios 的设备id |
//            | X-Request-Token | xx | 选填（/anonymous/可以必填）(/option/可填可不填) (其他场景是必传的) |
    /**
     * | X-Request-AppId |001 | 必填 开发系统是分配账号 |
     */
    private String appId;
    /**
     * | X-Request-AppSecret |0xxx3| 必填 开发系统是分配秘钥 |
     */
    private String appSecret;
    /**
     * | X-Request-deviceType | 1 |必填 [1-微信 2-WAP 3-Android 4-iOS 5-pc 6-手动创建 7-小程序] |
     */
    private Byte deviceType;
    /**
     * | X-Request-DeviceId | 342223 | 选填 设备id；3，4必填，即安卓和ios 的设备id |
     */
    private String deviceId;
    /**
     * | X-Request-Token | xx | 选填（/anonymous/可以必填）(/option/可填可不填) (其他场景是必传的) |
     */
    private String token;
    /**
     * X-Request-Token，转到下游 X-Request-CustId
     */
    private String custId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Byte deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
}
