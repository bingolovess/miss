package com.miss.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.miss.R;
import com.miss.base.BaseActivity;
import com.miss.base.statusbar.StatusBarUtil;
import com.miss.fragment.ChatFragment;
import com.miss.fragment.ContactFragment;
import com.miss.fragment.ProfileFragment;
import com.miss.view.TabView;
import com.nineoldandroids.view.ViewHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tab_weixin)
    TabView mTabWeixin;

    @BindView(R.id.tab_contact)
    TabView mTabContact;

    @BindView(R.id.tab_profile)
    TabView mTabProfile;
    private List<TabView> mTabViews = new ArrayList<>();
    private Map<Integer,Fragment> maps = new HashMap<Integer,Fragment>();
    private String[] mTabTitles = {"微信","联系人","我"};
    private static final int INDEX_WEIXIN = 0;
    private static final int INDEX_CONTACT = 1;
    private static final int INDEX_PROFILE = 2;
    //记录异常信息时，tab索引
    private int current = 0;
    //记录异常信息时的数据对应的键
    private static final String SAVE_INDEX = "home_save_index";
    private static final String SAVE_DATA = "home_save_data";

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        ButterKnife.bind(this);
        initData();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);//将toolbar与ActionBar关联
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);//初始化状态
        toggle.syncState();*/
        //蒙层颜色
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorGray));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                View mContent = drawerLayout.getChildAt(0);
                ViewHelper.setTranslationX(mContent, drawerView.getMeasuredWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
        });
    }

    private void initData() {
        mTabViews.add(mTabWeixin);
        mTabViews.add(mTabContact);
        mTabViews.add(mTabProfile);
        maps.put(INDEX_WEIXIN,ChatFragment.newInstance(mTabTitles[INDEX_WEIXIN]));
        maps.put(INDEX_CONTACT,ContactFragment.newInstance(mTabTitles[INDEX_CONTACT]));
        maps.put(INDEX_PROFILE,ProfileFragment.newInstance(mTabTitles[INDEX_PROFILE]));
        mViewPager.setOffscreenPageLimit(3);
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            /**
             * @param position 滑动的时候，position总是代表左边的View， position+1总是代表右边的View
             * @param positionOffset 左边View位移的比例
             * @param positionOffsetPixels 左边View位移的像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 左边View进行动画
                mTabViews.get(position).setXPercentage(1 - positionOffset);
                // 如果positionOffset非0，那么就代表右边的View可见，也就说明需要对右边的View进行动画
                if (positionOffset > 0) {
                    mTabViews.get(position + 1).setXPercentage(positionOffset);
                }
            }
        });
    }

    //=====================Activity异常重启（屏幕旋转等）========================
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //程序异常销毁时调用
        outState.putInt(SAVE_INDEX,current);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //恢复数据调用
        int saveIndex = savedInstanceState.getInt(SAVE_INDEX);
        updateCurrentTab(saveIndex);
    }
    //==========================================================================

    /**
     * 这种是少量的可以使用
     */
    private class HomePagerAdapter extends FragmentPagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return getTabFragment(i, mTabTitles[i]);
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            if (mCurTransaction == null) {
//                mCurTransaction = mFragmentManager.beginTransaction();
//            }
//            final long itemId = getItemId(position);
//            String name = makeFragmentName(container.getId(), itemId);
//            Fragment fragment = mFragmentManager.findFragmentByTag(name);
//            if (fragment != null) {
//                mCurTransaction.attach(fragment);
//            } else {
//                fragment = getItem(position);
//                mCurTransaction.add(container.getId(), fragment,
//                        makeFragmentName(container.getId(), itemId));
//            }
//            if (fragment != mCurrentFragment) {
//                fragment.setMenuVisibility(false);
//                fragment.setUserVisibleHint(false);
//            }
////            return fragment;
//            return super.instantiateItem(container, position);
//        }
//        private String makeFragmentName(int viewId, long id) {
//            return "android:switcher:" + viewId + ":" + id;
//        }
//        @Override
//        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            //super.destroyItem(container, position, object);
//            if (mCurTransaction == null) {
//                mCurTransaction = mFragmentManager.beginTransaction();
//            }
//            mCurTransaction.detach((Fragment)object);
//        }
    }

    private void updateCurrentTab(int index) {
        for (int i = 0; i < mTabViews.size(); i++) {
            if (index == i) {
                mTabViews.get(i).setXPercentage(1);
            } else {
                mTabViews.get(i).setXPercentage(0);
            }
        }
        current = index;
    }
    @OnClick({R.id.tab_weixin, R.id.tab_contact, R.id.tab_profile})
    public void onClickTab(View v) {
        switch (v.getId()) {
            case R.id.tab_weixin:
                mViewPager.setCurrentItem(INDEX_WEIXIN, false);
                updateCurrentTab(INDEX_WEIXIN);
                break;
            case R.id.tab_contact:
                mViewPager.setCurrentItem(INDEX_CONTACT, false);
                updateCurrentTab(INDEX_CONTACT);
                break;
            case R.id.tab_profile:
                mViewPager.setCurrentItem(INDEX_PROFILE, false);
                updateCurrentTab(INDEX_PROFILE);
                break;
             default:
                 break;
        }
    }
    private Fragment getTabFragment(int index, String title) {
        Fragment fragment = null;
        switch (index) {
            case INDEX_WEIXIN:
                fragment = ChatFragment.newInstance(title);
                break;
            case INDEX_CONTACT:
                fragment = ContactFragment.newInstance(title);
                break;
            case INDEX_PROFILE:
                fragment = ProfileFragment.newInstance(title);
                break;
        }
        return fragment;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }
}
