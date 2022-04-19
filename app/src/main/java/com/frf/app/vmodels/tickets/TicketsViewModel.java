package com.frf.app.vmodels.tickets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.frf.app.data.CompetitionsData;
import com.frf.app.data.TicketsMatchesData;
import com.frf.app.data.TicketsOffersData;
import com.frf.app.data.TicketsSectorsData;
import com.frf.app.models.ErrorModel;
import com.frf.app.models.MessageModel;
import com.frf.app.repository.tickets.TicketsRepository;
import com.frf.app.vmodels.MainViewModel;

public class TicketsViewModel extends MainViewModel {

    private TicketsRepository mRepository;
    public void setmRepository(TicketsRepository mRepository) {
        this.mRepository = mRepository;
    }

    MutableLiveData<CompetitionsData[]> mCompatitions;
    MutableLiveData<TicketsMatchesData[]> mMatches;
    MutableLiveData<TicketsSectorsData> mSectors;
    MutableLiveData<TicketsOffersData> mOffers;
    MutableLiveData<String> mSectorsSvg;

    public LiveData<CompetitionsData[]> initCompetitions() {
        if(mCompatitions == null) {
            mCompatitions = new MutableLiveData<>();
        }
        return mCompatitions;
    }

    public LiveData<TicketsMatchesData[]> initMatches() {
        if(mMatches == null) {
            mMatches = new MutableLiveData<>();
        }
        return mMatches;
    }

    public LiveData<TicketsSectorsData> initSectors() {
        if(mSectors == null) {
            mSectors = new MutableLiveData<>();
        }
        return mSectors;
    }

    public LiveData<TicketsOffersData> initOffers() {
        if(mOffers == null) {
            mOffers = new MutableLiveData<>();
        }
        return mOffers;
    }

    public LiveData<String> initSectorsSvg() {
        if(mSectorsSvg == null) {
            mSectorsSvg = new MutableLiveData<>();
        }
        return mSectorsSvg;
    }

    public void getCompetitions(){
        mRepository.getCompetitions(new TicketsRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(CompetitionsData[] data) {
                mCompatitions.postValue(data);
            }

            @Override
            public void onResponseSucces(TicketsMatchesData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsSectorsData data) {

            }

            @Override
            public void onResponseSucces(TicketsOffersData data) {

            }

            @Override
            public void onResponseSucces(String data) {

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

    public void getMatches(int id) {
        mRepository.getMatches(id, new TicketsRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(CompetitionsData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsMatchesData[] data) {
                mMatches.postValue(data);
            }

            @Override
            public void onResponseSucces(TicketsSectorsData data) {

            }

            @Override
            public void onResponseSucces(TicketsOffersData data) {

            }

            @Override
            public void onResponseSucces(String data) {

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

    public void getSectors(int id) {
        mRepository.getMatches(id, new TicketsRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(CompetitionsData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsMatchesData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsSectorsData data) {
                mSectors.postValue(data);
            }

            @Override
            public void onResponseSucces(TicketsOffersData data) {

            }

            @Override
            public void onResponseSucces(String data) {

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

    public void getSectorsSvg(int id) {
        mRepository.getSectorsSvg(id, new TicketsRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(CompetitionsData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsMatchesData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsSectorsData data) {

            }

            @Override
            public void onResponseSucces(TicketsOffersData data) {

            }

            @Override
            public void onResponseSucces(String data) {
                mSectorsSvg.postValue(data);
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

    public void getOffers(int id) {
        mRepository.getOffers(id, new TicketsRepository.AsyncResponse() {
            @Override
            public void onResponseSucces(CompetitionsData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsMatchesData[] data) {

            }

            @Override
            public void onResponseSucces(TicketsSectorsData data) {
            }

            @Override
            public void onResponseSucces(TicketsOffersData data) {
                mOffers.postValue(data);
            }

            @Override
            public void onResponseSucces(String data) {

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
