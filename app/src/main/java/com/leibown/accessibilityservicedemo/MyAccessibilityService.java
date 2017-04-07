package com.leibown.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务功能
 */
public class MyAccessibilityService extends AccessibilityService {

    private List<AccessibilityNodeInfo> parents;

    private ButtonIdEntity entity;

    /**
     * 当启动服务的时候就会被调用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        parents = new ArrayList<>();

        entity = new ButtonIdEntity();
        String wechatVersion = Utils.getVersion(this);
        if (wechatVersion.equals("6.3.31")) {
            entity.setHongBaoClose(BtnIdConstans.hongBaoClose_6331);
            entity.setHongBaoDetailClose(BtnIdConstans.hongBaoDetailClose_6331);
            entity.setHongBaoOpen(BtnIdConstans.hongBaoOpen_6331);
        } else if (wechatVersion.equals("6.5.3")) {
            entity.setHongBaoClose(BtnIdConstans.hongBaoClose_653);
            entity.setHongBaoDetailClose(BtnIdConstans.hongBaoDetailClose_653);
            entity.setHongBaoOpen(BtnIdConstans.hongBaoOpen_653);
        }else if (wechatVersion.equals("6.5.4")) {
            entity.setHongBaoClose(BtnIdConstans.hongBaoClose_654);
            entity.setHongBaoDetailClose(BtnIdConstans.hongBaoDetailClose_654);
            entity.setHongBaoOpen(BtnIdConstans.hongBaoOpen_654);
        }else if (wechatVersion.equals("6.5.7")) {
            entity.setHongBaoClose(BtnIdConstans.hongBaoClose_657);
            entity.setHongBaoDetailClose(BtnIdConstans.hongBaoDetailClose_657);
            entity.setHongBaoOpen(BtnIdConstans.hongBaoOpen_657);
        }
    }


    private boolean isFromUser = true;//是否用户主动进入微信界面

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        int eventType = event.getEventType();
        Log.e("是否是用户点击进入微信", isFromUser + "");
        if (Constans.isStrongMode)
            isFromUser = false;
        //根据回调事件类型处理
        switch (eventType) {
            //通知栏发生变化时
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                Log.e("leibown:", "texts:" + texts.toString());
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (content.contains("[微信红包]")) {
                            //模拟打开通知栏消息，即打开微信
                            if (event.getParcelableData() != null &&
                                    event.getParcelableData() instanceof Notification) {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    isFromUser = false;
                                    pendingIntent.send();
                                    Log.e("demo", "进入微信");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.e("leibown:", "className:" + className);
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    //点击最后一个红包
                    Log.e("demo", "点击红包");
                    if (!isFromUser)
                        getLastPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                    //开红包
                    Log.e("demo", "开红包");
                    determineHongBaoIsToke(this, "手慢了，红包派完了");
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    //退出红包
                    Log.e("demo", "退出红包");
//                    isFromUser = true;
                    if (!isFromUser) {
                        inputClick(entity.getHongBaoDetailClose());
                        jumpToHome();
                    }
                }
                break;
            default:
                if (Constans.isStrongMode) {
                    getLastPacket();
                    Log.e("leibown", "进入强力模式");
                }
                break;
        }

    }

    /**
     * 返回主界面
     */
    private void jumpToHome() {
        Log.e("leibown", "跳回主界面");
        isFromUser = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        }, 1000);
    }

    /**
     * 判断红包是否被抢
     *
     * @param accessibilityService
     * @param text
     */
    private void determineHongBaoIsToke(AccessibilityService accessibilityService, String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }

        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        Log.e("leibown:", "nodeInfoList：" + nodeInfoList.size());
        if (!nodeInfoList.isEmpty()) {
            if (!isFromUser) {
                inputClick(entity.getHongBaoClose());
                jumpToHome();
            }
            Log.e("leibown:", "红包被抢了");
        } else {
            inputClick(entity.getHongBaoOpen());
            if (!isFromUser)
                jumpToHome();
            Log.e("leibown:", "红包没有被抢！！！");
        }
    }

    /**
     * 通过ID获取控件，并进行模拟点击
     *
     * @param clickId
     */
    private void inputClick(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);

            for (AccessibilityNodeInfo item : list) {
                Log.e("inputClick:", item.isClickable() + "");
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void getLastPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        recycle(nodeInfo);
        if (parents.size() > 0) {
            parents.get(parents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            jumpToHome();
        }
    }

    /**
     * 回归函数遍历每一个节点，并将含有"领取红包"存进List中
     *
     * @param info
     */
    private void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if (info.getText() != null) {
                if ("领取红包".equals(info.getText().toString())) {
                    if (info.isClickable()) {
                        Log.e("leibown:", "nodeInfo:" + info.getText().toString());
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    AccessibilityNodeInfo parent = info.getParent();
                    while (parent != null) {
                        if (parent.isClickable()) {
                            parents.add(parent);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }

    }


    @Override
    public void onInterrupt() {

    }
}
