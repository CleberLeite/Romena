package com.frf.app.vmodels.arena;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.arena.ArenaRepository;
import com.frf.app.vmodels.MainViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class ArenaViewModel extends MainViewModel {

    private ArenaRepository mRepository;
    public void setmRepository(ArenaRepository mRepository) {
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

    public void setLike(int id){
        mRepository.setArenaPostLike(id);
    }

    public void getItens(String idUser, String page, String order, String category){
        mRepository.getArena(new ArenaRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(JSONObject data) {
                try {
                    data.put("isCategory", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        }, idUser, page, order, category);
    }

    public void getCategories(){
        mRepository.getCategories(new ArenaRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(JSONObject data) {
                try {
                    data.put("isCategory", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mHome.postValue(data);
            }

            @Override
            public void onResponseMessage(MessageModel messageModel) {

            }

            @Override
            public void onResponseError(ErrorModel errorModel) {

            }
        });
    }
}