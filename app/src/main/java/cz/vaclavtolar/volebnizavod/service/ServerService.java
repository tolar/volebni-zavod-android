package cz.vaclavtolar.volebnizavod.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cz.vaclavtolar.volebnizavod.dto.Election;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static cz.vaclavtolar.volebnizavod.service.ServerApi.BASE_URL;

public class ServerService {

    private static ServerService instance;

    private ServerApi serverApi;


    private ServerService() {
        initRestApiClient();
    }

    private void initRestApiClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        ObjectMapper objectMapper = new ObjectMapper();
        JacksonConverterFactory converterFactory = JacksonConverterFactory.create(objectMapper);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(converterFactory)
                .client(httpClient)
                .build();
        serverApi = retrofit.create(ServerApi.class);
    }

    public Call<List<Election>> getAllElections() {
        return serverApi.getAllElections();
    }

    @NotNull
    public static ServerService getInstance() {
        if (instance == null) {
            instance = new ServerService();
        }
        return instance;
    }
}
