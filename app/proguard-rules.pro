# Regras ProGuard/R8 — complementadas em T039 com regras de release completas.

# Manter classes anotadas com @Serializable (Kotlinx Serialization)
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Manter entidades Room (@Entity)
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Manter classes geradas pelo Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.* { *; }

# Firebase — evitar ofuscação de classes internas
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# OkHttp — evitar warning de platform
-dontwarn okhttp3.**
-dontwarn okio.**

# Retrofit — manter anotações
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep,allowshrinking,allowoptimization interface com.squareup.retrofit2.** { *; }

# Kotlinx Serialization — manter companion object com serializer()
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# WorkManager — manter workers para reflexão
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Hilt Workers (@HiltWorker) — manter fábrica gerada
-keep class dagger.hilt.android.internal.lifecycle.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

# Play Services Location — evitar warnings
-dontwarn com.google.android.gms.location.**

# Coroutines — manter classes de debug
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**
