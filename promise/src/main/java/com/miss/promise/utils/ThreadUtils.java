package com.miss.promise.utils;

import android.os.Looper;

import com.miss.promise.exception.CalledFromWrongThreadRuntimeException;


/**
 * Created by Hirofumi Nakagawa on 13/07/16.
 */
public final class ThreadUtils {
	private ThreadUtils() {
	}

	public static boolean isMainThread() {
		return isMainThread(Thread.currentThread());
	}

	public static boolean isMainThread(Thread thread) {
		return Looper.getMainLooper().getThread() == thread;
	}

	public static void checkMainThread(Thread thread) {
		if (!isMainThread(thread))
			throw new CalledFromWrongThreadRuntimeException("Don't touch without main thread!!");
	}

	public static void checkNotMainThread(Thread thread) {
		if (isMainThread(thread))
			throw new CalledFromWrongThreadRuntimeException("Don't touch on main thread!!");
	}

	public static void checkNotMainThread() {
		checkNotMainThread(Thread.currentThread());
	}

	public static void checkMainThread() {
		checkMainThread(Thread.currentThread());
	}
}