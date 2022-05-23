package com.fixirman.provider.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.fixirman.R;
import com.app.fixirman.databinding.DialogSelectCategoryBinding;
import com.fixirman.provider.adapter.CategoryAdapter;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.ActivityUserRegisterBinding;
import com.fixirman.provider.model.user.UserResponse;
import com.fixirman.provider.my_interfaces.AdapterOnClickListener;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.utils.Crashlytics;
import com.fixirman.provider.viewmodel.CategoryViewModel;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {
    private final String TAG = "UserRegister";

    private ActivityUserRegisterBinding binding;
    private String phone,email,password,fullName,cnic;
    private SessionManager sessionManager;
    private List<Integer> selectedItems;
    @Inject
    ViewModelProvider.Factory factory;
    private CategoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
         binding = DataBindingUtil.setContentView(this,R.layout.activity_user_register);
         binding.setActivity(this);
         initMe();
    }

    private void initMe() {
        selectedItems = new ArrayList<>();
        sessionManager = new SessionManager(this);
        phone = getIntent().getStringExtra("phone");
        viewModel = new ViewModelProvider(this,factory).get(CategoryViewModel.class);
        if(phone == null){
            phone = "";
        }
        binding.etPhone.setText(phone);
        showSelectCategory();
    }

    private void showSelectCategory() {
        Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        DialogSelectCategoryBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.dialog_select_category,null,false);
        dialog.setContentView(dialogBinding.getRoot());
        CategoryAdapter categoryAdapter = new CategoryAdapter(this,viewModel.getCategoryList(this),selectedItems);
        categoryAdapter.itemClickListener((view, Position) -> {

        });
        dialogBinding.rvCategories.setAdapter(categoryAdapter);

        dialogBinding.btnDone.setOnClickListener(v->{
            if(selectedItems.size() < 1){
                Toast.makeText(this, "Please select a category first", Toast.LENGTH_SHORT).show();
            }else{
                dialog.dismiss();
                Log.e(TAG, "showSelectCategory: selectedItems: "+selectedItems.toString() );
            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }

    public void onClick(View v){
        if (v.getId() == R.id.btn_register) {
            if (AppUtils.isNetworkAvailable(this)) {
                if (validate()) {
                    signUpUser();
                }
            } else {
                AppUtils.createNetworkError(this);
            }
        }
    }

    private void signUpUser() {
        showProgressBar();
        Log.e(TAG, "signUpUser: items: "+new Gson().toJson(selectedItems));
        ApiUtils.getApiInterface().registerUser(AppConstants.TYPE_USER,phone,password,email,cnic,fullName,"",new Gson().toJson(selectedItems),"","","", Build.MANUFACTURER, Build.MODEL, "Android", Settings.Secure.ANDROID_ID,
                Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), Build.VERSION.RELEASE,sessionManager.getToken(),
                "A","phone",AppConstants.APP_VERSION).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call,@NonNull Response<UserResponse> response) {
                hideProgressBar();
                if(response.isSuccessful()){
                    UserResponse userResponse = response.body();
                    if(userResponse.getSuccess() == 1){
                        sessionManager.saveUserLogin(phone,password);
                        AlertDialog alertDialog = new AlertDialog.Builder(UserRegisterActivity.this).create();
                        alertDialog.setTitle("Thank your!");
                        alertDialog.setMessage(userResponse.getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, which) -> {
                            openLoginScreen();
                        });
                        alertDialog.show();
                    }else{
                        onApiCallFailed(userResponse.getMessage());
                    }
                }else{
                    onApiCallFailed("Server Connection Error");
                    Crashlytics.log("response code "+response.code());
                    Crashlytics.logException(new Exception("register user: "+response.message()));
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
                Crashlytics.log("register user api call with network status "+AppUtils.isNetworkAvailable(UserRegisterActivity.this));
                Crashlytics.logException(t);
            }
        });
    }

    private void openLoginScreen() {
        Intent i = new Intent(this,UserLoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void onApiCallFailed(String message) {
        hideProgressBar();
        AppUtils.createFailedDialog(this,message);
    }
    private void hideProgressBar() {
        binding.btnRegister.setText("Register");
        binding.btnRegister.setEnabled(true);
        AppUtils.hideProgressBar(binding.pb);
    }
    private void showProgressBar(){
        binding.btnRegister.setText("");
        binding.btnRegister.setEnabled(false);
        AppUtils.showProgressBar(binding.pb);
    }

    public boolean validate(){
        boolean check = true;
        email = binding.etEmail.getText().toString().trim();
        cnic = binding.etCnic.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        fullName = binding.etName.getText().toString().trim();

        if(email.isEmpty()){
            check = false;
            binding.ilEmail.setError("Email is empty");
        }else{
            binding.ilEmail.setError(null);
        }

        if(cnic.isEmpty()){
            check = false;
            binding.ilCnic.setError("CNIC is empty");
        }else{
            binding.ilCnic.setError(null);
        }

        if(fullName.isEmpty()){
            check = false;
            binding.ilName.setError("Full name is empty");
        }else{
            binding.ilName.setError(null);
        }


        if(password.isEmpty()){
            check = false;
            binding.ilPassword.setError("Password is empty");
        }else{
            if(password.length() < 4){
                check = false;
                binding.ilPassword.setError("Password is too short");
            }else{
                if(confirmPassword.equals(password)){
                    binding.ilPassword.setError(null);
                    binding.ilConfirmPassword.setError(null);
                }else{
                    check = false;
                    binding.ilConfirmPassword.setError("Password mismatch");
                }
            }
        }
        if(phone.isEmpty()){
            check = false;
            Toast.makeText(this, "Invalid phone, Try Again Later", Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure you want to exit?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes", (dialog, which) -> {
            super.onBackPressed();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No",(dialogInterface, i) -> alertDialog.dismiss());
        alertDialog.show();
    }
}
