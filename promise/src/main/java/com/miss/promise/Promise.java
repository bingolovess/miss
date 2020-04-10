package com.miss.promise;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Hirofumi Nakagawa on 13/07/12.
 */
public class Promise<I, O> {
	private static final String TAG = Promise.class.getSimpleName();

	private static final int CORE_POOL_SIZE = 3;

	private static final int MAXIMUM_POOL_SIZE = 64;

	private static final int KEEP_ALIVE = 1;

	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, TAG + " #" + mCount.getAndIncrement());
		}
	};

	private static final ExecutorService sThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
			KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

	private static Map<Object, Set<Promise<?, ?>>> sPromises = new WeakHashMap<Object, Set<Promise<?, ?>>>();

	private final Handler mHandler;

	private final List<Task<?, ?>> mTasks;

	private final List<TaskExecutor<?, ?>> mTaskExecutors;

	private final OnYieldListener mOnYieldListener;

	private final Callback<O> mCallback;

	private final CallbackTaskExecutor<O> mCallbackTaskExecutor;

	private volatile State mState = State.INIT;

	private Promise(Handler handler, List<Task<?, ?>> tasks, List<TaskExecutor<?, ?>> taskExecutors, OnYieldListener onYieldListener, Callback<O> callback) {
		mHandler = handler;
		mTasks = tasks;
		mTaskExecutors = taskExecutors;
		mOnYieldListener = onYieldListener;
		mCallback = callback;
		mCallbackTaskExecutor = new CallbackTaskExecutor<O>(this);
	}

	public void execute(I value) {
		State state = getState();
		if (state == State.DESTROYED)
			throw new IllegalStateException("Promise#execute method must be called in INIT or RUNNING states");

		setState(State.RUNNING);

		if (mTaskExecutors.isEmpty()) {
			mCallbackTaskExecutor.run((O)value);
			return;
		}

		TaskExecutor<I, ?> taskExecutor = (TaskExecutor<I, ?>) mTaskExecutors.get(0);
		taskExecutor.run(value);
	}

	public void destroy() {
		setState(State.DESTROYED);
	}

	public State getState() {
		return mState;
	}

	synchronized void setState(State state) {
		mState = state;
	}

	public List<Task<?, ?>> getAllTasks() {
		return mTasks;
	}

	public Task<?, ?> getTask(int index) {
		if (index >= mTasks.size())
			return null;
		return mTasks.get(index);
	}

	List<TaskExecutor<?, ?>> getAllTaskExecutors() {
		return mTaskExecutors;
	}

	TaskExecutor<?, ?> getTaskExecutor(int index) {
		if (index == mTaskExecutors.size())
			return mCallbackTaskExecutor;
		return mTaskExecutors.get(index);
	}

	void done(final O result) {
		if (getState() != State.RUNNING || mCallback == null)
			return;

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (getState() == State.RUNNING)
					mCallback.onSuccess(result);
			}
		});
	}

	void fail(final Bundle result, final Exception exception) {
		if (getState() != State.RUNNING || mCallback == null)
			return;

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (getState() == State.RUNNING)
					mCallback.onFailure(result, exception);
			}
		});
	}

	void yield(final int code, final Bundle value) {
		if (getState() != State.RUNNING || mOnYieldListener == null)
			return;

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (getState() == State.RUNNING)
					mOnYieldListener.onYield(code, value);
			}
		});
	}

	public enum State {
		INIT,
		RUNNING,
		DESTROYED,
	}

	public static synchronized <T> Creator<T, T> with(Object lifecycle, Class<T> in) {
		return new Creator<T, T>(lifecycle, new Handler(Looper.getMainLooper()), sThreadPoolExecutor);
	}

	public static synchronized void destroyWith(Object lifecycle, Promise<?, ?> promise) {
		try {
			Set<Promise<?, ?>> set = sPromises.remove(lifecycle);
			if (set == null)
				return;
			set.remove(promise);
		} finally {
			promise.destroy();
		}
	}

	public static synchronized void destroyWith(Object lifecycle) {
		Set<Promise<?, ?>> set = sPromises.remove(lifecycle);
		if (set == null)
			return;

		for (Promise<?, ?> promise : set)
			promise.destroy();
	}

	public static class Creator<I, O> implements RestrictedCreator<I, O> {
		private final List<Task<?, ?>> mTasks = new ArrayList<Task<?, ?>>();

		private final List<TaskExecutor<?, ?>> mTaskExecutors = new ArrayList<TaskExecutor<?, ?>>();

		private final Object mLifecycle;

		private final Handler mHandler;

		private final ExecutorService mService;

		private OnYieldListener mOnYieldListener;

		private Callback<O> mCallback;

		public Creator(Object lifecycle, Handler handler, ExecutorService service) {
			mLifecycle = lifecycle;
			mHandler = handler;
			mService = service;
		}

		public <NO> Creator<I, NO> then(Task<O, NO> task) {
			mTasks.add(task);
			mTaskExecutors.add(new TaskExecutor<O, NO>(task, mTaskExecutors.size()));
			return (Creator<I, NO>) this;
		}

		public <NO> Creator<I, NO> thenOnMainThread(Task<O, NO> task) {
			return thenOnMainThread(task, 0);
		}

		public <NO> Creator<I, NO> thenOnMainThread(Task<O, NO> task, long delay) {
			mTasks.add(task);
			mTaskExecutors.add(new HandlerThreadTaskExecutor(task, mTaskExecutors.size(), delay, mHandler));
			return (Creator<I, NO>) this;
		}

		public <NO> Creator<I, NO> thenOnAsyncThread(Task<O, NO> task) {
			return thenOnAsyncThread(task, 0);
		}

		public <NO> Creator<I, List<NO>> thenOnParallelThread(List<Task<O, NO>> tasks) {
			TaskSet<O, NO> set = new TaskSet<O, NO>(tasks);
			mTasks.add(set);
			mTaskExecutors.add(new ParallelTaskExecutor<O, NO>(set, mTaskExecutors.size(), mService));
			return (Creator<I, List<NO>>) this;
		}

		public <NO> Creator<I, NO> thenOnAsyncThread(Task<O, NO> task, long delay) {
			mTasks.add(task);
			mTaskExecutors.add(new AsyncThreadTaskExecutor(task, mTaskExecutors.size(), delay, mService));
			return (Creator<I, NO>) this;
		}

		public Creator<I, O> setOnYieldListener(OnYieldListener onYieldListener) {
			mOnYieldListener = onYieldListener;
			return this;
		}

		public RestrictedCreator<I, O> setCallback(Callback<O> callback) {
			mCallback = callback;
			return this;
		}

		public Promise<I, O> create() {
			Promise<I, O> instance = new Promise<I, O>(mHandler, Collections.unmodifiableList(mTasks), Collections.unmodifiableList(mTaskExecutors), mOnYieldListener, mCallback);
			for (TaskExecutor<?, ?> taskExecutor : mTaskExecutors)
				taskExecutor.setPromise(instance);

			synchronized (Promise.class) {
				Set<Promise<?, ?>> set = sPromises.get(mLifecycle);
				if (set == null) {
					set = new HashSet<Promise<?, ?>>();
					sPromises.put(mLifecycle, set);
				}
				set.add(instance);
				return instance;
			}
		}
	}

	public interface RestrictedCreator<I, O> {
		public Promise<I, O> create();
	}
}
