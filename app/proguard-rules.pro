# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Keep Gson
-keepattributes *Annotation*
-keep class com.pranayam.app.data.model.** { *; }
-keep class com.pranayam.app.model.** { *; }
-keep class com.pranayam.app.api.** { *; }
