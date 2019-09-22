package com.example.agenda.util;

import com.example.agenda.interfaces.ITarefaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private static ITarefaService _tarefaService;
    private static String BASE_URL = "http://infnet.educacao.ws";

    public static ITarefaService getClient(){
        if (_tarefaService == null){
            Gson gson = new GsonBuilder()
                    .setDateFormat("dd-MM-yyyy")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory
                    .create(gson)).build();
            _tarefaService = retrofit.create(ITarefaService.class);
        }
        return _tarefaService;
    }
}
