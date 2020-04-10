package com.miss.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.base.toast.ToastUtil;
import com.miss.base.utils.LogUtils;
import com.miss.base.utils.ResourceUtils;
import com.miss.finger.FPerException;
import com.miss.finger.FingerPrinter;
import com.miss.picker.CityPickerDialog;
import com.miss.picker.OnePickerDialog;
import com.miss.picker.address.City;
import com.miss.picker.address.County;
import com.miss.picker.address.Province;
import com.miss.picker.wheel.adapter.AbstractWheelTextAdapter;
import com.miss.progress.HorizontalProgressView;
import com.miss.tooltips.ToolTip;
import com.miss.tooltips.ToolTipsManager;
import com.miss.view.AlignTextView;
import com.miss.view.AlignTextView2;
import com.miss.view.CommonHeader;
import com.miss.view.NoticeView;
import com.miss.view.TagsView;
import com.miss.view.TimeLineView;
import com.miss.view.VerificationCode;
import com.miss.view.dialog.CustomDialogBuilder;
import com.miss.view.segment.SegmentedControlItem;
import com.miss.view.segment.SegmentedControlView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * 一些小部件的页面
 */
public class WidgetActivity extends BaseActivity implements TagsView.TagsEditListener, ToolTipsManager.TipListener {
    public static String[] notices = new String[]{
            "伪装者:胡歌演绎'痞子特工'",
            "无心法师:生死离别!月牙遭虐杀",
            "花千骨:尊上沦为花千骨",
            "综艺饭:胖轩偷看夏天洗澡掀波澜",
            "碟中谍4:阿汤哥高塔命悬一线,超越不可能",
    };
    RelativeLayout rootLayout;
    NoticeView vNotice;
    TagsView mTagsView;
    ImageView verifyCode;
    String realCode = "";

