package com.frf.app.vmodels.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.activitys.SplashActivity;
import com.frf.app.informations.UserInformation;
import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.user.UserRepository;
import com.frf.app.vmodels.MainViewModel;

import java.util.ArrayList;

public class UserViewModel extends MainViewModel {

    //Repositorio
    private UserRepository mRepository;
    public void setmRepository(UserRepository mRepository) {
        this.mRepository = mRepository;
    }

    //LiveData
    private MutableLiveData<UserModel> mUser;
    public LiveData<UserModel> getUserData (){
        if(mUser == null){
            mUser = new MutableLiveData<>();
        }
        return mUser;
    }


    public void singIn (String email, String senha){
            loginUser(email, senha);
    }

    public void singUp (String nome, String email, String senha, String dataNascimento){
            createUser(nome, email, senha, dataNascimento);
    }

    public void auth(String id) {
        mRepository.auth(id, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                data.setIdMsg(2);
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel = new MessageModel();
                messageModel.setMsg(message);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {

            }
        });
    }

    public void forgot(String email){
        forgotEmail(email);
    }

    public void getUser (boolean hasRequested){
        getUserAccount(hasRequested);
    }

    public void getUserProfile() {
        mRepository.getUserProfile(new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel = new MessageModel();
                messageModel.setMsg(message);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(message);
                mError.postValue(errorModel);
            }
        });
    }

    public void setPost(ArrayList<String> imgs, String desc, int idCat) {
        setPostArena(imgs, desc, idCat);
    }

    private void setPostArena(ArrayList<String> imgs, String desc, int idCat) {
        mRepository.postArena(imgs, desc, idCat, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                data.setIdMsg(1);
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {

            }

            @Override
            public void onResponseError(String message) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(1);
                errorModel.setMsg(message);
                mError.postValue(errorModel);
            }
        });
    }

    private void getUserAccount(final boolean hasRequested){
        mRepository.getSaved(new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                if(hasRequested){
                    MessageModel messageModel = new MessageModel();
                    messageModel.setMsg(message);
                    mMessage.postValue(messageModel);
                }
            }

            @Override
            public void onResponseError(String message) {
                if(hasRequested) {
                    ErrorModel errorModel = new ErrorModel();
                    errorModel.setError(1);
                    errorModel.setMsg(message);
                    mError.postValue(errorModel);
                }
            }
        });
    }


    //Pega o usuario por login
    private void loginUser (String email, String senha){
        mRepository.loginUser(email, senha, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                data.setIdMsg(2);
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel = new MessageModel();
                messageModel.setMsg(message);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(0);
                errorModel.setMsg(message);
                mError.postValue(errorModel);
            }
        });
    }

    private void createUser (String nome, String email, String senha, String  dataNascimento){
        mRepository.createUser(nome, email, senha, dataNascimento, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                data.setIdMsg(1);
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel = new MessageModel();
                messageModel.setMsg(message);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(0);
                errorModel.setMsg(message);
                mError.postValue(errorModel);
            }
        });
    }

    //Esqueceu a senha
    private void forgotEmail (String email){
        mRepository.forgot(email, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel = new MessageModel();
                messageModel.setMsg(message);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(0);
                errorModel.setMsg(message);
                mError.postValue(errorModel);
            }
        });
    }

    public void uploadPhoto (Uri photoUri){
        changePhoto(photoUri);
    }

    //Muda a foto do usuario
    private void changePhoto (Uri photoUri){
        mRepository.uploadFoto(photoUri, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {
                mUser.postValue(data);
            }

            @Override
            public void onResponseMessage(String message) {
                MessageModel messageModel = new MessageModel();
                messageModel.setMsg(message);
                mMessage.postValue(messageModel);
            }

            @Override
            public void onResponseError(String message) {
                ErrorModel errorModel = new ErrorModel();
                errorModel.setError(0);
                errorModel.setMsg(message);
                mError.postValue(errorModel);
            }
        });
    }

    public void deleteAccount(String id, Context context) {
        mRepository.deleteAccount(id, new UserRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(UserModel data) {

            }

            @Override
            public void onResponseMessage(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                UserInformation.ClearUserData(context);
                Intent i = new Intent(context, SplashActivity.class);
                i.putExtra("resetSSO", true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

            @Override
            public void onResponseError(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
