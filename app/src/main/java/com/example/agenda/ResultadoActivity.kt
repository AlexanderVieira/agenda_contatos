package com.example.agenda

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_resultado.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultadoActivity : AppCompatActivity() {

    var myContacts = mutableListOf<Pessoa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        var dataBaseRef = FirebaseDatabase.getInstance().getReference()
        var contatoRef = dataBaseRef.child("usuarios")
        contatoRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*val value = dataSnapshot.getValue().toString()
                Log.i("FIREBASE", "Value is: " + value!!)*/
                myContacts.clear()
                for (dados in dataSnapshot.children){
                    var contato = dados.getValue(Pessoa::class.java)
                    contato?.let {
                        myContacts.add(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })
        val contatoAdapter = ArrayAdapter<Pessoa>(this, android.R.layout.simple_list_item_1, myContacts)
        lst_contatos.adapter = contatoAdapter
        contatoAdapter.notifyDataSetChanged()
        Toast.makeText(this,"Lista carregada com sucesso!", Toast.LENGTH_LONG).show()
    }
}