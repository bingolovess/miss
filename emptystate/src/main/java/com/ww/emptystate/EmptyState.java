package com.ww.emptystate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.button.MaterialButton;

public class EmptyState extends ConstraintLayout {
    //icon
    private IconSize iconSize = IconSize.NORMAL;
    private int icon = 0;
    //title
    private String title = "";
    private int titleSize = getResources().getDimensionPixelSize(R.dimen.emps_default_title_size);
    private int titleColor = ContextCompat.getColor(getContext(), R.color.emps_default_title_color);
    //description
    private String description = "";
    private int descriptionSize = getResources().getDimensionPixelSize(R.dimen.emps_default_description_size);
    private int descriptionColor = ContextCompat.getColor(getContext(), R.color.emps_default_description_color);
    //button
    private String buttonText = "";
    private boolean showButton = false;
    private int buttonTextColor = Color.WHITE;
    private int buttonTextSize = getResources().getDimensionPixelSize(R.dimen.emps_default_button_text_size);
    private int buttonBackgroundColor =
            ContextCompat.getColor(getContext(), R.color.emps_default_button_color);
    private int buttonCornerSize = getResources().getDimensionPixelSize(R.dimen.emps_default_button_corner_size);
    private OnClickListener buttonClickListener = null;
    //get font path from attrs and set the typefaces
    private Typeface titleTypeface = null;
    private Typeface descriptionTypeface = null;
    private Typeface buttonTypeface = null;
    public EmptyState(Context context) {
        this(context, null);
    }

