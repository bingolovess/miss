# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#聚合SDK
-keep class com.domob.sdk.unionads.splash.UnionSplashAD{
	    public	<methods>;
}
-keep class com.domob.sdk.unionads.splash.UnionSplashAdListener{
        public	<methods>;
}
-keep class com.domob.sdk.common.util.AdError{
	    public	<methods>;
}
#domob开屏SDK
-keep	class com.dm.sdk.ads.splash.**{
        public	<methods>;
}
-keep	class com.dm.sdk.common.util.AdError{
	    public	<methods>;
}
-keep	class com.dm.sdk.ads.DMAdActivity{
	    public	<methods>;
}
#gdt SDK
-keep	class com.qq.e.**{public protected	*;}

#dpush
-keep class com.domob.sdk.DPush{
		public	<methods>;
}
-keep class com.domob.sdk.DPushMessageReceiver{
		public	<methods>;
}
-keep class com.domob.sdk.service.DPushService{
		*;
}
-keep class com.domob.sdk.activity.DPushActivity{
		*;
}
-keep class com.domob.sdk.DPushNotificationReceiver{
		*;
}