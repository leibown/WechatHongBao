package com.leibown.accessibilityservicedemo;

/**
 * Created by Administrator on 2017/1/4.
 */

public class ButtonIdEntity {

    private String hongBaoClose = "com.tencent.mm:id/bed";//6.5.3版本红包的关闭按钮

    private String hongBaoOpen = "com.tencent.mm:id/be_";//6.5.3版本红包开字按钮

    private String hongBaoDetailClose = "com.tencent.mm:id/gs";//6.5.3版本红包详情界面关闭按钮

    public ButtonIdEntity() {
    }

    public ButtonIdEntity(String hongBaoClose, String hongBaoOpen, String hongBaoDetailClose) {
        this.hongBaoClose = hongBaoClose;
        this.hongBaoOpen = hongBaoOpen;
        this.hongBaoDetailClose = hongBaoDetailClose;
    }

    public String getHongBaoClose() {
        return hongBaoClose;
    }

    public void setHongBaoClose(String hongBaoClose) {
        this.hongBaoClose = hongBaoClose;
    }

    public String getHongBaoOpen() {
        return hongBaoOpen;
    }

    public void setHongBaoOpen(String hongBaoOpen) {
        this.hongBaoOpen = hongBaoOpen;
    }

    public String getHongBaoDetailClose() {
        return hongBaoDetailClose;
    }

    public void setHongBaoDetailClose(String hongBaoDetailClose) {
        this.hongBaoDetailClose = hongBaoDetailClose;
    }
}
