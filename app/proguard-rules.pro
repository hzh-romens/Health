# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zhoulisi/android/adt-bundle-mac-x86_64-20131030/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
##--------------Generalscan-------------##
#-dontwarn com.generalscan.**
#-keep class com.generalscan.**{*;}
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames#混淆时不会产生形形色色的类名
-dontskipnonpubliclibraryclasses#指定不去忽略非公共的类库
-dontpreverify#不预校验
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*//优化

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#保护指定的类和类的成员的名称，如果所有指定的类成员出席
-keepclasseswithmembernames class * {
    native <methods>;
}

#保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}


-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保护指定类的成员，如果此类受到保护他们会保护的更好
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保护指定的类文件和类成员
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepattributes Signature
-keepattributes *Annotation*

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**

#-dontwarn com.romens.rcp.**
#-keep class com.romens.rcp.** { *; }
#-keep interface com.romens.rcp.** { *; }

#android.support.v4
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

#android support v7
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**

##-------------------- Google Common -------------##
-dontwarn com.google.common.**
-keep class com.google.common.** { *; }

#-dontwarn com.romens.extend.chart.charts.**
#-keep class com.romens.extend.chart.charts.** { *; }


##------------------Gson----------------##
# Gson specific classes
-dontwarn com.google.gson.**
-keep   class sun.misc.Unsafe { *; }
-keep   class com.google.gson.stream.** { *; }
-keep   class com.google.gson.reflect.** { *; }

#library
-dontwarn com.romens.android.library.datetimepicker.**
-keep public class com.romens.android.library.datetimepicker.**{*;}


-keep class com.google.zxing.**{*;}
-dontwarn com.romens.extend.scanner.**
-keep public class com.romens.extend.scanner.**{*;}

##蒲公英
-dontwarn com.pgyersdk.**
-keep class com.pgyersdk.** { *; }

-dontwarn com.prolificinteractive.materialcalendarview.**
-keep class com.prolificinteractive.materialcalendarview.**{*;}

-dontwarn com.rengwuxian.materialedittext.**
-keep class com.rengwuxian.materialedittext.**{*;}

-dontwarn com.gc.materialdesign.**
-keep class com.gc.materialdesign.**{*;}

-dontwarn de.greenrobot.dao.**
-keep class de.greenrobot.dao.**{*;}

-dontwarn de.greenrobot.common.**
-keep class de.greenrobot.common.**{*;}


-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.**{*;}

-dontwarn java.nio.**
-keep class java.nio.**{*;}

-dontwarn okio.**
-keep class okio.**{*;}


#jackson
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.core.**
-dontwarn com.fasterxml.jackson.annotation.**
-dontwarn com.fasterxml.jackson.databind.**

-keep class fasterxml.jackson.core.** { *; }
-keep class fasterxml.jackson.annotation.** { *; }
-keep class fasterxml.jackson.databind.** { *; }

-keepclassmembers public final enum  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }

#romens.android
-dontwarn com.fima.cardsui.**
-keep class com.fima.cardsui.**{*;}

-dontwarn com.jakewharton.disklrucache.**
-keep class com.jakewharton.disklrucache.**{*;}

-dontwarn com.mikepenz.aboutlibraries.**
-keep class com.mikepenz.aboutlibraries.**{*;}

#glide
-dontwarn com.bumptech.glide.**
-keep class  com.bumptech.glide.**{*;}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


#高德相关混淆文件
#如果有其它包有warning，在报出warning的包加入下面类似的-dontwarn 报名
-dontwarn com.amap.api.**
-dontwarn com.aps.**
#3D 地图
-keep   class com.amap.api.maps2d.**{*;}
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
#Location
-keep   class com.amap.api.location.**{*;}
-keep   class com.aps.**{*;}
#Service
-keep   class com.amap.api.services.**{*;}


#----------extend library ------------------#
#环信
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9后的不需要加下面这个keep
#-keep class org.xbill.DNS.** {*;}
#另外，demo中发送表情的时候使用到反射，需要keep SmileUtils,注意前面的包名，
#不要SmileUtils复制到自己的项目下keep的时候还是写的demo里的包名
-keep class com.easemob.chatuidemo.utils.SmileUtils {*;}

#2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}


-dontwarn com.romens.android.**
-keep class com.romens.android.**{*;}

# library com.esotericsoftware.kryo
-keep class com.esotericsoftware.kryo.** { *; }
-dontwarn com.esotericsoftware.kryo.**
-dontwarn org.objenesis.instantiator.sun.**



#----------------- App Begin ------------------#

-keep public class com.romens.yjk.health.R$*{
public static final int *;
}

-keep class com.romens.yjk.health.model.**{*;}

-dontwarn com.romens.yjk.health.web.**
-keep class com.romens.yjk.health.web.**{*;}

-dontwarn com.romens.yjk.health.db.**
-keep public class com.romens.yjk.health.db.**{*;}

-dontwarn com.romens.yjk.health.ui.cells.**
-keep public class com.romens.yjk.health.ui.cells.**{*;}

-dontwarn com.romens.yjk.health.ui.components.**
-keep public class com.romens.yjk.health.ui.components.**{*;}

#romens.images
-dontwarn com.facebook.**
-keep class com.facebook.**{*;}

-keep class com.romens.images.**{
    public protected *;
}

##EventBus
#-dontwarn de.greenrobot.event.**
#-keep class de.greenrobot.event.**{*;}
##EventBus App Event
#-dontwarn com.romens.yjk.health.event.**
#-keep class com.romens.yjk.health.event.**{
#    public protected *;
#}

#pay
#wx
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}
-dontwarn com.tencent.mm.sdk.**
-keep class com.tencent.mm.sdk.**  {* ;}
#qq mta
-keep class com.tencent.stat.**  {* ;}
-keep class com.tencent.mid.**  {* ;}

#*****************************************************
# qq push and mta begin
# qq push
-dontwarn com.tencent.android.tpush.**
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.**  {* ;}
-keep class com.tencent.android.tpush.service.**  {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {* ;}

#   qq push and mta end
#************************************************************


-dontwarn com.romens.yjk.health.njxszk.wxapi.**
-keep class com.romens.yjk.health.njxszk.wxapi.**{
    public protected *;
}

-dontwarn com.romens.yjk.health.hyrmtt.wxapi.**
-keep class com.romens.yjk.health.hyrmtt.wxapi.**{
    public protected *;
}

-dontwarn com.romens.yjk.health.hyrmtt.pay.**
-keep class com.romens.yjk.health.hyrmtt.pay.**{
    public protected *;
}
