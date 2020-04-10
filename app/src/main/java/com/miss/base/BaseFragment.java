package com.miss.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miss.R;
import com.miss.view.CommonHeader;

public class BaseFragment extends Fragment {
    public static final String ARGS_TITLE = "args_title";
    private String mTitle;
    private CommonHeader commonHeader;
    public static BaseFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        BaseFragment fragment = new BaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(ARGS_TITLE, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_common_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TextView titleView = view.findViewById(R.id.fragment_tv);
        commonHeader = view.findViewById(R.id.common_header);
        commonHeader.setTitle(mTitle);
        //titleView.setText(mTitle);
    }

}
