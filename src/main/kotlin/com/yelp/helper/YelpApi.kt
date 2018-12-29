package com.yelp.helper

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YelpApi {

    companion object {
        private const val VERSION = "v3"
    }

    @GET("$VERSION/businesses/search")
    fun search(
            @Query("location") location: String,
            @Query("term") term: String,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int
    ): Call<BusinessList>
}