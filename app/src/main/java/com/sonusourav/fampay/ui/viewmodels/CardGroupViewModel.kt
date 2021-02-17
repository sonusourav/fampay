package com.sonusourav.fampay.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sonusourav.fampay.data.models.CardGroup
import com.sonusourav.fampay.data.models.Response
import com.sonusourav.fampay.data.repositories.CardGroupRepository
import com.sonusourav.fampay.utils.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CardGroupViewModel(private val cardGroupRepository: CardGroupRepository) : ViewModel() {
    private val cardGroupLiveData = MutableLiveData<Resource<Response>>()
    private val compositeDisposable = CompositeDisposable()

    fun init() {
        cardGroupLiveData.postValue(Resource.loading(null))
        fetchCardGroupList()
    }

    fun fetchCardGroupList() {
        compositeDisposable.add(
                cardGroupRepository.getCardGroups()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ cardList ->
                            cardGroupLiveData.postValue(Resource.success(cardList))
                        }, {
                            Log.d("card", it.localizedMessage!! + "")
                            cardGroupLiveData.postValue(Resource.error("Something Went Wrong", null))
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getCards(): MutableLiveData<Resource<Response>> {
        return cardGroupLiveData
    }
}