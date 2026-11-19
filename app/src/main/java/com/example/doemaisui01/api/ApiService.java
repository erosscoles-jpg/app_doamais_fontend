package com.example.doemaisui01.api;

import com.example.doemaisui01.api.ApiModels.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ---------- Campanhas ----------
    @GET("campaigns")
    Call<List<Campaign>> listCampaigns();

    @GET("campaigns/{id}")
    Call<Campaign> getCampaign(@Path("id") long id);

    @POST("campaigns")
    Call<Campaign> createCampaign(@Body Campaign body);

    @PUT("campaigns/{id}")
    Call<Campaign> updateCampaign(@Path("id") long id, @Body Campaign body);

    @DELETE("campaigns/{id}")
    Call<Void> deleteCampaign(@Path("id") long id);

    // ---------- Doadores ----------
    @GET("donors")
    Call<List<Donor>> listDonors();

    @GET("donors/{id}")
    Call<Donor> getDonor(@Path("id") long id);

    @POST("donors")
    Call<Donor> createDonor(@Body DonorCreate body);

    @PUT("donors/{id}")
    Call<Donor> updateDonor(@Path("id") long id, @Body DonorUpdate body);

    @DELETE("donors/{id}")
    Call<Void> deleteDonor(@Path("id") long id);

    // ---------- Funcionarios ----------
    @GET("employees")
    Call<List<Employee>> listEmployees();

    @POST("employees")
    Call<Employee> createEmployee(@Body EmployeeCreate body);

    @DELETE("employees/{id}")
    Call<Void> deleteEmployee(@Path("id") long id);

    // ---------- Ficha do doador ----------
    @GET("donors/{donorId}/form")
    Call<DonorForm> getForm(@Path("donorId") long donorId);

    @PUT("donors/{donorId}/form")
    Call<DonorForm> saveForm(@Path("donorId") long donorId, @Body DonorFormRequest body);

    @PUT("donors/{donorId}/form/evaluation")
    Call<DonorForm> evaluateForm(@Path("donorId") long donorId, @Body DonorFormEvaluation body);

    // status e opcional: passe null para listar todas (ex.: "PENDING")
    @GET("donor-forms")
    Call<List<DonorForm>> listForms(@Query("status") String status);

    // ---------- Inscricoes ----------
    // donorId e campaignId sao opcionais: passe null para nao filtrar
    @GET("enrollments")
    Call<List<Enrollment>> listEnrollments(@Query("donorId") Long donorId,
                                           @Query("campaignId") Long campaignId);

    @GET("enrollments/{id}")
    Call<Enrollment> getEnrollment(@Path("id") long id);

    @POST("enrollments")
    Call<Enrollment> createEnrollment(@Body EnrollmentRequest body);

    @PATCH("enrollments/{id}/status")
    Call<Enrollment> updateEnrollmentStatus(@Path("id") long id, @Body EnrollmentStatus body);
}