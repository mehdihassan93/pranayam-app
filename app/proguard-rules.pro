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

# Keep fields annotated with @SerializedName from being removed/renamed
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic type info for Retrofit (needed for Response<T> deserialization)
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep Room entities
-keep class com.pranayam.app.data.local.entity.** { *; }

# Keep Hilt-generated code
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
