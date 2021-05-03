package ipvc.estg.smartcities.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//var IP_ADRESS: String = "http://172.16.177.194/myslim/api/"
//var IP_ADRESS: String = "http://192.168.1.4/myslim/api/"
var IP_ADRESS: String = "http://10.0.2.2/myslim/api/"



object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
//        .baseUrl("http://10.0.2.2/myslim/api/")
        .baseUrl(IP_ADRESS)

        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}