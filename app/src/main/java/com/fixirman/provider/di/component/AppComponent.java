package com.fixirman.provider.di.component;

import android.app.Application;

import com.fixirman.provider.my_app.MyApplication;
import com.fixirman.provider.di.module.ActivityModule;
import com.fixirman.provider.di.module.AppModule;
import com.fixirman.provider.di.module.FragmentModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules={AndroidInjectionModule.class, ActivityModule.class,  FragmentModule.class, AppModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(MyApplication app);
}
