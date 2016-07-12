# 指定代码的压缩级别
-optimizationpasses 5

# 包明不混合大小写
-dontusemixedcaseclassnames

# 不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 # 优化不优化输入的类文件
-dontoptimize

 # 预校验
-dontpreverify

 # 混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
# 如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment



# 忽略警告
-ignorewarning

#记录生成的日志数据,gradle build时在本项目根目录输出

# apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 apk 中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt

#记录生成的日志数据，gradle build时 在本项目根目录输出-end


#####混淆保护自己项目的部分代码以及引用的第三方jar包library

#-libraryjars libs/umeng-analytics-v5.2.4.jar

#三星应用市场需要添加:sdk-v1.0.0.jar,look-v1.0.1.jar
#-libraryjars libs/sdk-v1.0.0.jar
#-libraryjars libs/look-v1.0.1.jar

#如果不想混淆 keep 掉
-keep class com.lippi.recorder.iirfilterdesigner.** {*; }
#友盟
-keep class com.umeng.**{*;}
#项目特殊处理代码

#忽略警告
-dontwarn com.lippi.recorder.utils**
#保留一个完整的包
-keep class com.lippi.recorder.utils.** {
    *;
 }

-keep class  com.lippi.recorder.utils.AudioRecorder{*;}


#如果引用了v4或者v7包
-dontwarn android.support.**

#混淆保护自己项目的部分代码以及引用的第三方jar包library-end

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
#  public static **[] values();
#  public static ** valueOf(java.lang.String);
#

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#避免混淆泛型 如果混淆报错建议关掉
#–keepattributes Signature

#移除log 测试了下没有用还是建议自己定义一个开关控制是否输出日志
#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#

#如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
#gson
#-libraryjars libs/gson-2.2.2.jar
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }


#-dontwarn android.support.**

# http client 文件上传
-keep class org.apache.http.** {*; }

# 百度地图
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

# JPush 相关
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

# litepal混淆
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepattributes *Annotation*
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}

# ButterKnife混淆
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# smack混淆
-dontwarn org.jivesoftware.smack.**
-dontwarn org.jivesoftware.smackx.**
-keep class org.jivesoftware.smack.** { *;}
-keep class org.jivesoftware.smackx.** { *;}


#-libraryjars libs/jxmpp-core-0.4.2.jar
#-libraryjars libs/jxmpp-util-cache-0.4.2.jar
#-libraryjars libs/smack-android-4.1.7.jar
#-libraryjars libs/smack-im-4.1.7.jar
#-libraryjars libs/smack-sasl-provided-4.1.7.jar
#-libraryjars libs/smack-tcp-4.1.7.jar

#-libraryjars libs/smack-core-4.1.7.jar

#-libraryjars libs/org.xbill.dns_2.1.7.jar
#-libraryjars libs/pinyin4j-2.5.0.jar
#-libraryjars libs/litepal-1.3.1.jar
#-libraryjars libs/mail.jar
#-libraryjars libs/httpmime-4.3.4.jar
#-libraryjars libs/BaiduLBS_Android.jar
#-libraryjars libs/additionnal.jar
#-libraryjars libs/xUtils-2.6.8.jar
#-libraryjars libs/zixing-core-3.2.0.jar
#-libraryjars libs/httpcore-4.3.jar
#-libraryjars libs/activation.jar
#-libraryjars libs/minidns-0.1.3.jar
#-libraryjars libs/IndoorscapeAlbumPlugin.jar
#-libraryjars libs/smack-extensions-4.1.6.jar
