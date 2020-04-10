package com.miss.tasks;

import android.text.TextUtils;
import android.widget.Toast;

import com.miss.base.utils.LogUtils;
import com.miss.launch.Task;

import cn.com.superLei.aoparms.AopArms;
import cn.com.superLei.aoparms.common.utils.ArmsPreference;

public class AopArmsTask extends Task {
    @Override
    public void run() {
        initAopArms();
    }
    private void initAopArms(){
        AopArms.init(mContext);
        AopArms.setInterceptor((key, methodName) -> {
            LogUtils.getInstance().e("intercept methodName:>>>>>"+methodName+ ">>>>>>key = "+key);
            if ("login_intercept".equals(key)){
                String userId = ArmsPreference.get(mContext, "userId", "");
                LogUtils.getInstance().e("userId = "+userId);
                if (TextUtils.isEmpty(userId)){
                    Toast.makeText(mContext, "您还没有登录", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });
    }

}
