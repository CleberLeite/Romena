package com.frf.app.vmodels.shop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frf.app.data.ShopProductData;
import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.shop.ShopRepository;
import com.frf.app.vmodels.MainViewModel;

public class ShopViewModel extends MainViewModel {

    private ShopRepository mRepository;
    public void setmRepository(ShopRepository mRepository) {
        this.mRepository = mRepository;
    }

    MutableLiveData<ShopProductData[]> mShop;

    public LiveData<ShopProductData[]> getShop (){
        if(mShop == null) {
            mShop = new MutableLiveData<>();
        }

        return mShop;
    }

    public void getItens(int page, int category){
        mRepository.getItens(page, category, new ShopRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(ShopProductData[] data) {
                mShop.postValue(data);
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

    public void search(String keyword) {
        mRepository.initSearch(keyword, new ShopRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(ShopProductData[] data) {
                mShop.postValue(data);
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
