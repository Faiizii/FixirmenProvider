package com.fixirman.provider.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.ActivityUserLoginBinding;
import com.fixirman.provider.model.user.User;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.utils.Crashlytics;
import com.fixirman.provider.model.user.UserResponse;
import com.google.gson.stream.MalformedJsonException;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";

    private ActivityUserLoginBinding binding;
    private SessionManager sessionManager;

    private String phone,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_login);
        binding.setActivity(this);
        initMe();
    }

    private void initMe() {
        sessionManager = new SessionManager(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_login:
                if(AppUtils.isNetworkAvailable(this)){
                    if(validate()){
                        signInUser();
                    }
                }else{
                    AppUtils.createNetworkError(this);
                }
                break;
            case R.id.btn_signUp:
                openPhoneScreen(AppConstants.PHONE_ACTIVITY_SIGN_UP);
                break;
            case R.id.btn_forgotPassword:
                openPhoneScreen(AppConstants.PHONE_ACTIVITY_FORGOT_PASSWORD);
                break;
        }
    }

    private void signInUser() {
        showProgressBar();
        ApiUtils.getApiInterface().loginUser(phone,password,AppConstants.TYPE_USER ,Build.MANUFACTURER, Build.MODEL, "Android", Settings.Secure.ANDROID_ID,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), Build.VERSION.RELEASE,sessionManager.getToken(),
                "A", AppConstants.APP_VERSION).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call,@NonNull Response<UserResponse> response) {
                hideProgressBar();
                if(response.isSuccessful()){
                    UserResponse userResponse = response.body();
                    if(userResponse.getSuccess() == 1){
                        User user = userResponse.getUser();
                        if(user != null){
                            if("Y".equalsIgnoreCase(user.getIsVerified())){
                                sessionManager.saveUser(user);
                                sessionManager.saveUserLogin(phone,password);
                                openMainScreen();
                            }else{
                                onApiCallFailed("Your profile is under review. Please visit our nearby office");
                            }
                        }else{
                            Toast.makeText(UserLoginActivity.this, "Null user", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        onApiCallFailed(userResponse.getMessage());
                    }
                }else{
                    onApiCallFailed("Server Connection Error");
                    Crashlytics.log("response code "+response.code());
                    Crashlytics.logException(new Exception("login user: "+response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call,@NonNull Throwable t) {
                Log.e(TAG, "onFailure: ",t );
                if(t instanceof SocketTimeoutException){
                    onApiCallFailed("Slow Internet Connection");
                }else if(t instanceof MalformedJsonException){
                    onApiCallFailed("Invalid Server Response");
                }else{
                    onApiCallFailed("Server Error");
                }
                Crashlytics.log("login user api call with network status "+AppUtils.isNetworkAvailable(UserLoginActivity.this));
                Crashlytics.logException(t);
            }
        });
    }

    private void openMainScreen() {
        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void onApiCallFailed(String message) {
        hideProgressBar(); AppUtils.createFailedDialog(this,message);

    }

    private void hideProgressBar() {
        binding.btnLogin.setText("Login");
        binding.btnLogin.setEnabled(true);
        binding.btnSignUp.setEnabled(true);
        binding.btnForgotPassword.setEnabled(true);
        AppUtils.hideProgressBar(binding.pb);
    }
    private void showProgressBar(){
        binding.btnLogin.setText("");
        binding.btnLogin.setEnabled(false);
        binding.btnSignUp.setEnabled(false);
        binding.btnForgotPassword.setEnabled(false);
        AppUtils.showProgressBar(binding.pb);
    }

    private boolean validate() {
        boolean check = true;
        phone = binding.etPhone.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        if(phone.isEmpty()){
            binding.ilPhone.setError("Phone is empty");
            check = false;
        }else{
            binding.ilPhone.setError(null);
        }

        if(password.isEmpty()){
            binding.ilPass.setError("Password is empty");
            check = false;
        }else{
            binding.ilPass.setError(null);
        }
        return check;
    }

    private void openPhoneScreen(int type){
        Intent i = new Intent(this,PhoneActivity.class);
        i.putExtra("type", type);
        startActivity(i);
    }
    private void openSignUpScreen() {
        Intent i = new Intent(this,UserRegisterActivity.class);
        startActivity(i);
    }
}
