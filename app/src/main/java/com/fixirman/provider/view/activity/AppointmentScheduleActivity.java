package com.fixirman.provider.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.fixirman.R;
import com.fixirman.provider.api.http.ApiUtils;
import com.app.fixirman.databinding.ActivityAppointmentScheduleBinding;
import com.app.fixirman.databinding.DialogPaymentMethodBinding;
import com.app.fixirman.databinding.VhDayDateBinding;
import com.app.fixirman.databinding.VhTimeSlotBinding;
import com.fixirman.provider.model.appointment.AppointmentResponse;
import com.fixirman.provider.model.categroy.Category;
import com.fixirman.provider.model.categroy.SubCategory;
import com.fixirman.provider.model.provider.DateModel;
import com.fixirman.provider.model.provider.DateTimeResult;
import com.fixirman.provider.model.provider.ProviderDetailResponse;
import com.fixirman.provider.utils.AppConstants;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.LocationHandler;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.view.fragment.SubCategoryFragment;
import com.fixirman.provider.viewmodel.AppointmentViewModel;
import com.fixirman.provider.utils.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentScheduleActivity extends AppCompatActivity implements LocationHandler.RequestCallBack {
    private final String TAG = "AppointmentSchedule";

    private ActivityAppointmentScheduleBinding binding;
    private SessionManager sessionManager;
    private String addressId = "";
    private static final int CHANGE_ADDRESS_CODE = 143;
    private static final int TYPE_BOOK_NOW = 144;
    private static final int TYPE_SCHEDULE = 145;
    private List<String> timeSlots;
    private List<DateModel> dateSlots;
    private DateAdapter dateAdapter;
    private TimeAdapter timeAdapter;
    private int dateSelectedIndex = 0, timeSelectedIndex = 0;


    private LocationHandler locationHandler;
    //private SubCategory subCategory;
    private ArrayMap<Integer, SubCategory> list;

    @Inject
    ViewModelProvider.Factory factory;
    private AppointmentViewModel viewModel;

    private LatLng currentLatLng;
    private int addressType;
    private Category category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_appointment_schedule);
        binding.setActivity(this);
        initMe();
    }

    private void initMe() {
        sessionManager = new SessionManager(this);
        category = getIntent().getParcelableExtra("object");

        list = new ArrayMap<>();

        handleTypeOfAppointment(true,false);
        initLocation();
        initViewModel();
        getOnlineDetails();
        initAdapters();
    }

    private void initLocation() {
        //init location handler
        locationHandler = new LocationHandler(this);
        locationHandler.setRequestOneTime(true);
        locationHandler.getLastSavedLocation();
        locationHandler.setLocationCallBack(this);
        locationHandler.setRequestAccuracy(100);

    }
    private void initViewModel() {
        viewModel = new ViewModelProvider(this,factory).get(AppointmentViewModel.class);
        viewModel.getSelectedServices().observe(this, arrayMap -> {
            if(arrayMap != null && arrayMap.size() > 0){
                String tempStr = arrayMap.valueAt(0).getTitle();
                if(arrayMap.size() > 1)
                    tempStr = tempStr+" and "+ (arrayMap.size() -1)+" More";

                binding.tvSubCategories.setText(tempStr);
            }
        });
    }

    private void initAdapters() {
        timeSlots = new ArrayList<>();
        dateSlots = new ArrayList<>();
        dateAdapter = new DateAdapter();
        timeAdapter = new TimeAdapter();
        binding.rvDates.setAdapter(dateAdapter);
        binding.rvTime.setAdapter(timeAdapter);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                if(AppUtils.isNetworkAvailable(this)){
                    if(validate()){
                        showPaymentDialog();
                    }
                }else{
                    AppUtils.createNetworkError(this);
                }
                break;
            case R.id.layout_address:
                Intent mapIntent = new Intent(this, LocationPickerActivity.class);
                startActivityForResult(mapIntent, CHANGE_ADDRESS_CODE);
                break;
            case R.id.mb_bookNow:
                handleTypeOfAppointment(true,false);
                break;
            case R.id.mb_schedule:
                handleTypeOfAppointment(false,true);
                break;
            case R.id.layout_subCategories:
            case R.id.tv_subCategories:
                openSubCategoryScreen();
                break;
        }
    }

    private void openSubCategoryScreen() {
        SubCategoryFragment fragment = new SubCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstants.CATEGORY_ID, category.getId());
            bundle.putString(AppConstants.CATEGORY_NAME, category.getTitle());
            fragment.setArguments(bundle);
            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame,fragment).addToBackStack("sub_category").commit();*/
        fragment.show(getSupportFragmentManager(),"sub_category");
    }

    private void handleTypeOfAppointment(boolean bookNow, boolean schedule){
        addressType = schedule ? TYPE_SCHEDULE : TYPE_BOOK_NOW;
        binding.mbBookNow.setStrokeColor(AppCompatResources.getColorStateList( this,bookNow ?R.color.colorAccent: android.R.color.transparent ));
        binding.mbSchedule.setStrokeColor(AppCompatResources.getColorStateList(this,schedule ? R.color.colorAccent : android.R.color.transparent ));

        binding.layoutAddress.setVisibility(bookNow ? View.VISIBLE : View.GONE);
        binding.layoutSchedule.setVisibility(schedule ? View.VISIBLE : View.GONE);
    }


    private void showPaymentDialog() {
        Dialog dialog = new Dialog(this,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        DialogPaymentMethodBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.dialog_payment_method,null,false);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.mbConfirmPayment.setOnClickListener(v->{
            if(AppUtils.isNetworkAvailable(this)){
                if(validate(dialogBinding)){
                    createAppointment(dialog);
                }
            }else{
                AppUtils.createNetworkError(this);
            }
        });
        dialog.show();
    }

    private void showProgress(){
        binding.btnConfirm.setEnabled(false);
        binding.btnConfirm.setText("");
        AppUtils.showProgressBar(binding.pb);
    }
    private void hideProgressBar(){
        binding.btnConfirm.setEnabled(true);
        binding.btnConfirm.setText("Confirm");
        AppUtils.hideProgressBar(binding.pb);
    }

    String cardNumber,expiryDate,cvc,postalCode;
    private boolean validate(DialogPaymentMethodBinding dialogBinding) {
        boolean check = true;
        cardNumber = dialogBinding.etCardNumber.getText().toString().trim();
        expiryDate = dialogBinding.etExpiryDate.getText().toString().trim();
        cvc = dialogBinding.etCvc.getText().toString().trim();
        postalCode = dialogBinding.etPostalCode.getText().toString().trim();
        check = checkIsNotEmpty(cardNumber,dialogBinding.ilCardNumber);
        check = checkIsNotEmpty(expiryDate,dialogBinding.ilExpiryDate);
        check = checkIsNotEmpty(cvc,dialogBinding.ilCvc);
        check = checkIsNotEmpty(postalCode,dialogBinding.ilPostalCode);
        return check;
    }
    public boolean checkIsNotEmpty(String value, TextInputLayout inputLayout){
        if(value.isEmpty()){
            inputLayout.setError("Can't be empty");
            return false;
        }else{
            inputLayout.setError(null);
            return true;
        }
    }

    private boolean validate() {
        boolean check = true;
        if(list == null){
            check = false;
            Toast.makeText(this, "Detail is unavailable", Toast.LENGTH_SHORT).show();
        }
        //check address and time before creating request
        return check;
    }

    private void updateUI(List<String> timeSlots, List<DateModel> dates) {
        if(timeSlots != null){
            this.timeSlots.clear();
            this.timeSlots.addAll(timeSlots);
            timeAdapter.notifyDataSetChanged();
        }else{
            Log.e(TAG, "updateUI: no time slot in laded list" );
        }
        if(dates != null){
            this.dateSlots.clear();
            this.dateSlots.addAll(dates);
            dateAdapter.notifyDataSetChanged();
        }else{
            //no date found
            Log.e(TAG, "updateUI: no date in loaded list" );
        }
    }

    private void createAppointment(Dialog dialog){
        showProgress();
        dialog.hide();
        ApiUtils.getApiInterface().createAppointment(cardNumber,expiryDate,cvc,postalCode,sessionManager.getUserId(),timeSlots.get(timeSelectedIndex),
                dateSlots.get(dateSelectedIndex).getDate(),""
                ,addressId,binding.tvCurrentLocation.getText().toString().trim(),currentLatLng.latitude,currentLatLng.longitude,
                new Gson().toJson(list.keySet()))
                .enqueue(new Callback<AppointmentResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AppointmentResponse> call,@NonNull Response<AppointmentResponse> response) {
                        hideProgressBar();
                        if(response.isSuccessful()){
                            AppointmentResponse appointmentResponse = response.body();
                            if(appointmentResponse.getSuccess() == 1){
                                //save records to local
                                dialog.dismiss();
                                if(appointmentResponse.getAppointment() != null){
                                    viewModel.saveAppointment(appointmentResponse.getAppointment());
                                    Toast.makeText(AppointmentScheduleActivity.this,
                                            appointmentResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    openMainActivity();
                                }else{
                                    Crashlytics.log("appointment model is null in create appointment api");
                                }
                            }else{
                                //show dialog
                                dialog.show();
                                showFailedDialog(appointmentResponse.getMessage());
                            }
                        }else{
                            dialog.show();
                            Crashlytics.log("code: "+response.code());
                            Crashlytics.log("message: "+response.message());
                            Crashlytics.logException(new Exception("create appointment api "+response.message()));
                            showFailedDialog("Server Connection Error");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AppointmentResponse> call, @NonNull Throwable t) {
                        hideProgressBar();
                        dialog.show();
                        Crashlytics.log("create appointment api");
                        Crashlytics.logException(t);
                        if(t instanceof SocketTimeoutException){
                            showFailedDialog("Slow Internet Connection");
                        }else{
                            showFailedDialog("Server Error");
                        }
                    }
                });
    }

    private void openMainActivity() {
        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void getOnlineDetails(){
        AppUtils.showProgressBar(binding.pb);
        ApiUtils.getApiInterface().getTimeSlots(sessionManager.getUserId(),new Gson().toJson(list.keySet())).enqueue(
                new Callback<ProviderDetailResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProviderDetailResponse> call, @NonNull Response<ProviderDetailResponse> response) {
                        AppUtils.hideProgressBar(binding.pb);
                        if(response.isSuccessful()){
                            ProviderDetailResponse detailResponse = response.body();
                            if(detailResponse.getSuccess() == 1){
                                DateTimeResult result = detailResponse.getResult();
                                if(result != null){
                                    updateUI(result.getSlots(),result.getDates());
                                }

                            }else{
                                showFailedDialog(detailResponse.getMessage());
                            }
                        }else{
                            Crashlytics.log("code: "+response.code());
                            Crashlytics.log("message: "+response.message());
                            Crashlytics.logException(new Exception("load time slots connection exception with code:"+response.code()));
                            showFailedDialog("Server Connection Error");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProviderDetailResponse> call, @NonNull Throwable t) {
                        AppUtils.hideProgressBar(binding.pb);
                        Crashlytics.log(TAG);
                        Crashlytics.logException(t);
                        if(t instanceof SocketTimeoutException){
                            showFailedDialog("Slow Internet Connection");
                        }else{
                            showFailedDialog("Server Error");
                        }
                    }
                }
        );
    }

    private void showFailedDialog(String message) {
        AppUtils.createFailedDialog(this,message);
    }

    @Override
    public void sendLocation(Location location) {
        if(location != null){
            currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            binding.tvCurrentLocation.setText(locationHandler.getCompleteAddress(location.getLatitude(),location.getLongitude()));
        }else{
            Log.e(TAG, "sendLocation: location model is null" );
        }
    }

    private class DateAdapter extends RecyclerView.Adapter<MyVh>{

        @NonNull
        @Override
        public MyVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            VhDayDateBinding binding = DataBindingUtil.inflate(LayoutInflater.from(AppointmentScheduleActivity.this),
                    R.layout.vh_day_date,parent,false);
            return new MyVh(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyVh holder, int position) {
            /*if(position == dateSelectedIndex){
                holder.dateBinding.main.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else{
                holder.dateBinding.main.setBackgroundColor(getResources().getColor(R.color.defaultBackgroundColor));
            }*/
            if(position == dateSelectedIndex){
                holder.dateBinding.main.setBackgroundResource(R.drawable.background_round_corner);
            }else{
                holder.dateBinding.main.setBackgroundResource(R.color.defaultBackgroundColor);
            }
            holder.dateBinding.tvDayName.setText(dateSlots.get(position).getDayName());
            holder.dateBinding.tvDayDate.setText(dateSlots.get(position).getDayNumber());
            holder.dateBinding.main.setOnClickListener(v->{
                dateSelectedIndex = position;
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return dateSlots.size();
        }
    }
    private class TimeAdapter extends RecyclerView.Adapter<MyVh>{

        @NonNull
        @Override
        public MyVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            VhTimeSlotBinding binding = DataBindingUtil.inflate(LayoutInflater.from(AppointmentScheduleActivity.this),
                    R.layout.vh_time_slot,parent,false);
            return new MyVh(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MyVh holder, int position) {
            /*if(position == timeSelectedIndex){
                holder.timeBinding.main.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else{
                holder.timeBinding.main.setBackgroundColor(getResources().getColor(R.color.defaultBackgroundColor));
            }*/
            if(position == timeSelectedIndex){
                holder.timeBinding.main.setBackgroundResource(R.drawable.background_round_corner);
            }else{
                holder.timeBinding.main.setBackgroundResource(R.color.defaultBackgroundColor);
            }
            holder.timeBinding.tvTimeSlot.setText(timeSlots.get(position));
            holder.timeBinding.main.setOnClickListener(v->{
                timeSelectedIndex = position;
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return timeSlots.size();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CHANGE_ADDRESS_CODE){
                if(data != null) {
                    Bundle bundle = data.getExtras();
                    if(bundle != null){
                        currentLatLng = new LatLng(bundle.getDouble("lat"),bundle.getDouble("lng"));
                        String address = bundle.getString("address", "");
                        String streetAddress = bundle.getString("street_address","");
                        binding.tvCurrentLocation.setText(address+","+streetAddress);
                    }
                }
            }else{
                Log.e(TAG, "onActivityResult: unknown code" );
            }
        }else{
            Log.e(TAG, "onActivityResult: not ok " );
        }
    }
    private class MyVh extends RecyclerView.ViewHolder {
        public VhDayDateBinding dateBinding;
        public VhTimeSlotBinding timeBinding;
        public MyVh(VhTimeSlotBinding timeBinding) {
            super(timeBinding.getRoot());
            this.timeBinding = timeBinding;
        }
        public MyVh(VhDayDateBinding dateBinding) {
            super(dateBinding.getRoot());
            this.dateBinding = dateBinding;
        }
    }
}