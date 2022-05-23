package com.fixirman.provider.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.fixirman.R;
import com.app.fixirman.databinding.ActivityRequestDetailBinding;
import com.fixirman.provider.api.http.ApiUtils;
import com.fixirman.provider.model.NormalResponse;
import com.fixirman.provider.model.request.RequestBid;
import com.fixirman.provider.model.request.RequestDetail;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.view.create_request.RequestViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RequestDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = "requestDetail";

    private ActivityRequestDetailBinding binding;
    private RequestDetail model;

    private LatLng mapLatLng;
    private GoogleMap googleMap;

    @Inject
    ViewModelProvider.Factory factory;
    private RequestViewModel viewModel;

    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_request_detail);
        binding.setActivity(this);
        initMe();
    }

    private void initMe() {
        session = new SessionManager(this);
        viewModel = new ViewModelProvider(this,factory).get(RequestViewModel.class);
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }catch (Exception e){
            Log.e(TAG, "onCreate: ",e );
        }

        if(getIntent() != null){
            model = getIntent().getParcelableExtra("object");
            updateUI(model);
        }

        if(AppUtils.isNetworkAvailable(this) && model != null){
            viewModel.getOnlineRequestDetail(binding.pb,model.getId(),new SessionManager(this).getUserId());
        }else{
            AppUtils.createNetworkError(this);
        }
        if(model != null){
            viewModel.getRequestDetail(model.getId()).observe(this, requestDetail -> {
                if(requestDetail != null){
                    updateUI(requestDetail);
                }
            });
        }
    }

    private void updateUI(RequestDetail model) {
        if(model != null){
            binding.setModel(model);
            Glide.with(this).load(AppConstants.HOST_URL+model.getCategoryImage()).into(binding.ivCategoryImage);

            Glide.with(this).load(AppConstants.HOST_URL+model.getUserImage())
                    .apply(new RequestOptions().error(R.drawable.default_user).fitCenter())
                    .into(binding.ivUserImage);

            this.model = model;
            mapLatLng = new LatLng(model.getLatitude(),model.getLongitude());
            if(googleMap != null){
                MarkerOptions options = new MarkerOptions();
                options.position(mapLatLng);
                this.googleMap.clear();
                this.googleMap.addMarker(options);
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLatLng,14));
            }
            handleRequestStatus(model.getStatus());
        }else{
            Toast.makeText(this, "Invalid detail Please try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleRequestStatus(String status) {
        switch (status.toUpperCase()){
            case AppConstants.STATUS_SEARCHING:
                handleButtonVisibility(VISIBLE,GONE,GONE,GONE,GONE,GONE,GONE,GONE);
                binding.tvMessage.setText("Submit your request to earn this job");
                break;
            case AppConstants.STATUS_PENDING:
                handleButtonVisibility(GONE,VISIBLE,GONE,GONE,VISIBLE,GONE,GONE,GONE);
                binding.tvMessage.setText("Your request is in review. It will be soon accepted by the user");
                break;
            case AppConstants.STATUS_ACCEPT:
                handleButtonVisibility(GONE,GONE,VISIBLE,GONE,GONE,VISIBLE,GONE,GONE);
                binding.tvMessage.setText("Start your job as soon as possible to earn a good rating");
                break;
            case AppConstants.STATUS_START:
                handleButtonVisibility(GONE,GONE,GONE,VISIBLE,GONE,VISIBLE,GONE,GONE);
                binding.tvMessage.setText("Finish your job as soon as possible to earn a good rating");
                break;
            case AppConstants.STATUS_FINISH:
                handleButtonVisibility(GONE,GONE,GONE,GONE,GONE,GONE,VISIBLE,GONE);
                binding.tvMessage.setText("Rate your experience with the user");
                break;
            case AppConstants.STATUS_CANCEL:
            case AppConstants.STATUS_FAILED:
                handleButtonVisibility(GONE,GONE,GONE,GONE,GONE,GONE,GONE,GONE);
                binding.tvMessage.setText("This request is no more active for you");
                break;
            case AppConstants.STATUS_RATED:
                handleButtonVisibility(GONE,GONE,GONE,GONE,GONE,GONE,GONE,VISIBLE);
                //show rated layout
                binding.tvMessage.setText("Thank you for your feedback");
                break;
        }
    }
    private void handleButtonVisibility(int accept,int pending,int start, int finish,int cancel, int fail, int rate,int rated){
        binding.etBidInfo.setVisibility(accept);
        binding.btnAccept.setVisibility(accept);
        binding.btnPending.setVisibility(pending);
        binding.btnStart.setVisibility(start);
        binding.btnFinish.setVisibility(finish);
        binding.btnCancel.setVisibility(cancel);
        binding.btnFail.setVisibility(fail);

        //rating layout
        binding.rbRateUser.setVisibility(rate);
        binding.etReview.setVisibility(rate);
        binding.btnSubmitRating.setVisibility(rate);

        //show reviewed information in case if rated == visible

    }

    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.btn_back) {
            onBackPressed();
        } else if (id == R.id.btn_accept) {
            bidRequest();
        } else if (id == R.id.btn_start) {
            changeRequestStatus(AppConstants.STATUS_START);
        } else if (id == R.id.btn_finish) {
            changeRequestStatus(AppConstants.STATUS_FINISH);
        } else if (id == R.id.btn_cancel) {
            changeBidRequestStatus(AppConstants.STATUS_CANCEL);
        } else if (id == R.id.btn_fail) {
            changeRequestStatus(AppConstants.STATUS_FAILED);
        } else if (id == R.id.btn_submitRating) {
            submitRating();
        }
    }

    private void changeBidRequestStatus(String status) {
        if(!AppUtils.isNetworkAvailable(this)){
            AppUtils.createNetworkError(this);
            return;
        }
        int bidId = viewModel.getBidId(session.getUserId(),model.getId());
        AppUtils.showProgressBar(binding.pbStatusChange);
        ApiUtils.getApiInterface().changeBidRequestStatus(session.getUserId(),bidId,status).enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(@NonNull Call<NormalResponse> call,@NonNull  Response<NormalResponse> response) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(response.isSuccessful()){
                    NormalResponse normalResponse = response.body();
                    if(normalResponse.getSuccess() == 1){
                        model.setStatus(status);
                        updateUI(model);
                        Toast.makeText(RequestDetailActivity.this, normalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        AppUtils.createFailedDialog(RequestDetailActivity.this,normalResponse.getMessage());
                    }
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Connection Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NormalResponse> call,@NonNull  Throwable t) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(t instanceof SocketTimeoutException){
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Slow internet connection");
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Error");
                }
            }
        });
    }

    private void bidRequest() {
        if(!AppUtils.isNetworkAvailable(this)){
            AppUtils.createNetworkError(this);
            return;
        }

        AppUtils.showProgressBar(binding.pbStatusChange);
        String description = binding.etBidInfo.getText().toString().trim();
        if(description.isEmpty())
            description = "No Detail";
        ApiUtils.getApiInterface().bidRequest(session.getUserId(),model.getId(),description).enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(@NonNull Call<NormalResponse> call,@NonNull  Response<NormalResponse> response) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(response.isSuccessful()){
                    NormalResponse normalResponse = response.body();
                    if(normalResponse.getSuccess() == 1){
                        model.setStatus(AppConstants.STATUS_PENDING);
                        updateUI(model);
                        Toast.makeText(RequestDetailActivity.this, normalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        AppUtils.createFailedDialog(RequestDetailActivity.this,normalResponse.getMessage());
                    }
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Connection Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NormalResponse> call,@NonNull  Throwable t) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(t instanceof SocketTimeoutException){
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Slow internet connection");
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Error");
                }
            }
        });
    }

    private void submitRating() {
        if(!AppUtils.isNetworkAvailable(this)){
            AppUtils.createNetworkError(this);
            return;
        }
        AppUtils.showProgressBar(binding.pbStatusChange);

        String feedback = binding.etReview.getText().toString().trim();
        if(feedback.isEmpty())
            feedback = "empty";
        float rating = binding.rbRateUser.getRating();
        if(rating == 0){
            Toast.makeText(this, "Please select rating stars", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiUtils.getApiInterface().submitRating(model.getCreatedBy(),model.getId(),model.getCategoryId(),rating,feedback,session.getUserId()).enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(@NonNull Call<NormalResponse> call,@NonNull  Response<NormalResponse> response) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(response.isSuccessful()){
                    NormalResponse normalResponse = response.body();
                    if(normalResponse.getSuccess() == 1){
                        model.setStatus(AppConstants.STATUS_RATED);
                        updateUI(model);
                        Toast.makeText(RequestDetailActivity.this, normalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        AppUtils.createFailedDialog(RequestDetailActivity.this,normalResponse.getMessage());
                    }
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Connection Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NormalResponse> call,@NonNull  Throwable t) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(t instanceof SocketTimeoutException){
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Slow internet connection");
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Error");
                }
            }
        });
    }

    private void changeRequestStatus(String status) {
        if(!AppUtils.isNetworkAvailable(this)){
            AppUtils.createNetworkError(this);
            return;
        }
        AppUtils.showProgressBar(binding.pbStatusChange);
        ApiUtils.getApiInterface().changeRequestStatus(session.getUserId(),model.getId(),status).enqueue(new Callback<NormalResponse>() {
            @Override
            public void onResponse(@NonNull Call<NormalResponse> call,@NonNull  Response<NormalResponse> response) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(response.isSuccessful()){
                    NormalResponse normalResponse = response.body();
                    if(normalResponse.getSuccess() == 1){
                        model.setStatus(status);
                        updateUI(model);
                        Toast.makeText(RequestDetailActivity.this, normalResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        AppUtils.createFailedDialog(RequestDetailActivity.this,normalResponse.getMessage());
                    }
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Connection Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NormalResponse> call,@NonNull  Throwable t) {
                AppUtils.hideProgressBar(binding.pbStatusChange);
                if(t instanceof SocketTimeoutException){
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Slow internet connection");
                }else{
                    AppUtils.createFailedDialog(RequestDetailActivity.this,"Server Error");
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(this.googleMap != null) {
            this.googleMap.getUiSettings().setAllGesturesEnabled(false);
            this.googleMap.getUiSettings().setZoomControlsEnabled(false);
            this.googleMap.getUiSettings().setZoomGesturesEnabled(false);
            if(mapLatLng != null){
                MarkerOptions options = new MarkerOptions();
                options.position(mapLatLng);
                this.googleMap.clear();
                this.googleMap.addMarker(options);
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLatLng,14));
            }
        }else{
            Log.e(TAG, "onMapReady: map not ready" );
        }
    }
}