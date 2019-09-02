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
import android.text.method.TextKeyListener.clear



class ResultadoActivity : AppCompatActivity() {

    var myContacts = mutableListOf<String>()
    //var listKey = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

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

                /*val snapshotIterator = dataSnapshot.children
                val iterator = snapshotIterator.iterator()

                myContacts.clear()

                while (iterator.hasNext()) {
                    val next = iterator.next() as DataSnapshot

                    val match = next.getValue(Pessoa::class.java)
                    val key = next.key
                    //listKey.add(key!!)
                    myContacts.add(match.toString())
                }*/
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())
            }
        })

        val contatoAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myContacts)
        lst_contatos.adapter = contatoAdapter
        contatoAdapter.notifyDataSetChanged()

        Toast.makeText(this,"Lista carregada com sucesso!", Toast.LENGTH_LONG).show()
    }
}