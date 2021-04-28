package ipvc.estg.smartcities.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface EndPoints {

    @GET("users")
    fun getUsers(): Call<List<User>> //é usado o List porque a resposta vem num array

    @FormUrlEncoded
    @POST("users/login")
    fun postLogin(@Field("email") email: String, @Field("password") password: String): Call<User>

    // MAP ENDPOINTS
    @GET("map")
    fun getMapPoints(): Call<List<MapIncidences>>

    @GET("map/id/{id}")
    fun getMapPointsById(@Path("id") id: Int): Call<MapIncidences>

    @GET("map/carTrafficProblem/{id}")
    fun getMapPointsTrafficProblem(@Path("id") id: Int): Call<List<MapIncidences>>

    @FormUrlEncoded
    @POST("map/add")
    fun addPoint(@Field("users_id") users_id: Int,
            @Field("latCoordinates") latCoordinates: Double,
            @Field("longCoordinates") longCoordinates: Double,
            @Field("title") title: String,
            @Field("description") description: String,
            @Field("image") image: String,
            @Field("carTrafficProblem") carTrafficProblem: Int,
            @Field("solved") solved: Int
    ): Call<MapIncidences>

    @FormUrlEncoded
    @PUT("map/update")
    fun updatePoint(@Field("id") id: Int,
            @Field("title") title: String,
            @Field("description") description: String,
            @Field("image") image: String,
            @Field("carTrafficProblem") carTrafficProblem: Int,
            @Field("solved") solved: Int
    ): Call<MapIncidences>

    @DELETE("map/delete/{id}")
    fun deletePoint(@Path("id") id: Int): Call<MapIncidences>

    @Multipart
    @POST("upload")
    fun upload(
            @Part("description") description: RequestBody,
            @Part file: MultipartBody.Part
    ): Call<ResponseBody>


}