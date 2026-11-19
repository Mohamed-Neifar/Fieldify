package com.example.fieldify.network;

import com.example.fieldify.network.model.CreateReservationRequest;
import com.example.fieldify.network.model.CreateTerrainRequest;
import com.example.fieldify.network.model.InvitationDto;
import com.example.fieldify.network.model.InviteFriendRequest;
import com.example.fieldify.network.model.ReservationDto;
import com.example.fieldify.network.model.RespondInvitationRequest;
import com.example.fieldify.network.model.SportDto;
import com.example.fieldify.network.model.TerrainDto;
import com.example.fieldify.network.model.UpdateReservationStatusRequest;
import com.example.fieldify.network.model.UserDto;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FieldifyApiService {

    @GET("api/sports")
    Call<List<SportDto>> getSports();

    @GET("api/users/players")
    Call<List<UserDto>> getRegisteredPlayers();

    @GET("api/terrains")
    Call<List<TerrainDto>> getTerrains(
            @Query("sport") String sport,
            @Query("city") String city,
            @Query("maxPrice") Double maxPrice,
            @Query("search") String search
    );

    @GET("api/terrains/{id}")
    Call<TerrainDto> getTerrainDetail(@Path("id") Long id);

    @GET("api/terrains/{id}/booked-slots")
    Call<List<String>> getBookedSlots(@Path("id") Long id, @Query("date") String date);

    @Multipart
    @POST("api/upload")
    Call<List<String>> uploadFiles(@Part List<MultipartBody.Part> files);

    @POST("api/reservations")
    Call<ReservationDto> createReservation(@Body CreateReservationRequest request);

    @GET("api/reservations/me")
    Call<List<ReservationDto>> getMyReservations();

    @PATCH("api/reservations/{id}/cancel")
    Call<Void> cancelReservation(@Path("id") Long id);

    @POST("api/reservations/{id}/invite")
    Call<Void> inviteFriend(@Path("id") Long id, @Body InviteFriendRequest request);

    @GET("api/invitations/me")
    Call<List<InvitationDto>> getMyInvitations();

    @PATCH("api/invitations/{id}/respond")
    Call<Void> respondInvitation(@Path("id") Long id, @Body RespondInvitationRequest request);

    @GET("api/manager/terrains")
    Call<List<TerrainDto>> getManagerTerrains();

    @POST("api/manager/terrains")
    Call<TerrainDto> createManagerTerrain(@Body CreateTerrainRequest request);

    @GET("api/manager/reservations")
    Call<List<ReservationDto>> getManagerReservations();

    @PATCH("api/manager/reservations/{id}/status")
    Call<Void> updateReservationStatus(@Path("id") Long id, @Body UpdateReservationStatusRequest request);
}