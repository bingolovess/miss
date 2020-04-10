package com.miss.paint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class BrushesFragment extends Fragment {
    final static int LOAD_IMAGE = 1000;
    private static final String TAG = "Brushes";
    private static final boolean DEBUG = true;

    public static final String IMAGE_SAVE_DIRNAME = "Drawings";
    public static final String IMAGE_TEMP_DIRNAME = IMAGE_SAVE_DIRNAME + "/.temporary";
    public static final String WIP_FILENAME = "temporary.png";

    public static final String PREF_LAST_TOOL = "tool";
    public static final String PREF_LAST_TOOL_TYPE = "tool_type";
    public static final String PREF_LAST_COLOR = "color";
    public static final String PREF_LAST_HUDSTATE = "hudup";

    private boolean mJustLoadedImage = false;

    private Slate mSlate;

    private ToolButton mLastTool, mActiveTool;
    private ToolButton mLastColor, mActiveColor;
    private ToolButton mLastPenType, mActivePenType;

    private View mDebugButton;
    private View mColorsView;
    private View mActionBarView;
    private View mToolsView;
    private View mLogoView,mUnDoView,mLoadView,mClearView,mSaveView,mShareView,mOverflowView;
    private View mComboHudView;

    private Dialog mMenuDialog;

    private SharedPreferences mPrefs;

    private LinkedList<String> mDrawingsToScan = new LinkedList<String>();

    protected MediaScannerConnection mMediaScannerConnection;
    private String mPendingShareFile;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient =
            new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    if (DEBUG) Log.v(TAG, "media scanner connected");
                    scanNext();
                }

                private void scanNext() {
                    synchronized (mDrawingsToScan) {
                        if (mDrawingsToScan.isEmpty()) {
                            mMediaScannerConnection.disconnect();
                            return;
                        }
                        String fn = mDrawingsToScan.removeFirst();
                        mMediaScannerConnection.scanFile(fn, "image/png");
                    }
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    if (DEBUG) Log.v(TAG, "File scanned: " + path);
                    synchronized (mDrawingsToScan) {
                        if (path.equals(mPendingShareFile)) {
                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                            sendIntent.setType("image/png");
                            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(sendIntent, "Send drawing to:"));
                            mPendingShareFile = null;
                        }
                        scanNext();
                    }
                }
            };


    public static class ColorList extends LinearLayout {
        public ColorList(Context c, AttributeSet as) {
            super(c, as);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int newOrientation = (((right-left) > (bottom-top)) ? HORIZONTAL : VERTICAL);
            if (newOrientation != getOrientation()) {
                setOrientation(newOrientation);
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent e) {
            return true;
        }
    }

//    @Override
//    public Object onRetainNonConfigurationInstance() {
//        ((ViewGroup)mSlate.getParent()).removeView(mSlate);
//        return mSlate;
//    }

    public static interface ViewFunc {
        public void apply(View v);
    }

    public static void descend(ViewGroup parent, BrushesActivity.ViewFunc func) {
        for (int i=0; i<parent.getChildCount(); i++) {
            final View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                descend((ViewGroup) v, func);
            } else {
                func.apply(v);
            }
        }
    }

    @TargetApi(11)
    private void setupLayers() {
        if (!hasAnimations()) return;

        if (mComboHudView != null) {
            mComboHudView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            mToolsView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mColorsView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mActionBarView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle icicle) {
        super.onViewCreated(view, icicle);
        mSlate = (Slate) getActivity().getLastNonConfigurationInstance();
        if (mSlate == null) {
            mSlate = new Slate(getContext());
            // Load the old buffer if necessary
            if (!mJustLoadedImage) {
                loadDrawing(WIP_FILENAME, true);
            } else {
                mJustLoadedImage = false;
            }
        }
        final ViewGroup root = ((ViewGroup)view.findViewById(R.id.root));
        root.addView(mSlate, 0);

        mMediaScannerConnection =
                new MediaScannerConnection(getContext(), mMediaScannerClient);


//        if (icicle != null) {
//            onRestoreInstanceState(icicle);
//        }

        mActionBarView = view.findViewById(R.id.actionbar);
        mComboHudView = view.findViewById(R.id.hud);
        mToolsView = view.findViewById(R.id.tools);
        mColorsView = view.findViewById(R.id.colors);
        mLogoView = view.findViewById(R.id.logo);
        //补充按钮的点击事件 mUnDoView,mLoadView,mClearView,mSaveView,mShareView,mOverflowView;
        mUnDoView = view.findViewById(R.id.undo);
        mLoadView = view.findViewById(R.id.load);
        mClearView = view.findViewById(R.id.clear);
        mSaveView = view.findViewById(R.id.save);
        mShareView = view.findViewById(R.id.share);
        mOverflowView = view.findViewById(R.id.overflow);
        handlerIconClickEvent(view);

        setupLayers(); // the HUD needs to have a software layer at all times
        // so we can draw through it quickly

        mDebugButton = view.findViewById(R.id.debug);

        TextView title = (TextView) mActionBarView.findViewById(R.id.logotype);
        Typeface light = Typeface.create("sans-serif-light", Typeface.NORMAL);
        title.setTypeface(light);

        final ToolButton.ToolCallback toolCB = new ToolButton.ToolCallback() {
            @Override
            public void setPenMode(ToolButton tool, float min, float max) {
                mSlate.setPenSize(min, max);
                mLastTool = mActiveTool;
                mActiveTool = tool;

                if (mLastTool != mActiveTool) {
                    mLastTool.deactivate();
                    mPrefs.edit().putString(PREF_LAST_TOOL, (String) mActiveTool.getTag())
                            .commit();
                }
            }
            @Override
            public void setPenColor(ToolButton tool, int color) {
                BrushesFragment.this.setPenColor(color);
                mLastColor = mActiveColor;
                mActiveColor = tool;
                if (mLastColor != mActiveColor) {
                    mLastColor.deactivate();
                    mPrefs.edit().putInt(PREF_LAST_COLOR, color).commit();
                }
            }
            @Override
            public void setPenType(ToolButton tool, int penType) {
                BrushesFragment.this.setPenType(penType);
                mLastPenType = mActivePenType;
                mActivePenType = tool;
                if (mLastPenType != mActivePenType) {
                    mLastPenType.deactivate();
                    mPrefs.edit().putString(PREF_LAST_TOOL_TYPE, (String) mActivePenType.getTag())
                            .commit();
                }
            }
            @Override
            public void restore(ToolButton tool) {
                if (tool == mActiveTool && tool != mLastTool) {
                    mLastTool.click();
                    mPrefs.edit().putString(PREF_LAST_TOOL, (String) mActiveTool.getTag())
                            .commit();
                } else if (tool == mActiveColor && tool != mLastColor) {
                    mLastColor.click();
                    mPrefs.edit().putInt(PREF_LAST_COLOR, ((ToolButton.SwatchButton) mLastColor).color)
                            .commit();
                }
            }
            @Override
            public void setBackgroundColor(ToolButton tool, int color) {
                mSlate.setDrawingBackground(color);
            }
        };

        descend((ViewGroup) mColorsView, new BrushesActivity.ViewFunc() {
            @Override
            public void apply(View v) {
                final ToolButton.SwatchButton swatch = (ToolButton.SwatchButton) v;
                if (swatch != null) {
                    swatch.setCallback(toolCB);
                }
            }
        });

        final ToolButton penThinButton = (ToolButton)view.findViewById(R.id.pen_thin);
        penThinButton.setCallback(toolCB);

        final ToolButton penMediumButton = (ToolButton)view.findViewById(R.id.pen_medium);
        if (penMediumButton != null) {
            penMediumButton.setCallback(toolCB);
        }

        final ToolButton penThickButton = (ToolButton)view.findViewById(R.id.pen_thick);
        penThickButton.setCallback(toolCB);

        final ToolButton fatBrushButton = (ToolButton)view.findViewById(R.id.fat_brush);
        if (fatBrushButton != null) {
            fatBrushButton.setCallback(toolCB);
        }

        final ToolButton typeWhiteboardButton = (ToolButton)view.findViewById(R.id.whiteboard_brush);
        typeWhiteboardButton.setCallback(toolCB);

        final ToolButton typeFeltTipButton = (ToolButton)view.findViewById(R.id.felttip_brush);
        if (typeFeltTipButton != null) {
            typeFeltTipButton.setCallback(toolCB);
        }

        final ToolButton typeAirbrushButton = (ToolButton)view.findViewById(R.id.airbrush_brush);
        if (typeAirbrushButton != null) {
            typeAirbrushButton.setCallback(toolCB);
        }

        final ToolButton typeFountainPenButton = (ToolButton)view.findViewById(R.id.fountainpen_brush);
        if (typeFountainPenButton != null) {
            typeFountainPenButton.setCallback(toolCB);
        }
        mLastPenType = mActivePenType = typeWhiteboardButton;
        loadSettings();
        mActiveTool.click();
        mActivePenType.click();
        // clickDebug(null); // auto-debug mode for testing devices
    }

    private void handlerIconClickEvent(View view) {
        mLogoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLogo(view);
            }
        });
        mUnDoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUndo(view);
            }
        });
        mLoadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickLoad(view);
            }
        });
        mClearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickClear(view);
            }
        });
        mSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSave(view);
            }
        });
        mShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickShare(view);
            }
        });
        mOverflowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOverflow(view);
            }
        });
    }

    private void loadSettings() {
        mPrefs = getActivity().getPreferences(MODE_PRIVATE);

        final String toolTag = mPrefs.getString(PREF_LAST_TOOL, null);
        if (toolTag != null) {
            mActiveTool = (ToolButton) mToolsView.findViewWithTag(toolTag);
        }
        if (mActiveTool == null) {
            mActiveTool = (ToolButton) mToolsView.findViewById(R.id.pen_thick);
        }
        if (mActiveTool == null) {
            mActiveTool = (ToolButton) mToolsView.findViewById(R.id.pen_thin);
        }
        mLastTool = mActiveTool;
        if (mActiveTool != null) mActiveTool.click();

        final String typeTag = mPrefs.getString(PREF_LAST_TOOL_TYPE, "type_whiteboard");
        mLastPenType = mActivePenType = (ToolButton) mToolsView.findViewWithTag(typeTag);
        if (mActivePenType != null) mActivePenType.click();

        final int color = mPrefs.getInt(PREF_LAST_COLOR, 0xFF000000);
        descend((ViewGroup) mColorsView, new BrushesActivity.ViewFunc() {
            @Override
            public void apply(View v) {
                final ToolButton.SwatchButton swatch = (ToolButton.SwatchButton) v;
                if (swatch != null) {
                    if (color == swatch.color) {
                        mActiveColor = swatch;
                    }
                }
            }
        });
        mLastColor = mActiveColor;
        if (mActiveColor != null) mActiveColor.click();

        setHUDVisibility(mPrefs.getBoolean(PREF_LAST_HUDSTATE, false), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveDrawing(WIP_FILENAME, true);
    }

    @Override
    public void onResume() {
        super.onResume();

        String orientation = getString(R.string.orientation);

        getActivity().setRequestedOrientation(
                "landscape".equals(orientation)
                        ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

//    @Override
//    public void onAttachedToWindow() {
//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private String dumpBundle(Bundle b) {
        if (b == null) return "null";
        StringBuilder sb = new StringBuilder("Bundle{");
        boolean first = true;
        for (String key : b.keySet()) {
            if (!first) sb.append(" ");
            first = false;
            sb.append(key+"=(");
            sb.append(b.get(key));
        }
        sb.append("}");
        return sb.toString();
    }


    @Override
    public void onStart() {
        super.onStart();
        Intent startIntent = getActivity().getIntent();
        if (DEBUG) Log.d(TAG, "starting with intent=" + startIntent + " extras=" + dumpBundle(startIntent.getExtras()));
        String a = startIntent.getAction();
        if (!TextUtils.isEmpty(a)){
            if (a.equals(Intent.ACTION_EDIT)) {
                // XXX: what happens to the old drawing? we should really move to auto-save
                mSlate.clear();
                loadImageFromIntent(startIntent);
            } else if (a.equals(Intent.ACTION_SEND)) {
                // XXX: what happens to the old drawing? we should really move to auto-save
                mSlate.clear();
                loadImageFromContentUri((Uri)startIntent.getParcelableExtra(Intent.EXTRA_STREAM));
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            setHUDVisibility(!getHUDVisibility(), true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    final static boolean hasAnimations() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
    }

    public void clickLogo(View v) {
        setHUDVisibility(!getHUDVisibility(), true);
    }

    public boolean getHUDVisibility() {
        return mActionBarView.getVisibility() == View.VISIBLE;
    }

    @TargetApi(11)
    public void setHUDVisibility(boolean show, boolean animate) {
        if (!show) {
            if (hasAnimations() && animate) {
                AnimatorSet a = new AnimatorSet();
                AnimatorSet.Builder b =
                        a.play(ObjectAnimator.ofFloat(mLogoView, "alpha", 1f, 0.5f))
                                .with(ObjectAnimator.ofFloat(mActionBarView, "alpha", 1f, 0f));
                if (mComboHudView != null) {
                    b.with(ObjectAnimator.ofFloat(mComboHudView, "alpha", 1f, 0f));
                } else {
                    b.with(ObjectAnimator.ofFloat(mColorsView, "alpha", 1f, 0f))
                            .with(ObjectAnimator.ofFloat(mToolsView, "alpha", 1f, 0f));
                }
                a.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator a) {
                        if (mComboHudView != null) {
                            mComboHudView.setVisibility(View.GONE);
                            mComboHudView.setLayerType(View.LAYER_TYPE_NONE, null);
                        } else {
                            mColorsView.setVisibility(View.GONE);
                            mToolsView.setVisibility(View.GONE);
                        }
                        mActionBarView.setVisibility(View.GONE);
                    }
                });
                a.start();
            } else {
                if (mComboHudView != null) {
                    mComboHudView.setVisibility(View.GONE);
                } else {
                    mColorsView.setVisibility(View.GONE);
                    mToolsView.setVisibility(View.GONE);
                }
                mActionBarView.setVisibility(View.GONE);
                if (hasAnimations()) {
                    mLogoView.setAlpha(0.5f);
                }
            }
        } else {
            if (mComboHudView != null) {
                mComboHudView.setVisibility(View.VISIBLE);
            } else {
                mColorsView.setVisibility(View.VISIBLE);
                mToolsView.setVisibility(View.VISIBLE);
            }
            mActionBarView.setVisibility(View.VISIBLE);
            if (hasAnimations() && animate) {
                AnimatorSet a = new AnimatorSet();
                AnimatorSet.Builder b =
                        a.play(ObjectAnimator.ofFloat(mLogoView, "alpha", 0.5f, 1f))
                                .with(ObjectAnimator.ofFloat(mActionBarView, "alpha", 0f, 1f));
                if (mComboHudView != null) {
                    b.with(ObjectAnimator.ofFloat(mComboHudView, "alpha", 0f, 1f));
                } else {
                    b.with(ObjectAnimator.ofFloat(mColorsView, "alpha", 0f, 1f))
                            .with(ObjectAnimator.ofFloat(mToolsView, "alpha", 0f, 1f));
                }
                a.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator a) {
                        if (mComboHudView != null) {
                            mComboHudView.setVisibility(View.VISIBLE);
                            mComboHudView.setLayerType(View.LAYER_TYPE_NONE, null);
                        } else {
                            mColorsView.setVisibility(View.VISIBLE);
                            mToolsView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                a.start();
            } else {
                if (hasAnimations()) {
                    mLogoView.setAlpha(1f);
                }
            }
        }
        mPrefs.edit().putBoolean(PREF_LAST_HUDSTATE, show).commit();
    }

    public void clickClear(View v) {
        mSlate.clear();
    }

    public boolean loadDrawing(String filename) {
        return loadDrawing(filename, false);
    }

    @TargetApi(8)
    public File getPicturesDirectory() {
        final File d;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            d = new File("/sdcard/Pictures");
        }
        return d;
    }

    public boolean loadDrawing(String filename, boolean temporary) {
        File d = getPicturesDirectory();
        d = new File(d, temporary ? IMAGE_TEMP_DIRNAME : IMAGE_SAVE_DIRNAME);
        final String filePath = new File(d, filename).toString();
        if (DEBUG) Log.d(TAG, "loadDrawing: " + filePath);

        if (d.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            opts.inScaled = false;
            Bitmap bits = BitmapFactory.decodeFile(filePath, opts);
            if (bits != null) {
                //mSlate.setBitmap(bits); // messes with the bounds
                mSlate.paintBitmap(bits);
                return true;
            }
        }
        return false;
    }

    public void saveDrawing(String filename) {
        saveDrawing(filename, false);
    }

    public void saveDrawing(String filename, boolean temporary) {
        saveDrawing(filename, temporary, /*animate=*/ false, /*share=*/ false, /*clear=*/ false);
    }

    public void saveDrawing(String filename, boolean temporary, boolean animate, boolean share, boolean clear) {
        final Bitmap localBits = mSlate.copyBitmap(/*withBackground=*/!temporary);
        if (localBits == null) {
            if (DEBUG) Log.e(TAG, "save: null bitmap");
            return;
        }

        final String _filename = filename;
        final boolean _temporary = temporary;
        final boolean _share = share;
        final boolean _clear = clear;

        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {
                String fn = null;
                try {
                    File d = getPicturesDirectory();
                    d = new File(d, _temporary ? IMAGE_TEMP_DIRNAME : IMAGE_SAVE_DIRNAME);
                    if (!d.exists()) {
                        if (d.mkdirs()) {
                            if (_temporary) {
                                final File noMediaFile = new File(d, MediaStore.MEDIA_IGNORE_FILENAME);
                                if (!noMediaFile.exists()) {
                                    new FileOutputStream(noMediaFile).write('\n');
                                }
                            }
                        } else {
                            throw new IOException("cannot create dirs: " + d);
                        }
                    }
                    File file = new File(d, _filename);
                    if (DEBUG) Log.d(TAG, "save: saving " + file);
                    OutputStream os = new FileOutputStream(file);
                    localBits.compress(Bitmap.CompressFormat.PNG, 0, os);
                    localBits.recycle();
                    os.close();

                    fn = file.toString();
                } catch (IOException e) {
                    Log.e(TAG, "save: error: " + e);
                }
                return fn;
            }

            @Override
            protected void onPostExecute(String fn) {
                if (fn != null) {
                    synchronized(mDrawingsToScan) {
                        mDrawingsToScan.add(fn);
                        if (_share) {
                            mPendingShareFile = fn;
                        }
                        if (!mMediaScannerConnection.isConnected()&& !BrushesFragment.this.isRemoving()) {
                            mMediaScannerConnection.connect(); // will scan the files and share them
                        }
                    }
                }

                if (_clear) mSlate.clear();
            }
        }.execute();

    }

    public void clickSave(View v) {
        if (mSlate.isEmpty()) return;
        v.setEnabled(false);
        String filename = System.currentTimeMillis() + ".png";
        saveDrawing(filename);
        Toast.makeText(getContext(), "Drawing saved: " + filename, Toast.LENGTH_SHORT).show();
        v.setEnabled(true);
    }

    public void clickSaveAndClear(View v) {
        if (mSlate.isEmpty()) return;

        v.setEnabled(false);
        final String filename = System.currentTimeMillis() + ".png";
        saveDrawing(filename,
                /*temporary=*/ false, /*animate=*/ true, /*share=*/ false, /*clear=*/ true);
        Toast.makeText(getContext(), "Drawing saved: " + filename, Toast.LENGTH_SHORT).show();
        v.setEnabled(true);
    }

    private void setThingyEnabled(Object v, boolean enabled) {
        if (v == null) return;
        if (v instanceof View) ((View)v).setEnabled(enabled);
        else if (v instanceof MenuItem) ((MenuItem)v).setEnabled(enabled);
    }

    public void clickShare(View v) {
        hideOverflow();
        setThingyEnabled(v, false);
        final String filename = System.currentTimeMillis() + ".png";
        // can't use a truly temporary file because:
        // - we want mediascanner to give us a content: URI for it; some apps don't like file: URIs
        // - if mediascanner scans it, it will show up in Gallery, so it might as well be a regular drawing
        saveDrawing(filename,
                /*temporary=*/ false, /*animate=*/ false, /*share=*/ true, /*clear=*/ false);
        setThingyEnabled(v, true);
    }

    public void clickLoad(View unused) {
        hideOverflow();
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, LOAD_IMAGE);
    }

    public void clickDebug(View unused) {
        hideOverflow();
        boolean debugMode = (mSlate.getDebugFlags() == 0); // toggle
        mSlate.setDebugFlags(debugMode
                ? Slate.FLAG_DEBUG_EVERYTHING
                : 0);
        mDebugButton.setSelected(debugMode);
        Toast.makeText(getContext(), "Debug mode " + ((mSlate.getDebugFlags() == 0) ? "off" : "on"),
                Toast.LENGTH_SHORT).show();
    }

    public void clickUndo(View unused) {
        mSlate.undo();
    }

    public void clickAbout(View unused) {
        hideOverflow();
        //About.show(this);
    }

    public void clickQr(View unused) {
        hideOverflow();
        //QrCode.show(this);
    }

    public void clickShareMarketLink(View unused) {
        hideOverflow();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        startActivity(Intent.createChooser(sendIntent, "Share the Brushs app with:"));
    }

    public void clickMarketLink(View unused) {
        hideOverflow();
        Intent urlIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + getActivity().getPackageName()));
        startActivity(urlIntent);
    }

    public void clickSiteLink(View unused) {
        hideOverflow();
        Intent urlIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://rubberflat.com/brushes?from=app"));
        startActivity(urlIntent);
    }

    private void showOverflow() {
        mMenuDialog.show();
    }
    private void hideOverflow() {
        mMenuDialog.dismiss();
    }
    public void clickOverflow(View v) {
        if (mMenuDialog == null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.overflow_menu, null);

            //        TextView text = (TextView) layout.findViewById(R.id.text);
            //        text.setText("Hello, this is a custom dialog!");
            //        ImageView image = (ImageView) layout.findViewById(R.id.image);
            //        image.setImageResource(R.drawable.android);

            mMenuDialog = new Dialog(getContext());
            //mMenuDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK).create();
            Window dialogWin  = mMenuDialog.getWindow();
            dialogWin.requestFeature(Window.FEATURE_NO_TITLE);
            dialogWin.setGravity(Gravity.TOP|Gravity.RIGHT);
            WindowManager.LayoutParams winParams = dialogWin.getAttributes();
            winParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            winParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            winParams.y = getResources().getDimensionPixelOffset(R.dimen.action_bar_height);
            dialogWin.setAttributes(winParams);
            dialogWin.setWindowAnimations(android.R.style.Animation_Translucent);
            dialogWin.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mMenuDialog.setCanceledOnTouchOutside(true);

            mMenuDialog.setContentView(layout);
            // bash the background
            final View decor = layout.getRootView();

            decor.setBackgroundDrawable(null);
            decor.setPadding(0,0,0,0);
        }

        showOverflow();
    }

    public void setPenColor(int color) {
        mSlate.setPenColor(color);
    }

    public void setPenType(int type) {
        mSlate.setPenType(type);
    }

    protected void loadImageFromIntent(Intent imageReturnedIntent) {
        Uri contentUri = imageReturnedIntent.getData();
        loadImageFromContentUri(contentUri);
    }

    protected void loadImageFromContentUri(Uri contentUri) {
        Toast.makeText(getContext(), "Loading from " + contentUri, Toast.LENGTH_SHORT).show();

        loadDrawing(WIP_FILENAME, true);
        mJustLoadedImage = true;

        try {
            Bitmap b = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
            if (b != null) {
                mSlate.paintBitmap(b);
                if (DEBUG) Log.d(TAG, "successfully loaded bitmap: " + b);
            } else {
                Log.e(TAG, "couldn't get bitmap from " + contentUri);
            }
        } catch (FileNotFoundException ex) {
            Log.e(TAG, "error loading image from " + contentUri + ": " + ex);
        } catch (IOException ex) {
            Log.e(TAG, "error loading image from " + contentUri + ": " + ex);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    loadImageFromIntent(imageReturnedIntent);
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaScannerConnection.isConnected()) {
            mMediaScannerConnection.disconnect();
        }
    }
}
