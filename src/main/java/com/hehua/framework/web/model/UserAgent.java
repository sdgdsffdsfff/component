package com.hehua.framework.web.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserAgent {

    private float coreVersion;

    private boolean tridentCore;

    private boolean webkitCore;

    private boolean geckoCore;

    private boolean prestoCore;

    private float version;

    private boolean safari;

    private boolean chrome;

    private boolean firefox;

    private boolean ie;

    private boolean opera;

    private String mobile;

    private boolean macOs;

    private boolean windowsOs;

    private boolean linuxOs;

    private boolean android;

    private boolean iphone;

    private boolean ipad;

    private boolean winPhone;

    private boolean isMicroMessenger; // 是否是微信

    private boolean isRenrenShareSlurp; // 人人UGC分享的爬虫

    private boolean supportWebp; // 是否支持webp

    public float getCoreVersion() {
        return coreVersion;
    }

    public void setCoreVersion(float coreVersion) {
        this.coreVersion = coreVersion;
    }

    public boolean isSupportWebp() {
        return supportWebp;
    }

    public void setSupportWebp(boolean supportWebp) {
        this.supportWebp = supportWebp;
    }

    public boolean isGeckoCore() {
        return geckoCore;
    }

    public void setGeckoCore(boolean geckoCore) {
        this.geckoCore = geckoCore;
    }

    public boolean isPrestoCore() {
        return prestoCore;
    }

    public void setPrestoCore(boolean prestoCore) {
        this.prestoCore = prestoCore;
    }

    public boolean isTridentCore() {
        return tridentCore;
    }

    public void setTridentCore(boolean tridentCore) {
        this.tridentCore = tridentCore;
    }

    public boolean isWebkitCore() {
        return webkitCore;
    }

    public void setWebkitCore(boolean webkitCore) {
        this.webkitCore = webkitCore;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public boolean isChrome() {
        return chrome;
    }

    public void setChrome(boolean chrome) {
        this.chrome = chrome;
    }

    public boolean isFirefox() {
        return firefox;
    }

    public void setFirefox(boolean firefox) {
        this.firefox = firefox;
    }

    public boolean isIe() {
        return ie;
    }

    public void setIe(boolean ie) {
        this.ie = ie;
    }

    public boolean isOpera() {
        return opera;
    }

    public void setOpera(boolean opera) {
        this.opera = opera;
    }

    public boolean isSafari() {
        return safari;
    }

    public void setSafari(boolean safari) {
        this.safari = safari;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isMacOs() {
        return macOs;
    }

    public void setMacOs(boolean macOs) {
        this.macOs = macOs;
    }

    public boolean isLinuxOs() {
        return linuxOs;
    }

    public void setLinuxOs(boolean linuxOs) {
        this.linuxOs = linuxOs;
    }

    public boolean isWindowsOs() {
        return windowsOs;
    }

    public void setWindowsOs(boolean windowsOs) {
        this.windowsOs = windowsOs;
    }

    public boolean isAndroid() {
        return android;
    }

    public void setAndroid(boolean android) {
        this.android = android;
    }

    public boolean isIphone() {
        return iphone;
    }

    public void setIphone(boolean iphone) {
        this.iphone = iphone;
    }

    public boolean isIpad() {
        return ipad;
    }

    public void setIpad(boolean ipad) {
        this.ipad = ipad;
    }

    public boolean isWinPhone() {
        return winPhone;
    }

    public void setWinPhone(boolean winPhone) {
        this.winPhone = winPhone;
    }

    public boolean isMicroMessenger() {
        return isMicroMessenger;
    }

    public void setMicroMessenger(boolean isMicroMessenger) {
        this.isMicroMessenger = isMicroMessenger;
    }

    public boolean isRenrenShareSlurp() {
        return isRenrenShareSlurp;
    }

    public void setRenrenShareSlurp(boolean isRenrenShareSlurp) {
        this.isRenrenShareSlurp = isRenrenShareSlurp;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
