# ClearTV ProGuard/R8 Rules

# ── Compose ──────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Kotlin Serialization ─────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

# Keep @Serializable data classes
-keepclassmembers @kotlinx.serialization.Serializable class com.cleartv.** {
    *;
}
-keep class com.cleartv.data.model.** { *; }
-keep class com.cleartv.data.OpenMeteoResponse { *; }
-keep class com.cleartv.data.CurrentResponse { *; }
-keep class com.cleartv.data.DailyResponse { *; }
-keep class com.cleartv.data.GeoResponse { *; }
-keep class com.cleartv.data.GeoResult { *; }

# ── Ktor ─────────────────────────────────────────────────────────────
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep CIO engine
-keep class io.ktor.client.engine.cio.** { *; }

# SLF4J (Ktor optional dependency)
-dontwarn org.slf4j.**

# ── DataStore ────────────────────────────────────────────────────────
-keep class androidx.datastore.** { *; }
