package com.fixirman.provider.di.module;

import com.fixirman.provider.view.activity.AppointmentScheduleActivity;
import com.fixirman.provider.view.activity.ChangeAddressActivity;
import com.fixirman.provider.view.activity.ChatActivity;
import com.fixirman.provider.view.activity.FAQActivity;
import com.fixirman.provider.view.activity.LocationPickerActivity;
import com.fixirman.provider.view.activity.MainActivity;
import com.fixirman.provider.view.activity.PhoneActivity;
import com.fixirman.provider.view.activity.ProviderDetailsActivity;
import com.fixirman.provider.view.activity.RequestDetailActivity;
import com.fixirman.provider.view.activity.SplashActivity;
import com.fixirman.provider.view.activity.UpdateProfileActivity;
import com.fixirman.provider.view.activity.UserRegisterActivity;
import com.fixirman.provider.view.create_request.CreateRequestActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract SplashActivity contributeSplashActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract UserRegisterActivity contributeUserRegisterActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract PhoneActivity contributePhoneActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract LocationPickerActivity contributeLocationPickerActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract ChatActivity contributeChatActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract UpdateProfileActivity contributeUpdateProfileActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract ChangeAddressActivity contributeChangeAddressActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract ProviderDetailsActivity contributeProviderDetailsActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract AppointmentScheduleActivity contributeAppointmentScheduleActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract FAQActivity contributeFAQActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract CreateRequestActivity contributeCreateRequestActivity();

    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract RequestDetailActivity contributeRequestDetailActivity();

}
