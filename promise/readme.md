```java
   //promise
    Promise.with(this, Integer.class)
     	   .then(new Task<Integer, String>() {
				@Override
     		    public void run(Integer value, NextTask<String> next) {
					ThreadUtils.checkMainThread();
					Log.e(TAG, "0---------------------value = "+ value);
					Toast.makeText(MainActivity.this, "Input:" + value, Toast.LENGTH_SHORT).show();
					next.yield(0, null);
					next.run(value + 1 + "");
     		}})
     		.thenOnAsyncThread(new Task<String, Integer>() {
				@Override
				public void run(String value, NextTask<Integer> next) {
     				try {
						ThreadUtils.checkNotMainThread();
     					Log.e(TAG, "This task is running on the no main thread  value = "+ value);
     					Thread.sleep(1000);
     					int i = 1/0;
     					next.run(Integer.parseInt(value) + 1);
     				} catch (InterruptedException e) {
     					next.fail(null, e);
     				}
     			}})
     		.then(new Task<Integer, String>() {
     			@Override
     			public void run(Integer value, NextTask<String> next) {
     				ThreadUtils.checkNotMainThread();
     				Log.e(TAG,"1------------value = "+ value);
     				next.run(value + 1 + "");
     			}})
     		.thenOnMainThread(new Task<String, Integer>() {
     			@Override
     			public void run(String value, NextTask<Integer> next) {
     				Log.e(TAG,"2------------value = "+ value);
     				ThreadUtils.checkMainThread();
     				Toast.makeText(MainActivity.this, "This task is running on main thread", Toast.LENGTH_SHORT).show();
     				next.run(Integer.parseInt(value) + 1);
     			}})
     		.setOnYieldListener(new OnYieldListener() {
     			@Override
     			public void onYield(int code, Bundle bundle) {
     				ThreadUtils.checkMainThread();
     				Log.e(TAG, "onYield code:" + code);
     			}})
     		.setCallback(new Callback<Integer>() {
     			@Override
     			public void onSuccess(Integer result) {
     				ThreadUtils.checkMainThread();
     				Log.e(TAG, "setCallback:" + result);
     				Toast.makeText(MainActivity.this, "Completed all tasks:" + result, Toast.LENGTH_SHORT).show();
     			}

     			@Override
     			public void onFailure(Bundle result, Exception exp) {
     				ThreadUtils.checkMainThread();
     				Toast.makeText(MainActivity.this, "Failed some task", Toast.LENGTH_SHORT).show();
     				Log.e(TAG, "onFailure:"+exp.getMessage() + "", exp);
     			}})
     		.create().execute(1);

       //销毁
        @Override
            protected void onPause() {
                Promise.destroyWith(this);
                super.onPause();
            }
```