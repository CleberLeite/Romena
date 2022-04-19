package com.frf.app.vmodels.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.home.HomeRepository;
import com.frf.app.vmodels.MainViewModel;

import org.json.JSONObject;

public class HomeViewModel extends MainViewModel {

    private com.frf.app.repository.home.HomeRepository mRepository;
    public void setmRepository(com.frf.app.repository.home.HomeRepository mRepository) {
        this.mRepository = mRepository;
    }

    MutableLiveData<JSONObject> mHome;

    public LiveData<JSONObject>  getHome (){
        if(mHome == null) {
            mHome = new MutableLiveData<>();
        }

        return mHome;
    }

    public void getItens(){
        mRepository.getHome(new HomeRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(JSONObject data) {
                mHome.postValue(data);
            }

            @Override
            public void onResponseMessage(MessageModel messageModel) {
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(ErrorModel errorModel) {
                mError.setValue(errorModel);
            }
        });
    }

    public void postLike(String id) {
        mRepository.postLike(id);
    }
}
