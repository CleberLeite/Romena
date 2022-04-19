package com.frf.app.vmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;


public class MainViewModel extends ViewModel {

    //Se holver algum erro
    protected MutableLiveData<ErrorModel> mError = new MutableLiveData<>();
    public LiveData<ErrorModel> getError (){
        return mError;
    }

    //Se tiver alguma mensagem
    protected MutableLiveData<MessageModel> mMessage = new MutableLiveData<>();
    public LiveData<MessageModel> getMessage (){
        return mMessage;
    }


    //Se estiver carregando
    protected MutableLiveData<Boolean> mLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading (){
        return mLoading;
    }
}
