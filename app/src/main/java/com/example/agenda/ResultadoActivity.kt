package com.example.agenda

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_resultado.*

private const val FILE_NAME = "backup.txt"
private const val DELITER = "#"

class ResultadoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        var myContacts = load(FILE_NAME)
        val contatoAdapter = ArrayAdapter<Pessoa>(this, android.R.layout.simple_list_item_1, myContacts)
        lst_contatos.adapter = contatoAdapter
        Toast.makeText(this,"Lista carregada com sucesso!", Toast.LENGTH_LONG).show()
    }

    fun load(file: String): List<Pessoa>{
        val myContatos = mutableListOf<Pessoa>()
        openFileInput(FILE_NAME).use {fis->
            fis.bufferedReader().use {reader->
                val lines  = reader.readLines()
                var index = 0
                while (index < lines.size){
                    if (lines[index++] == DELITER){
                        val nome = lines[index++]
                        val telefone = lines[index++]
                        val email = lines[index++]
                        val cidade = lines[index++]
                        myContatos.add(Pessoa(nome, telefone, email, cidade))
                    } else {
                        Toast.makeText(this,"Erro Aplicativo!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return  myContatos
    }


}
