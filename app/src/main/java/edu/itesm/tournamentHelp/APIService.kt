package edu.itesm.tournamentHelp

import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("comics?apikey=<APIKEY>")
    suspend fun getComics() : Response<Results>
}