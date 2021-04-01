package ipvc.estg.smartcities.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface EndPoints {

    @GET("users")
    fun getUsers(): Call<List<User>> //é usado o List porque a resposta vem num array

    @GET("users/login/{email}/{password}")
    fun getUserLogin(@Path("email") email: String, @Path("password") password: String): Call<User>

    // MAP ENDPOINTS

    @GET("map")
    fun getMapPoints(): Call<List<MapIncidences>>
}