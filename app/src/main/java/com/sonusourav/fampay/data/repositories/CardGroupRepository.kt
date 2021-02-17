package com.sonusourav.fampay.data.repositories

import com.sonusourav.fampay.data.api.ApiHelper
import com.sonusourav.fampay.data.models.Response
import io.reactivex.Single

class CardGroupRepository(private val apiHelper: ApiHelper) {

    fun getCardGroups(): Single<Response> {
        return apiHelper.getCardGroups()
    }
}