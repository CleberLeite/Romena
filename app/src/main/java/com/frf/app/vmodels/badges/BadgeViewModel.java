package com.frf.app.vmodels.badges;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.data.BadgeData;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.badges.BadgeRepository;
import com.frf.app.vmodels.MainViewModel;

import java.util.List;


public class BadgeViewModel extends MainViewModel {

    private BadgeRepository mRepository;
    public void setmRepository(BadgeRepository mRepository) {
        this.mRepository = mRepository;
    }

    MutableLiveData<List<BadgeData>> mBadges;

    public LiveData<List<BadgeData>> getBadges (){
        if(mBadges == null){
            mBadges = new MutableLiveData<>();
        }


        return mBadges;
    }

    public void getBadges(int page){
        mRepository.getList(page, new BadgeRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(List<BadgeData> data) {
                mBadges.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel =  new MessageModel();
                messageModel.setMsg(message);
                messageModel.setId(1);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {

            }
        });
    }
}
