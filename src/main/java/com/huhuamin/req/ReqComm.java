package com.huhuamin.req;

/**
 * 公共业务模块
 */
public class ReqComm {

    private String deviceId;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 来源[1-微信 2-WAP 3-Android 4-iOS 5-pc]
     */
    ///来源[1-微信 2-WAP 3-Android 4-iOS 5-pc]
    private Byte registerSource;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * 来源[1-微信 2-WAP 3-Android 4-iOS 5-pc]
     *
     * @return
     */
    public Byte getRegisterSource() {
        return registerSource;
    }

    /**
     * 来源[1-微信 2-WAP 3-Android 4-iOS 5-pc]
     *
     * @param registerSource
     */
    public void setRegisterSource(Byte registerSource) {
        this.registerSource = registerSource;
    }
}