    ToolTipsManager mToolTipsManager;
    @ToolTip.Align int mAlign = ToolTip.ALIGN_CENTER;
    TextView tipsView;
    TextView countDownTimeTv;

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        StatusBarUtil.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary));//设置背景颜色
        CommonHeader commonHeader = findViewById(R.id.common_header);
        commonHeader.setTitle("小部件");
        //通知栏的效果
        vNotice = (NoticeView) findViewById(R.id.notice);
        vNotice.start(Arrays.asList(notices));
        vNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WidgetActivity.this, notices[vNotice.getIndex()], Toast.LENGTH_SHORT).show();
            }
        });

       //TagsView
        mTagsView = findViewById(R.id.tags_view);
        mTagsView.setHint("请输入喜欢的类型");
        mTagsView.setHintTextSize(12);
        mTagsView.setTagsListener(this);
        mTagsView.setTagsWithSpacesEnabled(true);
        /*mTagsView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new String[]{"蘋果","鳳梨"}));
        mTagsView.setThreshold(1);*/

        //验证码
        verifyCode = findViewById(R.id.code);
        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
                realCode=VerificationCode.getInstance().getCode().toLowerCase();
            }
        });
        verifyCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();
        //分段视图
        SegmentedControlView mScv1 = findViewById(R.id.scv1);
        SegmentedControlView mScv2 = findViewById(R.id.scv2);
        SegmentedControlView mScv3 = findViewById(R.id.scv3);

        List<SegmentedControlItem> items = new ArrayList<>();
        items.add(new SegmentedControlItem("Yesterday"));
        items.add(new SegmentedControlItem("Today"));
        items.add(new SegmentedControlItem("Tomorrow"));
        mScv1.addItems(items);

        mScv2.addItems(items);
        mScv3.addItems(items);

        mScv1.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "selected:%d", position);
                LogUtils.getInstance().e(msg);
            }
        });

        mScv3.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                String msg = String.format(Locale.getDefault(), "selected:%d", position);
                LogUtils.getInstance().e(msg);
            }
        });
        //tooltips
        rootLayout = findViewById(R.id.root_view);
        tipsView = findViewById(R.id.tips_view);
        mToolTipsManager = new ToolTipsManager(this);
        //countDownTime
        countDownTimeTv = findViewById(R.id.countTimer);
        new CountDownTimer(30000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                countDownTimeTv.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                countDownTimeTv.setText("done");
            }
        }.start();
        //时间轴
        TimeLineView timeLineView = findViewById(R.id.timeLineView);
        List<String> lineList = new ArrayList<>();
        lineList.add("step1");
        lineList.add("step2");
        lineList.add("step3");
        lineList.add("step4");
        lineList.add("step5");
        lineList.add("step6");
        timeLineView.setPointStrings(lineList,3.5f);
        //HorizontalProgressView
        HorizontalProgressView horizontalProgressView = findViewById(R.id.hpv);
        TextView progressTv = findViewById(R.id.progress_text);
        horizontalProgressView.startProgressAnimation();
        horizontalProgressView.setProgressViewUpdateListener(new HorizontalProgressView.HorizontalProgressUpdateListener() {
            @Override
            public void onHorizontalProgressStart(View view) {

            }

            @Override
            public void onHorizontalProgressUpdate(View view, float progress) {
                int progressInt = (int) progress;
                progressTv.setText(progressInt + "%");
            }

            @Override
            public void onHorizontalProgressFinished(View view) {

            }
        });
        //Picker组件
        initCityData();

        //自适应 TextView
        AlignTextView alignTextView = findViewById(R.id.alignTextView);
        AlignTextView2 alignTextView2 = findViewById(R.id.alignTextView2);
        String text = "Hooray! It's snowing! It's time to make a snowman.James runs out. He makes a big pile of snow. He puts a big snowball on top. He adds a scarf and a hat. He adds an orange for the nose. He adds coal for the eyes and buttons.In the evening, James opens the door. What does he see? The snowman is moving! James invites him in. The snowman has never been inside a house. He says hello to the cat. He plays with paper towels.A moment later, the snowman takes James's hand and goes out.They go up, up, up into the air! They are flying! What a wonderful night!The next morning, James jumps out of bed. He runs to the door.He wants to thank the snowman. But he's gone.";
        String text1 = "AppCompatActivity AppCompatActivityActivityActivityActivity";
        String text2 = "For every layout expression, there is a binding adapter that makes the framework calls required to set the corresponding properties or listeners. For example, the binding adapter can take care of calling the setText() method to set the text property or call the setOnClickListener() method to add a listener to the click event. The most common binding adapters, such as the adapters for the android:text property used in the examples in this page, are available for you to use in the android.databinding.adapters package. For a list of the common binding adapters, see adapters. You can also create custom adapters, as shown in the following example:";

        alignTextView.setText(text2);
        alignTextView2.setText(text2);
        //
    }
    private ArrayList<Province> provinces = new ArrayList<Province>();
    /**
     * 初始化城市数据
     */
    private void initCityData() {
        String address = ResourceUtils.readAssets2String(this, "address.json", "utf-8");
        if (!TextUtils.isEmpty(address)){
            try {
                JSONArray jsonList = new JSONArray(address);
                Gson gson = new Gson();
				for (int i = 0; i < jsonList.length(); i++) {
				    provinces.add(gson.fromJson(jsonList.getString(i), Province.class));
				}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTagsChanged(Collection<String> tags) {
        LogUtils.getInstance().e(Arrays.toString(tags.toArray()));
    }

    @Override
    public void onEditingFinished() {

    }

    @Override
    public void onTipDismissed(View view, int anchorViewId, boolean byUser) {

    }
    //tooltips
    public void tooltipsClick(View view){
        //mToolTipsManager.findAndDismiss(tipsView);
        ToolTip.Builder builder = new ToolTip.Builder(this, tipsView, rootLayout, "測試tips", ToolTip.POSITION_BELOW);
        builder.setAlign(mAlign);
        builder.setTextAppearance(R.style.TooltipTextAppearance);
        //builder.setTypeface(mCustomFont);//mCustomFont = Typeface.createFromAsset(getAssets(), "Pacifico-Regular.ttf");
        builder.setBackgroundColor(getResources().getColor(R.color.colorOrange));
        mToolTipsManager.show(builder.build());
        ToastUtil.show(this,"tips");
        /*CharSequence initialGuideTex = Html.fromHtml("Click on the <a href='#'>Buttons</a> " +
                "and the <a href='#'>Radio Buttons</a> bellow to test various tool tip configurations." +
                "<br><br><font color='grey'><small>GOT IT</small></font>");

        ToolTip.Builder builder = new ToolTip.Builder(this, tipsView, rootLayout, initialGuideTex, ToolTip.POSITION_ABOVE);
        builder.setAlign(mAlign);
        builder.setBackgroundColor(Color.DKGRAY);
        builder.setTextAppearance(R.style.TooltipTextAppearance);
        mToolTipsManager.show(builder.build());*/
    }

    public void showCustomDialog(View view){
        CustomDialogBuilder builder = new CustomDialogBuilder(this);
        builder.setTitle("查找附近wifi网络");
        builder.setMessage("当您开始阅读本书时，人类已经进入了二十一世纪。");
        builder.setNegativeButton("取消", new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                builder.dismiss();
            }

        });
        builder.setPositiveButton("确认", new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                builder.dismiss();
            }

        });
        builder.setPositiveNormalTextColor(0xFF48CE3A);
        builder.setPositivePressedTextColor(0xFF48CE3A);
        builder.setBackgroundAlpha(160);
        builder.show();
    }
    public void singleDatePicker(View view) {
        final ArrayList<String> years = new ArrayList<String>();
        years.add("2005年");
        years.add("2006年");
        years.add("2007年");
        years.add("2008年");
        years.add("2009年");
        years.add("2010年");
        years.add("2011年");
        years.add("2012年");
        years.add("2013年");
        years.add("2014年");
        years.add("2015年");
        years.add("2016年");
        years.add("2017年");
        years.add("2018年");
        AbstractWheelTextAdapter adapter = new AbstractWheelTextAdapter(WidgetActivity.this,
                com.miss.picker.R.layout.wheel_text) {
            @Override
            public int getItemsCount() {
                return years.size();
            }
            @Override
            protected CharSequence getItemText(int index) {
                return years.get(index);
            }
        };
        OnePickerDialog picker = new OnePickerDialog(WidgetActivity.this,adapter , new OnePickerDialog.onSelectListener() {
            @Override
            public void onSelect(AbstractWheelTextAdapter adapter, int position) {
                //((Button)view).setText(years.get(position));
                LogUtils.getInstance().e(years.get(position));
            }
        });
        picker.show();
    }
    /**
     * 省市区 三级联动
     * @param view
     */
    public void areaPicker(View view) {
        new CityPickerDialog(WidgetActivity.this, provinces, null, null, null,
                new CityPickerDialog.onCityPickedListener() {
                    @Override
                    public void onPicked(Province selectProvince,
                                         City selectCity, County selectCounty) {
                        StringBuilder address = new StringBuilder();
                        address.append(
                                selectProvince != null ? selectProvince
                                        .getAreaName() : "")
                                .append(selectCity != null ? selectCity
                                        .getAreaName() : "")
                                .append(selectCounty != null ? selectCounty
                                        .getAreaName() : "");
                        String text = selectCounty != null ? selectCounty
                                .getAreaName() : "";
                        //((Button)view).setText(address);
                        LogUtils.getInstance().e(address.toString());
                    }
                }).show();
    }
    private int fingerErrorNum = 0;
    /**
     * 指纹识别，验证
     */
    public void fingerPrinter(View view){
        FingerPrinter.onCreate().setFingerPrinterListener(this,new FingerPrinter.FingerPrinterListener() {
            @Override
            public void onNext(boolean next) {
                if(next){
                    ToastUtil.show(WidgetActivity.this,"验证通过");
                }else{
                    fingerErrorNum++;
                    ToastUtil.show(WidgetActivity.this,"指纹识别失败，还剩" + (5-fingerErrorNum) + "次机会");
                }
            }

            @Override
            public void onError(FPerException e) {
                if(e instanceof FPerException){
                    Toast.makeText(WidgetActivity.this,((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
