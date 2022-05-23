package com.fixirman.provider.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.app.fixirman.R;
import com.app.fixirman.databinding.FragmentReviewBinding;
import com.fixirman.provider.adapter.ReviewsAdapter;
import com.fixirman.provider.model.review.ReviewsModel;
import com.fixirman.provider.utils.AppUtils;
import com.fixirman.provider.utils.Crashlytics;
import com.fixirman.provider.utils.SessionManager;
import com.fixirman.provider.view.activity.MainActivity;
import com.fixirman.provider.viewmodel.ReviewViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class ReviewFragment extends Fragment {
    private final String TAG = "ReviewFragment";

    private FragmentActivity activity;
    private FragmentReviewBinding binding;
    @Inject
    ViewModelProvider.Factory factory;
    private ReviewViewModel viewModel;

    private List<ReviewsModel> adapterList;
    private ReviewsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review, container, false);
        binding.setFragment(this);
        activity = getActivity();
        initMe();
        return binding.getRoot();
    }

    private void initMe() {

        initReviewAdapter();
    }
    private void initReviewAdapter(){
        adapterList = new ArrayList<>();
        adapter = new ReviewsAdapter(activity,adapterList);
        binding.rvRating.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvRating.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this,factory).get(ReviewViewModel.class);

        if(AppUtils.isNetworkAvailable(activity)){
            viewModel.init(new SessionManager(activity).getUserId()+"",binding.pb);
        }else{
            Crashlytics.log("review screen opened without internet");
        }
        viewModel.getLastReview().observe(getViewLifecycleOwner(),this::updateUI);
        viewModel.getTotalReviews().observe(getViewLifecycleOwner(),this::updateTotalCount);
        viewModel.getReviewsAvg().observe(getViewLifecycleOwner(), integer -> {
            if(integer == null)
                integer = 0.0;
            binding.tvAvgRating.setText(String.format("%.1f",integer));
            //binding.tvAvgRating.setText(integer+"");
        });
        viewModel.getTotalReviews1().observe(getViewLifecycleOwner(),this::update1);
        viewModel.getTotalReviews2().observe(getViewLifecycleOwner(),this::update2);
        viewModel.getTotalReviews3().observe(getViewLifecycleOwner(),this::update3);
        viewModel.getTotalReviews4().observe(getViewLifecycleOwner(),this::update4);
        viewModel.getTotalReviews5().observe(getViewLifecycleOwner(),this::update5);
    }

    private void update1(Integer integer) {
        if(integer == null)
            integer = 0;

        binding.tvRating1Count.setText(integer+"");
        binding.sbRating1.setProgress(integer);
    }

    private void update2(Integer integer) {
        if(integer == null)
            integer = 0;

        binding.tvRating2Count.setText(integer+"");
        binding.sbRating2.setProgress(integer);
    }
    private void update3(Integer integer) {
        if(integer == null)
            integer = 0;

        binding.tvRating3Count.setText(integer+"");
        binding.sbRating3.setProgress(integer);
    }
    private void update4(Integer integer) {
        if(integer == null)
            integer = 0;

        binding.tvRating4Count.setText(integer+"");
        binding.sbRating4.setProgress(integer);
    }
    private void update5(Integer integer) {
        if(integer == null)
            integer = 0;

        binding.tvRating5Count.setText(integer+"");
        binding.sbRating5.setProgress(integer);
    }

    private void updateTotalCount(Integer integer) {
        if(integer == null){
            integer = 0;
        }
        binding.tvMessage.setText("Avg. Rating by "+ integer +" People");

        binding.sbRating1.setMax(integer);
        binding.sbRating2.setMax(integer);
        binding.sbRating3.setMax(integer);
        binding.sbRating4.setMax(integer);
        binding.sbRating5.setMax(integer);
    }

    private void updateUI(List<ReviewsModel> reviewsModels) {
        if(reviewsModels != null){
            if(reviewsModels.size() > 0){
                adapterList.clear();
                adapterList.addAll(reviewsModels);
                adapter.notifyDataSetChanged();
                reviewFound();
            }else{
                noReviewFound();
            }
        }else{
            noReviewFound();
        }
    }

    private void reviewFound() {
        binding.tvNoRatingFound.setVisibility(View.GONE);
        binding.rvRating.setVisibility(View.VISIBLE);
        binding.mbSeeAll.setVisibility(View.VISIBLE);
    }

    private void noReviewFound() {
        binding.rvRating.setVisibility(View.GONE);
        binding.tvNoRatingFound.setVisibility(View.VISIBLE);
        binding.mbSeeAll.setVisibility(View.GONE);
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mb_seeAll:
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new ReviewListingFragment())
                        .addToBackStack("all_reviews").commit();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)activity).setSelectIndex(1);
    }

}