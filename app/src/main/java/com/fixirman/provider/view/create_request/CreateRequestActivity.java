package com.fixirman.provider.view.create_request;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.ActivityCreateRequestBinding;
import com.fixirman.provider.model.categroy.Category;
import com.fixirman.provider.model.request.RequestResponse;
import com.fixirman.provider.model.user.UserAddress;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;

import java.net.SocketTimeoutException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRequestActivity extends AppCompatActivity {

    private final String TAG = "createRequest";

    private final String PAYMENT_CASH = "cash";
    private final String PAYMENT_CARD = "card";
    private final String PAYMENT_OTHER = "other";

    private ActivityCreateRequestBinding binding;

    @Inject
    ViewModelProvider.Factory factory;
    private RequestViewModel viewModel;

    private boolean isCreating = false;
    private String paymentMethod;
    private String itemStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_request);
        binding.setActivity(this);
        initMe();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new RequestSummaryFragment())
                    .commit();
        }

    }

    private void initMe() {
        paymentMethod = PAYMENT_CASH;
        viewModel = new ViewModelProvider(this,factory).get(RequestViewModel.class);
        checkIntent();
    }
    private void checkIntent() {
        try {
            Bundle bundle = getIntent().getExtras();
            Category category = bundle.getParcelable("object");
            if(category != null && viewModel != null){
                viewModel.addCategory(category);
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Select Category Again...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    public void onClick(View view){
        if(isCreating){
            return;
        }
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_createRequest:
                if(AppUtils.isNetworkAvailable(this)){
                    if(validate()){
                        createRequest();
                    }
                }else{
                    AppUtils.createNetworkError(this);
                }
                break;
        }
    }

    private void createRequest() {
        showProgressbar();
        UserAddress userAddress = viewModel.getAddress();
        ApiUtils.getApiInterface().createRequest(new SessionManager(this).getUserId(),paymentMethod,
                userAddress.getId(),userAddress.getAddress(),userAddress.getLatitude(),userAddress.getLongitude(),itemStr).enqueue(
                new Callback<RequestResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RequestResponse> call, @NonNull Response<RequestResponse> response) {
                        hideProgressbar();
                        if(response.isSuccessful()){
                            RequestResponse requestResponse = response.body();
                            if(requestResponse.getSuccess() == 1){
                                viewModel.saveRequestDetail(requestResponse.getRequestDetail());
                            }
                            Toast.makeText(CreateRequestActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            onApiCallFailed("Server Connection Error");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RequestResponse> call,@NonNull Throwable t) {
                        hideProgressbar();
                        Log.e(TAG, "onFailure: ",t );
                        if(t instanceof SocketTimeoutException){
                            onApiCallFailed("Slow Internet Connection");
                        }else if(t instanceof MalformedJsonException){
                            onApiCallFailed("Invalid Server Response");
                        }else{
                            onApiCallFailed("Server Error");
                        }
                    }
                }
        );
    }
    private void onApiCallFailed(String message) {
        AppUtils.createFailedDialog(this,message);
    }

    private boolean validate() {

        if(!viewModel.isItemsAdded()){
            Toast.makeText(this, "No job added", Toast.LENGTH_SHORT).show();
            return false;
        }
        itemStr = new Gson().toJson(viewModel.getRequestModels());
        Log.e(TAG, "validate: "+itemStr );
        if(viewModel.getAddress() == null){
            Toast.makeText(this, "Please select address details", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void showProgressbar(){
        isCreating = true;
        binding.btnCreateRequest.setText("");
        binding.btnCreateRequest.setEnabled(false);
        AppUtils.showProgressBar(binding.pb);
    }
    private void hideProgressbar(){
        isCreating = false;
        binding.btnCreateRequest.setText("Find Provider");
        binding.btnCreateRequest.setEnabled(true);
        AppUtils.hideProgressBar(binding.pb);
    }

    public void updateTitle(String title){
        if("Summary".equalsIgnoreCase(title)){
            binding.btnCreateRequest.setVisibility(View.VISIBLE);
        }else{
            binding.btnCreateRequest.setVisibility(View.GONE);
        }
        binding.tvTitle.setText(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO confirm before destroy
    }
}