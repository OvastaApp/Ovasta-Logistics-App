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

# ---- kotlinx.serialization ----
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class com.ovasta.logisticsapp.** {
    kotlinx.serialization.KSerializer serializer(...);
}
