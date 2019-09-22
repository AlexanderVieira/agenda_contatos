package com.example.agenda.interfaces;

import com.example.agenda.model.TarefaRequest;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ITarefaService {

    @Headers("Content-Type: application/json")
    @GET("/dadosAtividades.php")
    Call<TarefaRequest> GetTarefa();

}
