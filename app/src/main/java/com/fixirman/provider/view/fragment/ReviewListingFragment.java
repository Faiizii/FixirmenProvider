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
import com.app.fixirman.databinding.FragmentReviewListingBinding;
import com.fixirman.provider.adapter.ReviewsAdapter;
import com.fixirman.provider.model.review.ReviewsModel;
import com.fixirman.provider.view.activity.MainActivity;
import com.fixirman.provider.viewmodel.ReviewViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReviewListingFragment extends Fragment {
    private final String TAG = "ReviewListing";

    private FragmentActivity activity;
    private FragmentReviewListingBinding binding;
    @Inject
    ViewModelProvider.Factory factory;
    private ReviewViewModel viewModel;

    private List<ReviewsModel> adapterList;
    private ReviewsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_listing, container, false);
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
        viewModel.getAllReviews().observe(getViewLifecycleOwner(),this::updateUI);
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_back:
                activity.onBackPressed();
                break;
        }
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
    }

    private void noReviewFound() {
        binding.rvRating.setVisibility(View.GONE);
        binding.tvNoRatingFound.setVisibility(View.VISIBLE);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)activity).visibleNavigationView(GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)activity).visibleNavigationView(VISIBLE);
    }
}