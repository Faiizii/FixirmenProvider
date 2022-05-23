package com.fixirman.provider.viewmodel;

import android.widget.ProgressBar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.fixirman.provider.api_db_helper.DaoHelper;
import com.fixirman.provider.model.review.ReviewsModel;

import java.util.List;

import javax.inject.Inject;

public class ReviewViewModel extends ViewModel {

    private DaoHelper helper;
    @Inject
    public ReviewViewModel(DaoHelper helper){
        this.helper = helper;
    }

    public void init(String userId, ProgressBar pb){
        helper.getOnlineReviewsList(userId,pb);
    }
    public LiveData<List<ReviewsModel>> getAllReviews(){
        return helper.getReviews();
    }
    public LiveData<List<ReviewsModel>> getLastReview() {
        return helper.getRecentReviews();
    }

    public LiveData<Integer> getTotalReviews() {
        return helper.getTotalReviews();
    }
    public LiveData<Integer> getTotalReviews5() {
        return helper.getReviewsCountOf(5);
    }

    public LiveData<Integer> getTotalReviews4() {
        return helper.getReviewsCountOf(4);
    }

    public LiveData<Integer> getTotalReviews3() {
        return helper.getReviewsCountOf(3);
    }

    public LiveData<Integer> getTotalReviews2() {
        return helper.getReviewsCountOf(2);
    }

    public LiveData<Integer> getTotalReviews1() {
        return helper.getReviewsCountOf(1);
    }
    public LiveData<Double> getReviewsAvg() {
        return helper.getReviewsAvg();
    }
}
