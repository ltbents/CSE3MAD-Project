package com.example.project3;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class ApiClient {
    private static final String BASE_URL = "http://your-backend-url";
    private static Retrofit retrofit;

    public static Retrofit getApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public interface ApiInterface {
        @POST("change-password")
        Call<ResponseBody> changePassword(@Body PasswordChangeRequest request);

        @POST("logout")
        Call<ResponseBody> logout(@Header("Authorization") String token);
    }

    public static class PasswordChangeRequest {
        private String newPassword;

        public PasswordChangeRequest(String newPassword) {
            this.newPassword = newPassword;
        }
    }

}

