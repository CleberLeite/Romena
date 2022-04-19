package com.frf.app.repository.user;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.frf.app.R;
import com.google.gson.Gson;
import com.frf.app.informations.UserInformation;
import com.frf.app.models.user.UserModel;
import com.frf.app.repository.MainRepository;
import com.frf.app.utils.AsyncOperation;
import com.frf.app.utils.SecurePreferences;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import static com.frf.app.activitys.MainActivity.prefs;
import static com.frf.app.utils.CONSTANTS.SHARED_KEY_USER_ID;
import static com.frf.app.utils.CONSTANTS.SHARED_PREFS_KEY_APNS;
import static com.frf.app.utils.UTILS.FLIP_VERTICAL;
import static com.frf.app.utils.UTILS.flip;
import static com.frf.app.utils.UTILS.rotateBitmap;

public class UserRepository extends MainRepository {


    private AsyncResponse asyncResponse;
    private UserModel savedUser;

    private final int OP_GET_USER = 0;
    private final int OP_UPLOAD_USER = 1;
    private final int OP_UPLOAD_FOTO = 2;
    private final int OP_POST_LOGIN = 3;
    private final int OP_POST_CREATE = 4;
    private final int OP_FORGOT = 5;
    private final int OP_CHANGE_PASSWORD = 8;
    private final int OP_GET_USER_PROFILE = 9;
    private final int OP_SET_DELETE_ACCOUNT = 10;
    private final int OP_SET_POST_ARENA = 11;
    private final int OP_GET_COINS = 12;
    private final int OP_POST_AUTH = 12;

    public UserRepository() {}

    public UserRepository(Activity context) {
        this.context = context;
    }

    public UserRepository(Activity context, SecurePreferences preferences) {
        this.context = context;
        this.securePreferences = preferences;
        getSavedUser();
    }

    public void getNotNewIdUser(AsyncResponse response) {
        this.asyncResponse = response;
        asyncResponse.onResponseSucces(null);
    }

    //region Get Methods
    //Faz upload da foto
    public void uploadFoto (Uri photoUri, AsyncResponse response){
        this.asyncResponse = response;
        uploadPhoto(photoUri);
    }

    public void postArena(ArrayList<String> imgs, String desc, int idCat, AsyncResponse response) {
        this.asyncResponse = response;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("imgs", new Gson().toJson(imgs));
        params.put("desc", desc);
        params.put("idCategory", idCat);

        new AsyncOperation(context, AsyncOperation.TASK_ID_POST_ARENA, OP_SET_POST_ARENA, this).execute(params);
    }

    public void getSaved (AsyncResponse response){
        this.asyncResponse = response;
        if(hasUser()){
            if (savedUser == null) {
                UserModel model;
                Gson gson = new Gson();
                String nData = securePreferences.getString(SHARED_KEY_USER, "");
                model = gson.fromJson(nData, UserModel.class);
                savedUser = model;
            }
            asyncResponse.onResponseSucces(changeUserModelValues(savedUser));
        }else{
            asyncResponse.onResponseError("Sem Usuario Salvo");
        }
    }


    //Faz login do usuario
    public void loginUser (String email, String password, AsyncResponse response){
        this.asyncResponse = response;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("email", email);
        params.put("password", password);

        new AsyncOperation(context, AsyncOperation.TASK_ID_SIGN_IN, OP_POST_LOGIN, this).execute(params);
    }
    //Cria novo usuario
    public void createUser (String name, String email, String password, String  dataNascimento, AsyncResponse response){
        this.asyncResponse = response;
        savedUser = new UserModel(name, email);

        Hashtable<String, Object> params = new Hashtable<>();
        params.put("nome", name);
        params.put("email", email );
        params.put("senha", password);
        if (!dataNascimento.replaceAll(" ","").equals("")) {
            params.put("birthDay", dataNascimento);
        }
        new AsyncOperation(context, AsyncOperation.TASK_ID_SIGN_UP, OP_POST_CREATE, this).execute(params);
    }

    //Esqueceu a senha
    public void forgot (String email, AsyncResponse response) {
        this.asyncResponse = response;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("email", email);
        new AsyncOperation(context, AsyncOperation.TASK_ID_PASSWORD_FORGOTTEN, OP_FORGOT, this).execute(params);
    }

