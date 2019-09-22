package com.example.agenda.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.example.agenda.ui.MainActivityKt.AD_APP_UNIT_ID;
import static com.example.agenda.ui.MainActivityKt.AD_UNIT_ID;

public class TarefasActivity extends AppCompatActivity {

    private static final String TAG = "TarefasActivity";
    private List<Tarefa> tarefas = new ArrayList<>();
    private List<String> novaListaTarefas = new ArrayList<>();
    private ArrayAdapter<String> tarefasAdapter;
    private ListView listViewTarefas;
    private FirebaseAuth mAuth;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefas);

        mAuth = FirebaseAuth.getInstance();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView_tarefas);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MobileAds.initialize(this,AD_APP_UNIT_ID);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        View btn_contatatos = findViewById(R.id.btn_fab_tarefas_for_contatos);
        View btn_sair = findViewById(R.id.btn_fab_tarefas);

        new SimpleTooltip.Builder(this)
                .anchorView(btn_contatatos)
                .text("Listagem de Contatos.")
                .gravity(Gravity.END)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show();

        new SimpleTooltip.Builder(this)
                .anchorView(btn_sair)
                .text("Sair do Aplicativo.")
                .gravity(Gravity.END)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show();

        loadJSON();
    }

    public void loadContacts(View view) {
        Intent intentResultado = new Intent(this, ResultadoActivity.class);
        startActivity(intentResultado);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    public void signOut(View view) {
        mAuth.signOut();
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
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