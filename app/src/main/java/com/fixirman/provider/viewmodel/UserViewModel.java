package com.fixirman.provider.viewmodel;

import android.widget.ProgressBar;

import com.fixirman.provider.api_db_helper.DaoHelper;
import com.fixirman.provider.model.user.AdModel;
import com.fixirman.provider.model.user.User;
import com.fixirman.provider.model.user.UserAddress;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
public class UserViewModel extends ViewModel {

    private DaoHelper daoHelper;
    @Inject
    public UserViewModel(DaoHelper helper){
        daoHelper = helper;
    }
    public LiveData<List<User>> getAllUsers(int userId, int count){
        return daoHelper.getAllUsers(userId,count);
    }
    public void init(ProgressBar pb, int userId){
        daoHelper.getOnlineCategories(pb,userId);
    }
    //test function
    public void updateUser(User u) {
        daoHelper.updateUser(u);
    }

    public void registerFirebaseListener(int userId) {
        daoHelper.registerFirebaseListeners(userId);
    }
    public LiveData<Integer> getUnreadCount() {
        return daoHelper.getUnreadNotificationCount();
    }
    public void logout() {
        daoHelper.logout();
    }

    public void insertAddress(List<UserAddress> userAddress) {
        daoHelper.insertAddresses(userAddress);
    }

    public void insertAds(List<AdModel> ads) {
        daoHelper.insertAds(ads);
    }

    public LiveData<List<UserAddress>> getAddresses(int userId) {
        return daoHelper.getUserAddresses(userId);
    }

    public void clearUserAddresses(int userId) {
        daoHelper.clearUserAddresses(userId);
    }

    public void deleteAddress(int itemId) {
        daoHelper.deleteAddress(itemId);
    }
}
