package com.example.doemaisui01.api;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ENDERECO DO BACK-END (a URL SEMPRE termina com "/")
    //  - Genymotion (rede NAT):          http://10.0.3.2:8081/
    //  - Emulador do Android Studio:     http://10.0.2.2:8081/
    //  - Celular fisico / Genymotion em Bridge: http://IP-DO-SEU-PC:8081/
    private static final String BASE_URL = "http://10.0.3.2:8081/";

    private static ApiService instance;

    public static synchronized ApiService get() {
        if (instance == null) {
            Log.d("API", "BASE_URL = " + BASE_URL);

            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            // BODY mostra o JSON (inclusive senhas). Troque para NONE antes de apresentar.
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(8, TimeUnit.SECONDS)  // erro aparece em 8s se nao conectar
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(log)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return instance;
    }
}