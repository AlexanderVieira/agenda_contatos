package com.example.agenda

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_resultado.*


class ResultadoActivity : AppCompatActivity() {

    var myContacts = mutableListOf<String>()
    //var listKey = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        val mAdView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        var dataBaseRef = FirebaseDatabase.getInstance().getReference()
        var contatoRef = dataBaseRef.child("usuarios")

        contatoRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var value = dataSnapshot.getValue().toString()
                Log.i("FIREBASE", "Value is: " + value!!)

                for (contatoSnapshot in dataSnapshot.children) {

                    var contato = contatoSnapshot.getValue(Pessoa::class.java)
                    myContacts.add(contato.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("FIREBASE", "Failed to read value.", error.toException())
            }
        })

        val contatoAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myContacts)
        lst_contatos.adapter = contatoAdapter
        contatoAdapter.notifyDataSetChanged()

        Toast.makeText(this,"Lista carregada com sucesso!", Toast.LENGTH_LONG).show()
    }
}