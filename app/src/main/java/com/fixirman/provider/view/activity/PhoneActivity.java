package com.fixirman.provider.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.ActivityPhoneBinding;
import com.fixirman.provider.model.user.UserResponse;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.utils.Crashlytics;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.stream.MalformedJsonException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneActivity extends AppCompatActivity {
    private final String TAG = "PhoneActivity";

    private ActivityPhoneBinding binding;

    private String phoneNumber = "",code = "",verificationId = "";
    private String countryCode = "";

    private final int CODE_TIME_OUT = 60;
    private CountDownTimer timer;

    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private int TYPE = AppConstants.PHONE_ACTIVITY_CHECK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone);
        binding.setActivity(this);
        init();
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        binding.ccp.registerPhoneNumberTextView(binding.etPhone);
        if(getIntent() != null){
            TYPE = getIntent().getIntExtra("type", AppConstants.PHONE_ACTIVITY_CHECK);
        }else{
            Log.e(TAG, "init: type not found intent is null" );
        }
        initTimer();
        initCodeCallBack();
        initCountrySpinner();
        initOtpCompleteListener();
    }

    private void initOtpCompleteListener() {
        try {
            binding.otpView.setOtpCompletionListener(this::verifyCode);
        }catch (Exception e){
            Log.e(TAG, "initOtpCompleteListener: ",e );
            Crashlytics.logException(e);
        }
    }

    private void initCountrySpinner() {
        binding.ccp.setOnCountryChangeListener(selectedCountry -> {
            countryCode = "+"+selectedCountry.getPhoneCode();
            Log.e(TAG, "onCountrySelected: "+countryCode );
        });

    }

    private void initTimer() {
        timer = new CountDownTimer(1000*CODE_TIME_OUT, 1000) {
            @Override
            public void onTick(long l) {
                hideResendCode();
                binding.tvResendTimer.setText("Resend Code in "+(l/1000));
            }

            @Override
            public void onFinish() {
                showResendCode();
            }
        };
    }

    private void initCodeCallBack() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithCredential(phoneAuthCredential);
                hideSendCodeProgressBar();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Crashlytics.logException(e);
                hideSendCodeProgressBar();
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    binding.etPhone.setError("Invalid Mobile Number. Please check mobile format");
                }else{
                    binding.etPhone.setError("Verification Failed");
                }
                Log.e(TAG, "onVerificationFailed: ",e );
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, forceResendingToken);
                hideSendCodeProgressBar();
                verificationId = s;
                forceResendingToken = token;
                timer.cancel();
                timer.start();
                showCodeLayout();
            }
        };
    }



    public void onClick(View v){
        AppUtils.hideSoftKeyboard(this);
        switch (v.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_sendCode:
                if(AppUtils.isNetworkAvailable(this)){
                    if(phoneValidate()){
                        checkPhoneNumberExistenceOnServer();
                    }
                }else{
                    AppUtils.createNetworkError(this);
                }

                break;
            case R.id.tv_resendCode:
                if(AppUtils.isNetworkAvailable(this)){
                    resendCode();
                }else{
                    AppUtils.createNetworkError(this);
                }

                break;
            case R.id.btn_verify:
                if(AppUtils.isNetworkAvailable(this)){
                   if(validateCode()){
                       verifyCode(code);
                   }
                }else{
                    AppUtils.createNetworkError(this);
                }
                break;
        }
    }

    private boolean validateCode() {
        boolean check = true;
        try {
            code = binding.otpView.getText().toString().trim();
        }catch (Exception e){
            Log.e(TAG, "validateCode: ", e);
            Crashlytics.logException(e);
        }finally {
            if(code.isEmpty()){
                check = false;
               binding.tlCode.setError("Can't be Empty");
            }else if(code.length() < 6){
                check = false;
                binding.tlCode.setError("Invalid Code");
            }else{
                binding.tlCode.setError(null);
            }
        }

        return check;
    }

    private void resendCode() {
        if(forceResendingToken == null)
            return;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,CODE_TIME_OUT,TimeUnit.SECONDS,this,callbacks,forceResendingToken);
    }

    private boolean phoneValidate() {
        boolean check = true;
        phoneNumber = binding.etPhone.getText().toString().trim();
        phoneNumber =  phoneNumber.replaceAll("\\s+","");
        if(phoneNumber.length() >= 10){
            binding.etPhone.setError(null);
            if(countryCode.isEmpty())
               countryCode =  "+"+binding.ccp.getSelectedCountryCode();
            //phoneNumber = "+"+countryCode+ phoneNumber;
            phoneNumber = countryCode+ phoneNumber;
            Log.e(TAG, "phoneValidate: "+phoneNumber );
        }else{
            check = false;
            binding.etPhone.setError("Invalid Phone");
        }
        return check;
    }

    private void checkPhoneNumberExistenceOnServer(){
        if(phoneNumber.isEmpty())
            return;

        showSendCodeProgressBar();
        ApiUtils.getApiInterface().verifyMyPhone(phoneNumber,AppConstants.TYPE_USER,TYPE == AppConstants.PHONE_ACTIVITY_CHECK ? "check" : "sign_up",
                Build.MANUFACTURER, Build.MODEL, "Android", Settings.Secure.ANDROID_ID,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), Build.VERSION.RELEASE,new SessionManager(this).getToken(),
                "A", AppConstants.APP_VERSION)
                .enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call,@NonNull Response<UserResponse> response) {
                if(response.isSuccessful()){
                    UserResponse userResponse = response.body();
                    switch (TYPE){
                        case AppConstants.PHONE_ACTIVITY_SIGN_UP:
                            if(userResponse.getSuccess() == 1){
                                sendCode();
                            }else{
                                onApiCallFailed(userResponse.getMessage());
                            }
                            break;
                        case AppConstants.PHONE_ACTIVITY_FORGOT_PASSWORD:
                        case AppConstants.PHONE_ACTIVITY_ADD_NEW_NUMBER:
                        case AppConstants.PHONE_ACTIVITY_CHECK:
                            if(userResponse.getSuccess() == 0){
                                sendCode();
                            }else{
                                onApiCallFailed(userResponse.getMessage());
                            }
                            break;
                        default:
                            onApiCallFailed(userResponse.getMessage());
                    }
                }else{
                    onApiCallFailed("Server Connection Error");
                    Crashlytics.log("response code "+response.code());
                    Crashlytics.logException(new Exception("verify my phone: "+response.message()));
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
                Crashlytics.log("very my phone api call with network status "+AppUtils.isNetworkAvailable(PhoneActivity.this));
                Crashlytics.logException(t);
            }
        });
    }
    private void onApiCallFailed(String message) {
        hideSendCodeProgressBar();
        AppUtils.createFailedDialog(this,message);
    }

    private void sendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,CODE_TIME_OUT, TimeUnit.SECONDS,this,callbacks);
    }
    private void verifyCode(String otp) {
        AppUtils.hideSoftKeyboard(this);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otp);
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        showVerifyProgressBar();
        auth.signInWithCredential(phoneAuthCredential).addOnSuccessListener(authResult -> {
         //navigate to next
            hideVerifyProgressBar();
            Intent i = null;
            if(TYPE == AppConstants.PHONE_ACTIVITY_FORGOT_PASSWORD){
                i = new Intent(PhoneActivity.this,ForgotPasswordActivity.class);
            }else if(TYPE == AppConstants.PHONE_ACTIVITY_SIGN_UP){
                i = new Intent(PhoneActivity.this,UserRegisterActivity.class);
            }else if(TYPE == AppConstants.PHONE_ACTIVITY_CHECK){
                i = new Intent(this,MainActivity.class);
            }
            if(i != null){
                i .putExtra("phone",phoneNumber);
                startActivity(i);
                this.finish();
            }else{
                Log.e(TAG, "signInWithCredential: intent null no type found" );
            }
        }).addOnFailureListener(e ->{
            hideVerifyProgressBar();
            if(e instanceof FirebaseAuthInvalidCredentialsException){
                binding.tlCode.setError("Invalid Code! Please Enter the Correct Code");
            }
            Log.e(TAG, "onFailure: ",e );
            Crashlytics.logException(e);
            //hideProgressBarVerify();
        });
    }

    @Override
    public void onBackPressed() {
        if(binding.llVerifyCode.getVisibility() == View.VISIBLE){
            timer.cancel();
            showPhoneLayout();
        }else {
            super.onBackPressed();
        }
    }

    private void showPhoneLayout(){
        binding.llVerifyCode.setVisibility(View.GONE);
        binding.llSendCode.setVisibility(View.VISIBLE);
        binding.btnSendCode.setVisibility(View.VISIBLE);
    }
    private void showCodeLayout(){
        binding.llSendCode.setVisibility(View.GONE);
        binding.llVerifyCode.setVisibility(View.VISIBLE);
        binding.btnSendCode.setVisibility(View.GONE);
    }
    private void showResendCode(){
        binding.tvResendTimer.setVisibility(View.GONE);
        binding.llResendCode.setVisibility(View.VISIBLE);
    }
    private void hideResendCode(){
        binding.llResendCode.setVisibility(View.GONE);
        binding.tvResendTimer.setVisibility(View.VISIBLE);
    }

    private void showSendCodeProgressBar(){
        binding.btnSendCode.setEnabled(false);
        binding.btnSendCode.setText("");
        AppUtils.showProgressBar(binding.pbSendCode);
    }
    private void hideSendCodeProgressBar(){
        binding.btnSendCode.setEnabled(true);
        binding.btnSendCode.setText(getResources().getString(R.string.send_code));
        AppUtils.hideProgressBar(binding.pbSendCode);
    }
    private void showVerifyProgressBar(){
        binding.btnVerify.setEnabled(false);
        binding.btnVerify.setText("");
        AppUtils.showProgressBar(binding.pbVerifyCode);
    }
    private void hideVerifyProgressBar(){
        binding.btnVerify.setEnabled(true);
        binding.btnVerify.setText(getResources().getString(R.string.verify));
        AppUtils.hideProgressBar(binding.pbVerifyCode);
    }
}
