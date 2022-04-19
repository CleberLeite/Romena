package com.frf.app.vmodels.news;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.news.NewsRepository;
import com.frf.app.vmodels.MainViewModel;

import org.json.JSONObject;

public class NewsViewModel extends MainViewModel {

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

    public void clearData() {
        mHome = new MutableLiveData<>();
    }

    public void clearMessage() {
        mMessage = new MutableLiveData<>();
    }

    public void getItens(String page, String category, String fav, String order, String bookmarked){
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
        }, page, category, fav, order, bookmarked);
    }

    public void setFollowNewsCategory(int id){
        mRepository.setFollowNewsCategory(id, new NewsRepository.AsyncResponse() {
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
}
