package com.example.agenda

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


private const val FILE_NAME = "backup.txt"
private const val DELITER = "#"
private const val WRITE_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    lateinit var nome: String
    lateinit var telefone: String
    lateinit var email: String
    lateinit var cidade: String

    private var contatos: MutableList<Pessoa> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
    }

    fun setListeners(){
        btn_salvar.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
                requestContactsPermission()
            }
            else{
                saveForm()
            }

            /*ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS), WRITE_REQUEST_CODE)
            saveForm()*/
        }
        btn_limpar.setOnClickListener {
            clear()
        }
        btn_visualizar.setOnClickListener {
            val resultIntent = Intent(this@MainActivity, ResultadoActivity::class.java)
            startActivity(resultIntent)
        }
    }

    fun saveForm() {
        nome = edtxt_nome.text.toString()
        telefone = edtxt_telefone.text.toString()
        email = edtxt_email.text.toString()
        cidade = edtxt_cidade.text.toString()

        if (nome.isNullOrEmpty() && telefone.isNullOrEmpty() && email.isNullOrEmpty() && cidade.isNullOrEmpty()){
            Toast.makeText(this,"Preencha os campos obrigatórios!", Toast.LENGTH_LONG).show()
        }
        else if (nome.isNullOrEmpty()){
            Toast.makeText(this,"Nome é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (telefone.isNullOrEmpty()){
            Toast.makeText(this,"Telefone é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else{
            contatos = load(FILE_NAME)
            var myContato = Pessoa(nome, telefone, email, cidade)
            var newContatos = contatos.plus(myContato)

            openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { fos->
                fos?.bufferedWriter().use { writer ->
                    newContatos.forEach { contato ->
                        writer?.appendln("#\n$contato")
                    }
                }
            }
            clear()
            Toast.makeText(this,"Contato salvo com sucesso!", Toast.LENGTH_LONG).show()
        }
    }

    fun load(file: String): MutableList<Pessoa>{
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

    fun clear(){
            nome = edtxt_nome.setText("").toString()
            telefone = edtxt_telefone.setText("").toString()
            email = edtxt_email.setText("").toString()
            cidade = edtxt_cidade.setText("").toString()
    }

    fun requestContactsPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)){
            //Toast.makeText(this,"...", Toast.LENGTH_LONG).show()
            Snackbar.make(cl_root_main_activity, "Permitir ao Agenda acesso aos contatos.", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK") {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_CONTACTS),
                        WRITE_REQUEST_CODE
                    )
                }.show()
        }
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissão concedida.", Toast.LENGTH_LONG).show()
            }
            else{
                Snackbar.make(cl_root_main_activity, "Permissão negada.", Snackbar.LENGTH_LONG ).show()
            }
        }
    }*/

}
