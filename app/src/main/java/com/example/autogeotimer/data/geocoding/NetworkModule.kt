package com.example.autogeotimer.data.geocoding

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE // TODO: switch to NONE for release builds
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
        // .eventListener(object : okhttp3.EventListener() {
        //     override fun dnsStart(call: okhttp3.Call, domainName: String) {
        //         android.util.Log.d("OkHttpDebug", "DNS start for: $domainName")
        //     }
        //     override fun dnsEnd(call: okhttp3.Call, domainName: String, inetAddressList: List<java.net.InetAddress>) {
        //         android.util.Log.d("OkHttpDebug", "DNS success: $inetAddressList")
        //     }
        // })

    val geocodingApi: GoogleGeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GoogleGeocodingApi::class.java)
    }

}