    public void auth(String id, AsyncResponse response) {
        this.asyncResponse = response;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("user_id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_POST_AUTH, OP_POST_AUTH, this).execute(params);
    }

    public void getUserProfile(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        new AsyncOperation(context, AsyncOperation.TASK_ID_GET_USER_PROFILE, OP_GET_USER_PROFILE, this).execute();
    }

    public void deleteAccount(String id, AsyncResponse response) {
        this.asyncResponse = response;
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("id", id);
        new AsyncOperation(context, AsyncOperation.TASK_ID_POST_DELETE_ACCOUNT, OP_SET_DELETE_ACCOUNT, this).execute(params);
    }

    //Esqueceu a senha

    //endregion

    private UserModel changeUserModelValues(UserModel nModel){

        String apns = securePreferences.getString(SHARED_PREFS_KEY_APNS, "");

        nModel.setApns(apns);

        prefs.put(SHARED_KEY_USER_ID, String.valueOf(nModel.getId()));

        UserInformation.setUserId(nModel.getId());
        UserInformation.setUserData(nModel);

        //Salvar usuario na memoria
        saveUser(nModel);

        return nModel;
    }

    public boolean hasUser (){
        return securePreferences.containsKey(SHARED_KEY_USER);
    }

    private void saveUser (UserModel user){
        Gson gson = new Gson();
        securePreferences.removeValue(SHARED_KEY_USER);
        securePreferences.put(SHARED_KEY_USER, gson.toJson(user));
    }

    private void getSavedUser() {
        if(securePreferences.containsKey(SHARED_KEY_USER)){
            Gson gson = new Gson();
            String nUser = securePreferences.getString(SHARED_KEY_USER, "");
            savedUser = gson.fromJson(nUser, UserModel.class);
        }
    }

    private void uploadPhoto (Uri photoUri){
        Hashtable<String, Object> params = new Hashtable<>();
        params.put("sourceFile", photoUri);

        new AsyncOperation(context, AsyncOperation.TASK_ID_UPLOAD_PICTURE, OP_UPLOAD_FOTO, this).execute(params);
    }

    //region Async Response
    @Override
    public void onSucces(int opId, int status, JSONObject response) {
        super.onSucces(opId, status, response);

        switch (opId){
            case OP_POST_AUTH:
            case OP_GET_USER:
            case OP_POST_LOGIN: {
                if (response.has("Object")) {
                    try {
                        Gson gson = new Gson();
                        UserModel data = gson.fromJson(response.getString("Object"), UserModel.class);
                        savedUser = data;

                        if(data != null){
                            UserInformation.setUserId((data.getId()));
                            if (data.getToken() != null && !data.getToken().equals("")) {
                                prefs.put(SHARED_KEY_USER_TOKEN, data.getToken());
                            }
                            new Handler().postDelayed(() -> asyncResponse.onResponseSucces(changeUserModelValues(data)), 300);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            break;
            case OP_SET_POST_ARENA: {
                asyncResponse.onResponseSucces(new UserModel());
            }
            break;
            case OP_UPLOAD_USER:{
                asyncResponse.onResponseSucces(changeUserModelValues(savedUser));
                asyncResponse.onResponseMessage("Utilizatorul actualizat cu succes!");
            }
            break;
            case OP_UPLOAD_FOTO:{
                if(response.has("img")){
                    try {
                        UserModel data = savedUser;
                        data.setImg(response.getString("img"));
                        saveUser(data);
                        asyncResponse.onResponseSucces(changeUserModelValues(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case OP_POST_CREATE:{
                 if(response.has("Object")){
                    try {
                        Gson gson = new Gson();
                        UserModel data = gson.fromJson(response.getString("Object"), UserModel.class);
                        if(data != null){
                            savedUser.setId(data.getId());
                            UserInformation.setUserId((data.getId()+""));
                            if (data.getToken() != null & !data.getToken().equals("")) {
                                prefs.put(SHARED_KEY_USER_TOKEN, data.getToken());
                            }
                            asyncResponse.onResponseSucces(changeUserModelValues(savedUser));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case OP_FORGOT:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case OP_CHANGE_PASSWORD:{
                asyncResponse.onResponseMessage("Parola actualizată");
            }
            break;

            case OP_GET_USER_PROFILE: {
                if(response.has("Object")){
                    try {
                        Gson gson = new Gson();
                        UserModel data;
                        data = gson.fromJson(response.getString("Object"), UserModel.class);

                        if(data != null){
                            savedUser = data;
                            UserInformation.setUserId((data.getId()+""));
                            asyncResponse.onResponseSucces(changeUserModelValues(savedUser));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        asyncResponse.onResponseError("");
                    }
                }else {
                    asyncResponse.onResponseError("");
                }
            }
            break;

            case  OP_SET_DELETE_ACCOUNT: {
                try {
                    asyncResponse.onResponseMessage(response.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    asyncResponse.onResponseMessage("Cont șters cu succes");
                }
            }
            break;
        }
    }

    @Override
    public void onError(int opId, int status, JSONObject response) {
        super.onError(opId, status, response);
        switch (opId){
            case OP_SET_POST_ARENA: {
                if (response.has("msg")) {
                    try {
                        asyncResponse.onResponseError(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        asyncResponse.onResponseError(context.getString(R.string.msg_erro_post_insert));
                    }
                }else {
                    asyncResponse.onResponseError(context.getString(R.string.msg_erro_post_insert));
                }
            }
            break;
            case OP_GET_USER:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    if(savedUser != null){
                        asyncResponse.onResponseSucces(savedUser);
                    }else{
                        asyncResponse.onResponseError(context.getResources().getString(R.string.eroare_la_cautarea_utilizatorului));
                    }

                }
            }
            break;
            case OP_UPLOAD_USER:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    asyncResponse.onResponseError("OP_UPLOAD_USER");
                }
            }
            break;

            case OP_UPLOAD_FOTO:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    asyncResponse.onResponseError("OP_UPLOAD_FOTO");
                }
            }
            break;
            case OP_POST_AUTH:
            case OP_POST_LOGIN:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    asyncResponse.onResponseError("OP_POST_LOGIN");
                }
            }
            break;
            case OP_POST_CREATE:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    asyncResponse.onResponseError("OP_POST_CREATE");
                }
            }
            break;
            case OP_FORGOT:{
                if(response.has("msg")){
                    try {
                        asyncResponse.onResponseMessage(response.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    asyncResponse.onResponseError("OP_FORGOT");
                }
            }
            break;
            case  OP_SET_DELETE_ACCOUNT: {
                try {
                    asyncResponse.onResponseError(response.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    asyncResponse.onResponseError("Contul nu poate fi șters");
                }
            }
            break;
        }
    }
    //endregion

    public interface AsyncResponse {
        void onResponseSucces (UserModel data);
        void onResponseMessage (String message);
        void onResponseError (String message);
    }
}
