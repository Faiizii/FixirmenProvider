package com.fixirman.provider.database;

import com.fixirman.provider.database.dao.EventDao;
import com.fixirman.provider.model.appointment.Appointment;
import com.fixirman.provider.model.appointment.AppointmentStatus;
import com.fixirman.provider.model.categroy.Category;
import com.fixirman.provider.model.categroy.ServiceProviders;
import com.fixirman.provider.model.categroy.ServiceType;
import com.fixirman.provider.model.categroy.SubCategory;
import com.fixirman.provider.model.chat.InboxModel;
import com.fixirman.provider.model.chat.MessageModel;
import com.fixirman.provider.model.faq.FAQModel;
import com.fixirman.provider.model.notification.Notification;
import com.fixirman.provider.model.provider.ProviderDetail;
import com.fixirman.provider.model.request.RequestBid;
import com.fixirman.provider.model.request.RequestDetail;
import com.fixirman.provider.model.request.RequestService;
import com.fixirman.provider.model.review.ReviewsModel;
import com.fixirman.provider.model.user.AdModel;
import com.fixirman.provider.model.user.User;
import com.fixirman.provider.model.user.UserAddress;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, UserAddress.class, Category.class, SubCategory.class, ServiceProviders.class,
        Appointment.class, AppointmentStatus.class, Notification.class, MessageModel.class,
        InboxModel.class, ProviderDetail.class, ReviewsModel.class, FAQModel.class, AdModel.class,
        ServiceType.class, RequestDetail.class, RequestService.class, RequestBid.class
},
        version = 1,exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile MyDatabase INSTANCE;

    // --- DAO ---
    public abstract EventDao eventDao();
}
