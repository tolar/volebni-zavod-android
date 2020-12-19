package cz.vaclavtolar.volebnizavod.service;

import java.util.List;

import cz.vaclavtolar.volebnizavod.dto.Election;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ServerApi {

    String BASE_URL = "https://volebnizavod.herokuapp.com/api/v1/";
//    String BASE_URL = "https://tolar:8080/api/v1/";

    @GET("elections")
    Call<List<Election>> getAllElections();
}
