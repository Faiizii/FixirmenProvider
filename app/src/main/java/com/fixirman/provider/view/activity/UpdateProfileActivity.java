package com.fixirman.provider.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.fixirman.provider.api.params.HttpParams;
import com.app.fixirman.databinding.ActivityUpdateProfileBinding;
import com.fixirman.provider.model.user.User;
import com.fixirman.provider.model.user.UserResponse;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.utils.UploadImageUtils;
import com.fixirman.provider.viewmodel.UserViewModel;
import com.fixirman.provider.utils.Crashlytics;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private final String TAG = "UpdateProfile";

    private ActivityUpdateProfileBinding binding;
    private UploadImageUtils uploadImageUtils;
    private SessionManager sessionManager;
    private int MY_REQUEST_CODE = 121;
    private String name,email,phone,cnic,description;
    private int type;

    @Inject
    ViewModelProvider.Factory factory;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update_profile);
        binding.setActivity(this);
        initMe();
    }

    private void initMe() {
        sessionManager = new SessionManager(this);
        uploadImageUtils = new UploadImageUtils(this,"profile_image.png");
        updateUI(sessionManager.getUser());
        if(getIntent() != null){
            type = getIntent().getIntExtra("type", AppConstants.UPDATE_PROFILE);
        }
        updateButtonText();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this,factory).get(UserViewModel.class);
    }

    private void updateUI(User user) {
        if(user != null) {
            binding.etFullName.setText(user.getName());
            binding.etEmailAddress.setText(user.getEmail());
            binding.etPhoneNumber.setText(user.getPhone());
            binding.etCnic.setText(user.getCnic());
            binding.etDescription.setText(user.getDescription());
            Glide.with(this).load(AppConstants.HOST_URL + user.getImage()).apply(
                    new RequestOptions().error(R.drawable.default_user)
                            .fitCenter())
                    .fitCenter()
                    .placeholder(AppUtils.getImageLoader(this))
                    .into(binding.ivProfileImage);
        }
    }


    public void onClick(View v){
        AppUtils.hideSoftKeyboard(this);
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.iv_camera || id == R.id.cv_profileImage) {
            selectImage();
        } else if (id == R.id.btn_updateInfo) {
            if (AppUtils.isNetworkAvailable(this)) {
                if (validate()) {
                    updateProfile();
                }
            } else {
                AppUtils.createNetworkError(this);
            }
        }
    }
    private void updateProfile() {

        showProgressBar();
        ApiUtils.getApiInterface().updateProfile(AppUtils.getStringRequestBody(sessionManager.getUserId()+""),AppUtils.getStringRequestBody(name),
                AppUtils.getStringRequestBody(phone),AppUtils.getStringRequestBody(email),AppUtils.getStringRequestBody(cnic),
                AppUtils.getStringRequestBody(description),AppUtils.getStringRequestBody(AppConstants.TYPE_USER),
                uploadImageUtils.getImageBodyPart(HttpParams.USER_IMAGE)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                hideProgressBar();
                if(response.isSuccessful()){
                    UserResponse userResponse = response.body();
                    if(userResponse.getSuccess() == 1){
                        User user = userResponse.getUser();
                        if(user != null){
                            sessionManager.saveUser(user);
                            if(user.getUserAddress() != null){
                                viewModel.clearUserAddresses(user.getId());
                                viewModel.insertAddress(user.getUserAddress());
                            }else{
                                Crashlytics.log("info: user address list is null in update user profile api");
                            }
                            onBackPressed();
                        }else{
                            Crashlytics.log("info: user object is null in update user profile api");
                            Crashlytics.log("message: "+response.message());
                        }
                    }else{
                        showFailedDialog(userResponse.getMessage());
                    }
                }else{
                    Crashlytics.log("code: "+response.code());
                    Crashlytics.log("message: "+response.message());
                    Crashlytics.logException(new Exception("Update profile connection exception with code:"+response.code()));
                    showFailedDialog("Server Connection Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call,@NonNull Throwable t) {
                hideProgressBar();
                Crashlytics.log(TAG);
                Crashlytics.logException(t);
                if(t instanceof SocketTimeoutException){
                    showFailedDialog("Slow Internet Connection");
                }else{
                    showFailedDialog("Server Error");
                }
            }
        });
    }

    private void showFailedDialog(String message) {
        AppUtils.createFailedDialog(this,message);
    }

    private void hideProgressBar() {
        AppUtils.hideProgressBar(binding.pb);
        updateButtonText();
        binding.btnUpdateInfo.setEnabled(true);
    }

    private void showProgressBar() {
        binding.btnUpdateInfo.setText("");
        binding.btnUpdateInfo.setEnabled(false);
        AppUtils.showProgressBar(binding.pb);
    }
    private void updateButtonText(){
        if(type == AppConstants.UPDATE_PROFILE){
            binding.btnUpdateInfo.setText("Update Info");
        }else{
            binding.btnUpdateInfo.setText("Complete Info");
        }
    }

    private boolean validate() {
        boolean check = true;
        name = binding.etFullName.getText().toString().trim();
        phone = binding.etPhoneNumber.getText().toString().trim();
        email = binding.etEmailAddress.getText().toString().trim();
        cnic = binding.etCnic.getText().toString().trim();
        description = binding.etDescription.getText().toString().trim();

        if(name.isEmpty()){
            binding.ilFullName.setError("Can't be empty");
            check = false;
        }else{
            binding.ilFullName.setError(null);
        }

        if(phone.isEmpty()){
            binding.ilPhoneNumber.setError("Can't be empty");
            check = false;
        }else{
            binding.ilPhoneNumber.setError(null);
        }

        if(cnic.isEmpty()){
            binding.ilCnic.setError("Can't be empty");
            check = false;
        }else{
            binding.ilCnic.setError(null);
        }
        if(description.isEmpty()){
            binding.ilDescription.setError("Can't be empty");
            check = false;
        }else{
            binding.ilDescription.setError(null);
        }
        return check;
    }

    private void selectImage() {
        boolean result = AppUtils.checkPermissionCamera(this, AppUtils.REQUEST_PERMISSION_CAMERA,AppConstants.REQUEST_PERMISSION_TYPE_ACTIVITY,null);
        if (!result) return;
        boolean result1 = AppUtils.checkPermissionWrite(this, AppUtils.REQUEST_PERMISSION_WRITE, AppConstants.REQUEST_PERMISSION_TYPE_ACTIVITY,null);
        if (!result1) return;
        startActivityForResult(uploadImageUtils.getPickImageChooserIntent(), MY_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppUtils.REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    final boolean result = AppUtils.checkPermissionCamera(this, AppUtils.REQUEST_PERMISSION_CAMERA, AppConstants.REQUEST_PERMISSION_TYPE_ACTIVITY,null);
                    if (!result) return;
                    else {
                        selectImage();
                    }
                }
                break;
            case AppUtils.REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean result1 = AppUtils.checkPermissionWrite(this, AppUtils.REQUEST_PERMISSION_WRITE,AppConstants.REQUEST_PERMISSION_TYPE_ACTIVITY,null);
                    if (!result1) return;
                    else {
                        selectImage();
                    }

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == MY_REQUEST_CODE){
                try {
                    uploadImageUtils.prepareUploadData(data);
                    Glide.with(this).load(uploadImageUtils.getMyBitmap()).into(binding.ivProfileImage);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }else{
                Toast.makeText(this, "Result Unknown", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.e(TAG, "onActivityResult: no image selected");
            //Toast.makeText(activity, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
}