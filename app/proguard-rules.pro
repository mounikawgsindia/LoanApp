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

-keep interface com.wingspan.loanapp.data.networks.** { @retrofit2.http.* <methods>; }

-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
# Keep your model classes and Gson annotations
-keep class com.wingspan.loanapp.data.** { *; }
-keepclassmembers class com.wingspan.loanapp.data.** { <fields>; }

# Keep Gson
-keep class com.google.gson.** { *; }

-dontwarn com.google.gson.**

# ViewModels & Repositories
-keep class com.wingspan.loanapp.ui.theme.registration.** { *; }

-keep class com.wingspan.loanapp.data.repository.** { *; }

# Keep classes extending ViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }

# App utilities and logger
-keep class com.wingspan.loanapp.utils.** { *; }


# Keep attributes needed for Gson, Retrofit, and Kotlin
-keepattributes Signature, *Annotation*
-keepattributes Exceptions, InnerClasses, EnclosingMethod,
RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations,
RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations,
MethodParameters

-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class com.wingspan.loanapp.utils.ApiResult { *; }
-keep class com.wingspan.loanapp.utils.ApiResult* { *; }