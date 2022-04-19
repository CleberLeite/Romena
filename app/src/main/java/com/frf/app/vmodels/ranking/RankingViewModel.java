package com.frf.app.vmodels.ranking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.repository.news.NewsRepository;
import com.frf.app.vmodels.MainViewModel;

import org.json.JSONObject;

public class RankingViewModel extends MainViewModel {

    private NewsRepository mRepository;
    public void setmRepository(NewsRepository mRepository) {
        this.mRepository = mRepository;
    }

    MutableLiveData<JSONObject> mHome;

    public LiveData<JSONObject> getHome (){
        if(mHome == null) {
            mHome = new MutableLiveData<>();
        }

        return mHome;
    }

    /*public void getItens(){
        mRepository.getNews(new NewsRepository.AsyncResponse() {
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
    }*/
}
