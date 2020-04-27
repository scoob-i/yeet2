package com.example.yeet

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

interface RecipeApiService {
    @GET("filter.php")
    fun recipeFilter(@Query("i") searchString: String): Observable<Model.Result>

    @GET("Search.php")
    fun recipeSearch(@Query("s")searchString: String): Observable<Model.Result>

    companion object {
        fun create(): RecipeApiService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://www.themealdb.com/api/json/v2/9973533/")
                .build()

            return retrofit.create(RecipeApiService::class.java)
        }
    }
}