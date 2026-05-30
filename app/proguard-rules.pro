# ---- Debugging ----
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Navigation 3 route classes ----
# These are used as type-safe keys in the backstack
-keep class com.ovasta.logisticsapp.presentation.nav.Splash { *; }
-keep class com.ovasta.logisticsapp.presentation.nav.Login { *; }
-keep class com.ovasta.logisticsapp.presentation.nav.Home { *; }
-keep class com.ovasta.logisticsapp.presentation.nav.AvailableTasks { *; }
-keep class com.ovasta.logisticsapp.presentation.nav.TaskDetails { *; }

# ---- Gson: keep @SerializedName fields (Gson 2.11+ handles this, but explicit for safety) ----
# APIException extends IOException and is deserialized via Gson
-keep class com.ovasta.logisticsapp.base.exception.APIException { <fields>; }

# ---- kotlinx.serialization ----
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class com.ovasta.logisticsapp.** {
    kotlinx.serialization.KSerializer serializer(...);
}
