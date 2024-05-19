package com.example.project3;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    public static final String TAG = "ProfileFragment";

    public ProfileFragment(){
        class ChangePasswordActivity extends AppCompatActivity {

            private EditText newPasswordEditText;
            private Button changePasswordButton;
            private ApiClient.ApiInterface apiInterface;

            @SuppressLint("MissingInflatedId")
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.fragment_profile);

                newPasswordEditText = findViewById(R.id.textEditPassword);
                changePasswordButton = findViewById(R.id.profile_ChangePassword);

                apiInterface = ApiClient.getApiClient().create(ApiClient.ApiInterface.class);

                changePasswordButton.setOnClickListener(v -> {
                    String newPassword = newPasswordEditText.getText().toString();
                    ApiClient.PasswordChangeRequest request = new ApiClient.PasswordChangeRequest(newPassword);

                    Call<ResponseBody> call = apiInterface.changePassword(request);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(ChangePasswordActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        }
        class MainActivity extends AppCompatActivity {
            private ApiClient.ApiInterface apiInterface;
            private String token;

            @SuppressLint("MissingInflatedId")
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                apiInterface = ApiClient.getApiClient().create(ApiClient.ApiInterface.class);
                token = "Bearer " /* retrieve saved token */;

                findViewById(R.id.profile_Logout).setOnClickListener(v -> {
                    Call<ResponseBody> call = apiInterface.logout(token);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                // Clear saved token and redirect to login
                                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to log out", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        }


    }

}
