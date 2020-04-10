package com.miss.ui;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.dialog.lemonbubble.LemonBubble;
import com.miss.dialog.lemonbubble.LemonBubbleGlobal;
import com.miss.dialog.lemonbubble.enums.LemonBubbleLayoutStyle;
import com.miss.dialog.lemonbubble.enums.LemonBubbleLocationStyle;
import com.miss.dialog.lemonhello.LemonHello;
import com.miss.dialog.lemonhello.LemonHelloAction;
import com.miss.dialog.lemonhello.LemonHelloGlobal;
import com.miss.dialog.lemonhello.LemonHelloInfo;
import com.miss.dialog.lemonhello.LemonHelloView;
import com.miss.dialog.lemonhello.interfaces.LemonHelloActionDelegate;
import com.miss.view.CommonHeader;

public class LemonActivity extends BaseActivity {
    private LinearLayout btn_success;
    private LinearLayout btn_error;
    private LinearLayout btn_warning;
    private LinearLayout btn_information;
    private LinearLayout btn_bookmark;
    private LinearLayout btn_multiMessages;
    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lemon);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        CommonHeader commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("弹窗");

        LemonHelloGlobal.statusBarColor = Color.parseColor("#3399FF");
        LemonBubbleGlobal.statusBarColor = Color.parseColor("#3399FF");

        btn_success = (LinearLayout) findViewById(R.id.btn_success);
        btn_error = (LinearLayout) findViewById(R.id.btn_error);
        btn_warning = (LinearLayout) findViewById(R.id.btn_warning);
        btn_information = (LinearLayout) findViewById(R.id.btn_information);
        btn_bookmark = (LinearLayout) findViewById(R.id.btn_bookmark);
        btn_multiMessages = (LinearLayout) findViewById(R.id.btn_multiMessages);

        initFunctions();
    }
    private void initFunctions() {

        // 成功按钮被点击
        btn_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LemonHello.getSuccessHello("提交成功", "恭喜您，您所填写的数据已经全部提交成功，我们的客服人员将在24小时内进行审核，请耐心等待.")
                        .addAction(new LemonHelloAction("我知道啦", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .show(LemonActivity.this);
            }
        });

        btn_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LemonHello.getErrorHello("发生错误", "对不起，您没有权限删除此数据，请联系系统管理员进行操作，谢谢。")
                        .addAction(new LemonHelloAction("关闭", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .show(LemonActivity.this);
            }
        });

        btn_warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LemonHello.getWarningHello("您确认删除这条数据吗？", "删除这条数据后会同时删除其关联的数据，并且无法撤销！")
                        .addAction(new LemonHelloAction("取消", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("确定删除", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();

                                // 提示框使用了LemonBubble，请您参考：https://github.com/1em0nsOft/LemonBubble4Android
                                LemonBubble.showRoundProgress(LemonActivity.this, "正在删除中...");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        LemonBubble.showRight(LemonActivity.this, "删除成功", 1000);
                                    }
                                }, 2000);
                            }
                        }))
                        .show(LemonActivity.this);
            }
        });

        btn_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LemonHello.getInformationHello("您确定要注销吗？", "注销登录后您将无法接收到当前用户的所有推送消息。")
                        .addAction(new LemonHelloAction("取消", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("我要注销", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                                // 提示框使用了LemonBubble，请您参考：https://github.com/1em0nsOft/LemonBubble4Android
                                LemonBubble.getRoundProgressBubbleInfo()
                                        .setLocationStyle(LemonBubbleLocationStyle.BOTTOM)
                                        .setLayoutStyle(LemonBubbleLayoutStyle.ICON_LEFT_TITLE_RIGHT)
                                        .setBubbleSize(200, 50)
                                        .setProportionOfDeviation(0.1f)
                                        .setTitle("正在请求服务器...")
                                        .show(LemonActivity.this);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        LemonBubble.showRight(LemonActivity.this, "注销成功，欢迎您下次登录", 2000);
                                    }
                                }, 1500);
                            }
                        }))
                        .show(LemonActivity.this);
            }
        });
        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LemonHelloInfo bookMarkInfo = new LemonHelloInfo()
                        .setTitle("添加书签")
                        .setContent("确认将《LemonKit》添加到您的书签当中吗？")
                        .setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_bookmark))
                        .addAction(new LemonHelloAction("取消", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("我再想想", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("添加", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                                LemonBubble.showRight(LemonActivity.this, "添加成功", 1500);
                            }
                        }));
                bookMarkInfo.show(LemonActivity.this);
            }
        });

        btn_multiMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LemonHelloInfo info1 = new LemonHelloInfo()
                        .setTitle("LemonKit想要获取您的位置")
                        .setContent("LemonKit获取到您的位置之后将会动态记录您的地理信息。")
                        .addAction(new LemonHelloAction("允许", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("拒绝", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }));
                LemonHelloInfo info2 = new LemonHelloInfo()
                        .setTitle("LemonKit想要访问数据")
                        .setContent("LemonKit希望使用蜂窝网络或者WLAN进行远程数据获取。")
                        .addAction(new LemonHelloAction("允许", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("拒绝", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }));
                LemonHelloInfo info3 = new LemonHelloInfo()
                        .setTitle("LemonKit想要推送通知")
                        .setContent("LemonKit将要获取通知权限，在适当的时候会向您推送一些通知。")
                        .addAction(new LemonHelloAction("允许", new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }))
                        .addAction(new LemonHelloAction("拒绝", Color.RED, new LemonHelloActionDelegate() {
                            @Override
                            public void onClick(LemonHelloView helloView, LemonHelloInfo helloInfo, LemonHelloAction helloAction) {
                                helloView.hide();
                            }
                        }));
                info1.show(LemonActivity.this);
                info2.show(LemonActivity.this);
                info3.show(LemonActivity.this);
            }
        });

    }
}
