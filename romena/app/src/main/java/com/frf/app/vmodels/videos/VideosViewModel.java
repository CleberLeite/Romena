package com.frf.app.vmodels.videos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.videos.VideosRepository;
import com.frf.app.vmodels.MainViewModel;

import org.json.JSONObject;

public class VideosViewModel extends MainViewModel {

    private VideosRepository mRepository;
    public void setmRepository(VideosRepository mRepository) {
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

    public void getItens(String page, String category, String fav, String order){
        mRepository.getVideos(page, category, fav, order, new VideosRepository.AsyncResponse() {
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

    public void setFollowVideosCategory(int id){
        mRepository.setFollowVideosCategory(id, new VideosRepository.AsyncResponse() {
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
