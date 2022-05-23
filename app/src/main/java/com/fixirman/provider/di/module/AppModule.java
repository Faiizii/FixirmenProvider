package com.fixirman.provider.di.module;

import android.app.Application;

import com.fixirman.provider.api.http.ApiInterface;
import com.fixirman.provider.api_db_helper.DaoHelper;
import com.fixirman.provider.database.MyDatabase;
import com.fixirman.provider.database.dao.EventDao;
import com.fixirman.provider.utils.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {

    // --- DATABASE INJECTION ---

    @Provides
    @Singleton
    MyDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application,
                MyDatabase.class, "fixirman.db")
                .allowMainThreadQueries() //Allows room to do operation on main thread
                .fallbackToDestructiveMigration()  // Clear db on upgrade
                .build();
    }

    @Provides
    @Singleton
    EventDao provideEventDao(MyDatabase database) {
        return database.eventDao();
    }

    // --- REPOSITORY INJECTION ---

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    DaoHelper provideDaoHelper(ApiInterface webservice, EventDao eventDao, Executor executor) {
        return new DaoHelper(webservice, eventDao, executor);
    }

    // --- NETWORK INJECTION ---

    @Provides
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    Retrofit provideRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(AppConstants.HOST_URL)
                .client(
                        new OkHttpClient.Builder()
                                .readTimeout(60, TimeUnit.SECONDS)
                                .connectTimeout(60, TimeUnit.SECONDS)
                                .build()
                )
                .build();
    }

    @Provides
    @Singleton
    ApiInterface provideApiWebservice(Retrofit restAdapter) {
        return restAdapter.create(ApiInterface.class);
    }
}
