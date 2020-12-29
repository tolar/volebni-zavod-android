package cz.vaclavtolar.volebnizavod.service;

import java.util.List;

import cz.vaclavtolar.volebnizavod.dto.Election;
import cz.vaclavtolar.volebnizavod.dto.ElectionData;
import cz.vaclavtolar.volebnizavod.dto.ElectionDistrictData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServerApi {

    String BASE_URL = "https://volebnizavod.herokuapp.com/api/v1/";

    @GET("elections")
    Call<List<Election>> getAllElections();

    @GET("elections/{id}")
    Call<ElectionData> getElection(@Path("id") int id);

    @GET("elections/{id}/districts")
    Call<List<ElectionDistrictData>> getElectionDistricts(@Path("id") int id);
}
