package com.miss.paint;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.widget.ImageView;

class QrCode {
	static void show(final Activity activity) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Light_Panel);
        } else {
		    builder = new AlertDialog.Builder(activity);
        }
		builder.setTitle(null);
		builder.setCancelable(true);
		ImageView iv = new ImageView(activity);
		iv.setImageResource(R.drawable.qr);
		builder.setView(iv);
		builder.create().show();
	}
}
