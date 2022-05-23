package com.fixirman.provider.di.module;



import com.fixirman.provider.di.key.ViewModelKey;
import com.fixirman.provider.view.create_request.RequestViewModel;
import com.fixirman.provider.viewmodel.AppointmentViewModel;
import com.fixirman.provider.viewmodel.CategoryViewModel;
import com.fixirman.provider.viewmodel.ChatViewModel;
import com.fixirman.provider.viewmodel.FAQViewModel;
import com.fixirman.provider.viewmodel.NotificationViewModel;
import com.fixirman.provider.viewmodel.ReviewViewModel;
import com.fixirman.provider.viewmodel.UserViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel bindUserViewModel(UserViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AppointmentViewModel.class)
    abstract ViewModel bindAppointmentViewModel(AppointmentViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationViewModel.class)
    abstract ViewModel bindNotificationViewModel(NotificationViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel.class)
    abstract ViewModel bindCategoryViewModel(CategoryViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel.class)
    abstract ViewModel bindChatViewModel(ChatViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FAQViewModel.class)
    abstract ViewModel bindFAQViewModel(FAQViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RequestViewModel.class)
    abstract ViewModel bindRequestViewModel(RequestViewModel repoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ReviewViewModel.class)
    abstract ViewModel bindReviewViewModel(ReviewViewModel repoViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(FactoryViewModel factory);
}
