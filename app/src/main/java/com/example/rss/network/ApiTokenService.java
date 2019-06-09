package com.example.rss.network;

import com.example.rss.model.Email;
import com.example.rss.model.LoginResponse;
import com.example.rss.model.LogoutResponse;
import com.example.rss.model.RegisterResponse;
import com.example.rss.model.Reserva;
import com.example.rss.model.Site;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiTokenService {
    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password);

    //@POST("api/register")
    //Call<RegisterResponse>register(@Body User user);

    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password);
    //@POST("api/login")
    //Call<LoginResponse>login(@Body User user);

    @POST("api/logout")
    Call<LogoutResponse> logout(
            //@Header("Authorization") String token
    );

    @GET("api/reservas")
    Call<ArrayList<Reserva>> getReservas(
            //@Header("Authorization") String token
    );

    @POST("api/reservas")
    Call<Reserva> createReserva(
            //@Header("Authorization") String token,
            @Body Reserva reserva);

    @PUT("api/reservas/{id}")
    Call<Reserva> updateReserva(
            //@Header("Authorization") String token,
            @Body Reserva reserva,
            @Path("id") int id);

    @DELETE("api/reservas/{id}")
    Call<ResponseBody> deleteReserva(
            //@Header("Authorization") String token,
            @Path("id") int id);

    @POST("api/email")
    Call<ResponseBody> sendEmail(@Body Email email);
}

