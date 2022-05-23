package com.fixirman.provider.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ProgressBar;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.ActivitySplashBinding;
import com.fixirman.provider.model.user.User;
import com.fixirman.provider.model.user.UserResponse;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.viewmodel.UserViewModel;
import com.fixirman.provider.utils.Crashlytics;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Inject
    ViewModelProvider.Factory factory;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        binding.setActivity(this);
        sessionManager = new SessionManager(this);
        initViewModel();
        initMe();
    }

    private void initMe() {
        if(sessionManager.isLogin()){
            checkUserLogin();
        }else{
            openUserLogin();
        }
    }


    private void initViewModel() {
        viewModel = new ViewModelProvider(this,factory).get(UserViewModel.class);
        viewModel.init(new ProgressBar(this),1);//1 us testing userId
    }

    private void checkUserLogin(){
        showProgress();
        ApiUtils.getApiInterface().loginUser(sessionManager.getPhone(),sessionManager.getPassword(), AppConstants.TYPE_USER,
                Build.MANUFACTURER, Build.MODEL, "Android", Settings.Secure.ANDROID_ID,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), Build.VERSION.RELEASE,sessionManager.getToken(),
                "A", AppConstants.APP_VERSION)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call,@NonNull Response<UserResponse> response) {
                        hideProgress();
                        if(response.isSuccessful()){
                            UserResponse userResponse = response.body();
                            if(userResponse.getSuccess() == 1){
                                if(userResponse.getUser() != null){
                                    User user = userResponse.getUser();
                                    if(user != null){
                                        sessionManager.saveUser(user);
                                        if("Y".equalsIgnoreCase(user.getIsVerified())){
                                            onUserLoginSuccess();
                                        }else{
                                            onApiCallFailed("Your Profile has not been Verified yet. Please visit out nearby office");
                                        }
                                    }
                                }else{
                                    openUserLogin();
                                }
                            }else{
                                openUserLogin();
                            }
                        }else{
                            showRetry();
                            Crashlytics.log("response code "+response.code());
                            Crashlytics.logException(new Exception("verify my phone: "+response.message()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call,@NonNull Throwable t) {
                        hideProgress();
                        Log.e(TAG, "onFailure: ",t );
                        showRetry();
                        Crashlytics.log("very my phone api call with network status "+ AppUtils.isNetworkAvailable(SplashActivity.this));
                        Crashlytics.logException(t);
                    }
                });
    }

    private void hideProgress() {

    }

    private void showProgress() {

    }

    private void showRetry() {

    }

    private void onApiCallFailed(String message) {
       AppUtils.createFailedDialog(this,message);
       //sessionManager.logoutUser();
    }

    private void onUserLoginSuccess(){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
    private void openUserLogin(){
        Intent i = new Intent(this,UserLoginActivity.class);
        startActivity(i);
    }
}
