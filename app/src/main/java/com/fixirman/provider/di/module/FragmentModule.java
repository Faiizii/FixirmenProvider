package com.fixirman.provider.di.module;

import com.fixirman.provider.view.create_request.RequestScheduleFragment;
import com.fixirman.provider.view.create_request.RequestSummaryFragment;
import com.fixirman.provider.view.create_request.SelectServiceFragment;
import com.fixirman.provider.view.fragment.AppointmentDetailFragment;
import com.fixirman.provider.view.fragment.AppointmentFragment;
import com.fixirman.provider.view.fragment.CategoriesFragment;
import com.fixirman.provider.view.fragment.InboxFragment;
import com.fixirman.provider.view.fragment.NotificationsFragment;
import com.fixirman.provider.view.fragment.ProvidersFragment;
import com.fixirman.provider.view.fragment.ReviewFragment;
import com.fixirman.provider.view.fragment.ReviewListingFragment;
import com.fixirman.provider.view.fragment.SubCategoryFragment;
import com.fixirman.provider.view.fragment.UserDetailFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract UserDetailFragment contributeUserDetailFragment();

    @ContributesAndroidInjector
    abstract AppointmentFragment contributeAppointmentFragment();

    @ContributesAndroidInjector
    abstract AppointmentDetailFragment contributeAppointmentDetailFragment();

    @ContributesAndroidInjector
    abstract NotificationsFragment contributeNotificationsFragment();

    @ContributesAndroidInjector
    abstract CategoriesFragment contributeCategoriesFragment();

    @ContributesAndroidInjector
    abstract SubCategoryFragment contributeSubCategoryFragment();

    @ContributesAndroidInjector
    abstract ProvidersFragment contributeProvidersFragment();

    @ContributesAndroidInjector
    abstract InboxFragment contributeInboxFragment();

    @ContributesAndroidInjector
    abstract RequestSummaryFragment contributeRequestSummaryFragment();

    @ContributesAndroidInjector
    abstract RequestScheduleFragment contributeRequestScheduleFragment();

    @ContributesAndroidInjector
    abstract SelectServiceFragment contributeSelectServiceFragment();

    @ContributesAndroidInjector
    abstract ReviewFragment contributeReviewFragment();

    @ContributesAndroidInjector
    abstract ReviewListingFragment contributeReviewListingFragment();
}
