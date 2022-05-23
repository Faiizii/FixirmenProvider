package com.fixirman.provider.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fixirman.provider.api_db_helper.DaoHelper;
import com.fixirman.provider.model.chat.InboxModel;
import com.fixirman.provider.model.chat.MessageModel;

import java.util.List;

import javax.inject.Inject;

public class ChatViewModel extends ViewModel {
    private DaoHelper helper;

    @Inject
    public ChatViewModel(DaoHelper helper){
        this.helper = helper;
    }

    public LiveData<List<InboxModel>> getInboxListing() {
        return helper.getInboxListing();
    }

    public void insertMessage(MessageModel model) {
        helper.insertMessage(model);
    }

    public void updateMessage(MessageModel model) {
        helper.updateMessage(model);
    }

    public List<MessageModel> getLocalMessages() {
        return  helper.getMessages();
    }
}
