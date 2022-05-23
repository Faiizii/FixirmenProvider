package com.fixirman.provider.view.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.FragmentMyAccountBinding;
import com.fixirman.provider.model.NormalResponse;
import com.fixirman.provider.model.user.User;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.view.activity.AboutUsActivity;
import com.fixirman.provider.view.activity.ChangeAddressActivity;
import com.fixirman.provider.view.activity.ContactUsActivity;
import com.fixirman.provider.view.activity.FAQActivity;
import com.fixirman.provider.view.activity.MainActivity;
import com.fixirman.provider.view.activity.TermsActivity;
import com.fixirman.provider.utils.Crashlytics;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private final String TAG = "MyAccountFragment";
    private FragmentActivity activity;

    private FragmentMyAccountBinding binding;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_account, container, false);
        binding.setFragment(this);
        activity = getActivity();
        initMe();
        return binding.getRoot();
    }

    private void initMe() {
        sessionManager = new SessionManager(activity);
    }

    private void updateUI(User user) {
        if(user != null){
            binding.tvName.setText(user.getName());
            binding.tvPhone.setText(user.getPhone());
            Glide.with(this).load(AppConstants.HOST_URL+user.getImage()).apply(
                    new RequestOptions().error(R.drawable.default_user)
                            .fitCenter())
                    .fitCenter()
                    .placeholder(AppUtils.getImageLoader(activity))
                    .into(binding.ivUserProfile);
        }else{
            Crashlytics.log("user is null at my account screen");
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.cv_profile:
            case R.id.tv_phone:
            case R.id.tv_name:
            case R.id.btn_editProfile:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new UserDetailFragment()).addToBackStack("user_detail").commit();
                break;
            case R.id.tv_myAddress:
                Intent iAddress = new Intent(activity, ChangeAddressActivity.class);
                startActivity(iAddress);
                break;
            case R.id.tv_contactUs:
                Intent contactUs = new Intent(activity, ContactUsActivity.class);
                startActivity(contactUs);
                break;
            case R.id.tv_privacyPolicy:
                Intent privacyPolicy = new Intent(activity, TermsActivity.class);
                startActivity(privacyPolicy);
                break;
            case R.id.tv_aboutUs:
                Intent aboutUs = new Intent(activity, AboutUsActivity.class);
                startActivity(aboutUs);
                break;
            case R.id.tv_faqs:
                Intent faq = new Intent(activity, FAQActivity.class);
                startActivity(faq);
                break;
            case R.id.mb_logout:
                if(AppUtils.isNetworkAvailable(activity)){
                    logoutOnServer();
                }else{
                    AppUtils.createNetworkError(activity);
                }
                break;
        }
    }
    private void logoutOnServer(){
        showLogoutProgress();
        ApiUtils.getApiInterface().logOut(sessionManager.getUserId(),AppConstants.TYPE_USER, Build.MANUFACTURER, Build.MODEL, "Android", Settings.Secure.ANDROID_ID,
                Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID), Build.VERSION.RELEASE,sessionManager.getToken(),
                "A", AppConstants.APP_VERSION).enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(@NonNull Call<NormalResponse> call, @NonNull Response<NormalResponse> response) {

                hideLogoutProgress();
                if(response.isSuccessful()){
                    NormalResponse normalResponse = response.body();
                    if(normalResponse.getSuccess() == 1){
                        ((MainActivity)activity).logout();
                    }else{
                        AppUtils.createFailedDialog(activity,response.message());
                    }
                }else{
                    ((MainActivity)activity).logout();
                    Crashlytics.log("code "+response.code());
                    Crashlytics.logException(new Exception("logout api at my account screen"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<NormalResponse> call, @NonNull Throwable t) {
                hideLogoutProgress();
                Crashlytics.log("logout api at my account screen");
                Crashlytics.logException(t);
                ((MainActivity)activity).logout();
            }
        });
    }
    private void showLogoutProgress(){
        binding.mbLogout.setText("");
        binding.mbLogout.setEnabled(false);
        AppUtils.showProgressBar(binding.pb);
    }

    private void hideLogoutProgress(){
        binding.mbLogout.setEnabled(true);
        binding.mbLogout.setText("Logout");
        AppUtils.hideProgressBar(binding.pb);
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI(sessionManager.getUser());
        //((MainActivity)activity).setSelectIndex(4);
        ((MainActivity)activity).visibleNavigationView(GONE);
    }
}
