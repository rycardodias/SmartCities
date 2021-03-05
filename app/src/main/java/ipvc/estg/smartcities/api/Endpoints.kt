package ipvc.estg.smartcities.api

import retrofit2.Call
import retrofit2.http.*

interface Endpoints {
    @GET("users/login/{email}/{password}")
    fun getUsers(@Path("email") email: String, @Path("password") password: String): Call<List<Users>>
}