    public EmptyState(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public EmptyState(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View.inflate(context, R.layout.empty_state, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmptyState);
        //icon
        icon = typedArray.getResourceId(R.styleable.EmptyState_emps_icon, 0);
        iconSize = IconSize.values()[typedArray.getInt(R.styleable.EmptyState_emps_iconSize, 1)];
        //title
        title = typedArray.getString(R.styleable.EmptyState_emps_title);
        titleSize = typedArray.getDimensionPixelSize(
                R.styleable.EmptyState_emps_titleSize,
                getResources().getDimensionPixelSize(R.dimen.emps_default_title_size)
        );
        titleColor = typedArray.getColor(
                R.styleable.EmptyState_emps_titleColor,
                ContextCompat.getColor(context, R.color.emps_default_title_color)
        );
        String titleFontPath = typedArray.getString(R.styleable.EmptyState_emps_titleFontPath);
        if (!TextUtils.isEmpty(titleFontPath)){
            titleTypeface = Typeface.createFromAsset(context.getAssets(), titleFontPath);
        }
        //description
        description = typedArray.getString(R.styleable.EmptyState_emps_description);
        descriptionSize = typedArray.getDimensionPixelSize(
                R.styleable.EmptyState_emps_descriptionSize,
                getResources().getDimensionPixelSize(R.dimen.emps_default_description_size)
        );
        descriptionColor = typedArray.getColor(
                R.styleable.EmptyState_emps_descriptionColor,
                ContextCompat.getColor(context, R.color.emps_default_description_color)
        );
        String descriptionFontPath = typedArray.getString(R.styleable.EmptyState_emps_descriptionFontPath);
        if (!TextUtils.isEmpty(descriptionFontPath)){
            descriptionTypeface = Typeface.createFromAsset(context.getAssets(), descriptionFontPath);
        }
        //button
        showButton = typedArray.getBoolean(R.styleable.EmptyState_emps_showButton, false);
        buttonText = typedArray.getString(R.styleable.EmptyState_emps_buttonText);
        buttonTextColor = typedArray.getColor(
                R.styleable.EmptyState_emps_buttonTextColor,
                Color.WHITE
        );
        buttonTextSize = typedArray.getDimensionPixelSize(
                R.styleable.EmptyState_emps_buttonTextSize,
                getResources().getDimensionPixelSize(R.dimen.emps_default_button_text_size)
        );
        buttonBackgroundColor = typedArray.getColor(
                R.styleable.EmptyState_emps_buttonBackgroundColor,
                ContextCompat.getColor(context, R.color.emps_default_button_color)
        );
        buttonCornerSize = typedArray.getDimensionPixelSize(
                R.styleable.EmptyState_emps_buttonCorner,
                getResources().getDimensionPixelSize(R.dimen.emps_default_button_corner_size)
        );
        String buttonFontPath = typedArray.getString(R.styleable.EmptyState_emps_buttonFontPath);
        if (!TextUtils.isEmpty(buttonFontPath)){
            buttonTypeface = Typeface.createFromAsset(context.getAssets(), buttonFontPath);
        }
        typedArray.recycle();
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initialView();
    }
    private AppCompatImageView emptyStateIcon;
    private TextView emptyStateTitle,emptyStateDescription;
    private MaterialButton emptyStateButton;
    private Guideline leftGuideline,rightGuideline;
    private void initialView() {
        emptyStateIcon = this.findViewById(R.id.emptyStateIcon);
        emptyStateTitle = this.findViewById(R.id.emptyStateTitle);
        emptyStateDescription = this.findViewById(R.id.emptyStateDescription);
        emptyStateButton = this.findViewById(R.id.emptyStateButton);
        leftGuideline = this.findViewById(R.id.leftGuideline);
        rightGuideline = this.findViewById(R.id.rightGuideline);
        //icon
        emptyStateIcon.setImageResource(icon);
        setIconSize(iconSize);
        //title
        if (!TextUtils.isEmpty(title)){
            emptyStateTitle.setText(title);
        } else{
            emptyStateTitle.setVisibility(View.GONE);
        }
        if (titleTypeface!=null){
            emptyStateTitle.setTypeface(titleTypeface);
        }
        emptyStateTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        emptyStateTitle.setTextColor(titleColor);
        //description
        if (!TextUtils.isEmpty(description)){
            emptyStateDescription.setText(description);
        } else {
            emptyStateDescription.setVisibility(View.GONE);
        }
        if (descriptionTypeface!=null){
            emptyStateDescription.setTypeface(descriptionTypeface);
        }
        emptyStateDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, descriptionSize);
        emptyStateDescription.setTextColor(descriptionColor);
        //button
        if (showButton){
            emptyStateButton.setVisibility(View.VISIBLE);
        }else {
            emptyStateButton.setVisibility(View.GONE);
        }

        emptyStateButton.setText(buttonText);
        emptyStateButton.setTextColor(buttonTextColor);
        emptyStateButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
        emptyStateButton.setCornerRadius(buttonCornerSize);
        if (buttonTypeface!=null){
            emptyStateButton.setTypeface(buttonTypeface);
        }
        DrawableCompat.setTint(emptyStateButton.getBackground(), buttonBackgroundColor);
        emptyStateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getVisibility() == View.VISIBLE){
                    if (buttonClickListener!=null){
                        buttonClickListener.onClick(view);
                    }
                }
            }
        });
    }

    /**
     * set a drawable resource as icon
     * @param resourceId get drawable resource, R.drawable.something
     * @return instance of emptyState
     */
    public EmptyState setIcon(@DrawableRes int resourceId){
        emptyStateIcon.setImageResource(resourceId);
        return this;
    }

    /**
     * set icon size type
     * @param iconSize get SMALL, NORMALL or BIG
     * @return instance of emptyState
     */
    public EmptyState setIconSize(IconSize iconSize){
        int size = 0;
        switch (iconSize){
            case BIG:
                size = getResources().getDimensionPixelSize(R.dimen.emps_default_icon_big_size);
                break;
            case SMALL:
                size = getResources().getDimensionPixelSize(R.dimen.emps_default_icon_small_size);
                break;
           default:
               size = getResources().getDimensionPixelSize(R.dimen.emps_default_icon_normal_size);
                break;
        }
        ViewGroup.LayoutParams params = emptyStateIcon.getLayoutParams();
        params.height = size;
        params.width = size;
        emptyStateIcon.setLayoutParams(params);

        return this;
    }

    /**
     * set title resource id
     * @param resourceId get a string resource id, R.string.something
     * @return instance of emptyState
     */
    public EmptyState setTitle(@StringRes int resourceId){
        title = getContext().getString(resourceId);
        emptyStateTitle.setText(title);
        emptyStateTitle.setVisibility( View.VISIBLE);
        return this;
    }

    /**
     * set plain title
     * @param text get a string, "something"
     * @return instance of emptyState
     */
    public EmptyState setTitle(String text){
        this.title = text;
        if (!TextUtils.isEmpty(text)){
            emptyStateTitle.setText(text);
            emptyStateTitle.setVisibility(View.VISIBLE);
        } else {
            emptyStateTitle.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * set title color
     * @param colorRes get color resource id, R.color.someColor
     * @return instance of emptyState
     */
    public EmptyState setTitleColorResource(@ColorRes int colorRes){
        titleColor = ContextCompat.getColor(getContext(), colorRes);
        emptyStateTitle.setTextColor(titleColor);
        return this;
    }

    /**
     * set description color
     * @param color get color
     * @return instance of emptyState
     */
    public EmptyState setTitleColor(@ColorInt int color){
        titleColor = color;
        emptyStateTitle.setTextColor(color);
        return this;
    }

    /**
     * set title size
     * @param dimenRes get dimension, R.dimen.someSize
     * @return instance of emptyState
     */
    public EmptyState setTitleSize(@DimenRes int dimenRes){
        titleSize = getResources().getDimensionPixelSize(dimenRes);
        emptyStateTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        return this;
    }

    /**
     * set title typeface
     * @param typeface
     * @return instance of emptyState
     */
    public EmptyState setTitleTypeface(Typeface typeface){
        titleTypeface = typeface;
        emptyStateTitle.setTypeface(titleTypeface);
        return this;
    }


    /**
     * set description resource id
     * @param resourceId get a string resource id, R.string.something
     * @return instance of emptyState
     */
    public EmptyState setDescription(@StringRes int resourceId){
        description = getContext().getString(resourceId);
        emptyStateDescription.setText(description);
        emptyStateDescription.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * set plain description
     * @param text get a string, "something"
     * @return instance of emptyState
     */
    public EmptyState setDescription(String text){
        this.description = text;
        emptyStateDescription.setText(text);
        emptyStateDescription.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * set description color
     * @param colorRes get color resource id, R.color.someColor
     * @return instance of emptyState
     */
    public EmptyState setDescriptionColorResource(@ColorRes int colorRes){
        descriptionColor = ContextCompat.getColor(getContext(), colorRes);
        emptyStateDescription.setTextColor(descriptionColor);
        return this;
    }

    /**
     * set description color
     * @param color get color
     * @return instance of emptyState
     */
    public EmptyState setDescriptionColor(@ColorInt int color){
        descriptionColor = color;
        emptyStateDescription.setTextColor(color);
        return this;
    }

    /**
     * set description size
     * @param dimenRes get dimension, R.dimen.someSize
     * @return instance of emptyState
     */
    public EmptyState setDescriptionSize(@DimenRes int dimenRes){
        descriptionSize = getResources().getDimensionPixelSize(dimenRes);
        emptyStateDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, descriptionSize);
        return this;
    }

    /**
     * set description typeface
     * @param typeface
     * @return instance of emptyState
     */
    public EmptyState setDescriptionTypeface(Typeface typeface){
        descriptionTypeface = typeface;
        emptyStateDescription.setTypeface(descriptionTypeface);
        return this;
    }

    /**
     * set button description resource id
     * @param resourceId get a string resource id, R.string.something
     * @return instance of emptyState
     */
    public EmptyState setButtonText(@StringRes int resourceId){
        buttonText = getResources().getString(resourceId);
        emptyStateDescription.setText(description);
        //because it has description and can't be GONE
        showButton = true;
        emptyStateButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * set plain button description
     * @param text get a string, "something"
     * @return instance of emptyState
     */
    public EmptyState setButtonText(String text){
        buttonText = text;
        emptyStateButton.setText(text);
        //because it has description and can't be GONE
        showButton = true;
        emptyStateButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * set button background color
     * @param colorRes get color resource id, R.color.someColor
     * @return instance of emptyState
     */
    public EmptyState setButtonBackgroundColorResource(@ColorRes int colorRes){
        buttonBackgroundColor = ContextCompat.getColor(getContext(), colorRes);
        DrawableCompat.setTint(emptyStateButton.getBackground(), buttonBackgroundColor);
        return this;
    }

    /**
     * set button background color
     * @param color get color
     * @return instance of emptyState
     */
    public EmptyState setButtonBackgroundColor(@ColorInt int color){
        buttonBackgroundColor = color;
        DrawableCompat.setTint(emptyStateButton.getBackground(), buttonBackgroundColor);
        return this;
    }

    /**
     * set button description size
     * @param dimenRes get dimension, R.dimen.someSize
     * @return instance of emptyState
     */
    public EmptyState setButtonTextSize(@DimenRes int dimenRes){
        buttonTextSize = getResources().getDimensionPixelSize(dimenRes);
        emptyStateButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize);
        return this;
    }

    /**
     * set button corner size
     * @param dimenRes get dimension, R.dimen.someSize
     * @return instance of emptyState
     */
    public EmptyState setButtonCornerSize(@DimenRes int dimenRes){
        buttonCornerSize = getResources().getDimensionPixelSize(dimenRes);
        emptyStateButton.setCornerRadius(buttonCornerSize);
        return this;
    }

    /**
     * set button typeface
     * @param typeface
     * @return instance of emptyState
     */
    public EmptyState setButtonTypeface(Typeface typeface){
        buttonTypeface = typeface;
        emptyStateButton.setTypeface(buttonTypeface);
        return this;
    }

    /**
     * set button click listener
     * @param clickListener instance of OnClickListener
     * @return instance of emptyState
     */
    public EmptyState setButtonClickListener(OnClickListener clickListener){
        buttonClickListener = clickListener;
        return this;
    }

    /**
     * showing EmptyState
     * @param animRes get animation resource, default is fade_in
     * @param interpolator get animation interpolator, default is AccelerateDecelerateInterpolator
     */
    public void show(@AnimRes int animRes, Interpolator interpolator) {
        if (animRes == 0){
            animRes = android.R.anim.fade_in;
        }
        if (interpolator == null){
            interpolator = new AccelerateDecelerateInterpolator();
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), animRes);
        animation.setInterpolator(interpolator);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                EmptyState.this.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
    }

    /**
     * hiding EmptyState
     * @param animRes get animations resource, default is fade_out
     * @param interpolator get animation interpolator, default is AccelerateDecelerateInterpolator
     */
    public void hide(@AnimRes int animRes,Interpolator interpolator) {
        if(animRes == 0){
            animRes = android.R.anim.fade_out;
        }
        if (interpolator == null){
            interpolator = new AccelerateDecelerateInterpolator();
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), animRes);
        animation.setInterpolator(interpolator);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                EmptyState.this.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
    }
}
