package com.example.agenda.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.agenda.R;
import com.example.agenda.interfaces.ITarefaService;
import com.example.agenda.model.Tarefa;
import com.example.agenda.model.TarefaRequest;
import com.example.agenda.util.HttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TarefasActivity extends AppCompatActivity {

    private static final String TAG = "TarefasActivity";
    private List<Tarefa> tarefas = new ArrayList<>();
    private List<String> novaListaTarefas = new ArrayList<>();
    private ArrayAdapter<String> tarefasAdapter;
    private ListView listViewTarefas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefas);

        loadJSON();
    }

    private void loadJSON(){
        ITarefaService _client = HttpClient.getClient();
        Call<TarefaRequest> loadTarefa = _client.GetTarefa();

        loadTarefa.enqueue(new Callback<TarefaRequest>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<TarefaRequest> call, Response<TarefaRequest> response) {
                try {

                    Log.i(TAG, response.toString());
                    Log.i(TAG, response.body().getTarefa().get(0).toString());

                    tarefas = response.body().getTarefa();
                    tarefas.forEach((t) ->{
                        Log.i(TAG, t.getDescricao());
                        Tarefa novaTarefa = new Tarefa();
                        /*novaTarefa.setId(t.getId());
                        novaTarefa.setDescricao(t.getDescricao());*/
                        novaListaTarefas.add("Id: " + t.getId() + "\n" + "Descrição: " + t.getDescricao());
                    } );

                    /*for (Tarefa s : tarefas){
                        Log.i(TAG, s.getDescricao());
                    }*/

                    tarefasAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, novaListaTarefas);
                    listViewTarefas = findViewById(R.id.lst_tarefas);
                    listViewTarefas.setAdapter(tarefasAdapter);
                    tarefasAdapter.notifyDataSetChanged();

                    Toast.makeText(getBaseContext(),"Lista carregada com sucesso!", Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Log.i(TAG, "Ocorreu um erro!");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TarefaRequest> call, Throwable t) {

                Log.i(TAG, t.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadJSON();
    }
}