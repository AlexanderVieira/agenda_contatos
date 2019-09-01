package com.example.agenda

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

private const val WRITE_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    lateinit var nome: String
    lateinit var senha: String
    lateinit var telefone: String
    lateinit var celular: String
    lateinit var email: String
    lateinit var cpf: String
    lateinit var cidade: String
    //private lateinit var database: FirebaseDatabase
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
        senha = edtxt_senha.text.toString()
        telefone = edtxt_telefone.text.toString()
        celular = edtxt_celular.text.toString()
        email = edtxt_email.text.toString()
        cpf = edtxt_cpf.text.toString()
        cidade = edtxt_cidade.text.toString()

        if (nome.isNullOrEmpty() && senha.isNullOrEmpty()
            && telefone.isNullOrEmpty() && celular.isNullOrEmpty()
            && email.isNullOrEmpty() && cpf.isNullOrEmpty() && cidade.isNullOrEmpty()){
            Toast.makeText(this,"Preencha os campos obrigatórios!", Toast.LENGTH_LONG).show()
        }
        else if (nome.isNullOrEmpty()){
            Toast.makeText(this,"Nome é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (senha.isNullOrEmpty()){
            Toast.makeText(this,"Senha é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (email.isNullOrEmpty()){
            Toast.makeText(this,"E-mail é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else{
            //contatos = load(FILE_NAME)
            var myContato = Pessoa(nome, senha, telefone, celular, email, cpf, cidade)
            var dataBaseRef = FirebaseDatabase.getInstance().getReference()
            var contatoRef = dataBaseRef.child("usuarios")
            contatoRef.child("002").setValue(myContato)
            clear()
            Toast.makeText(this,"Contato salvo com sucesso!", Toast.LENGTH_LONG).show()
        }
    }

    fun clear(){
            nome = edtxt_nome.setText("").toString()
            senha = edtxt_senha.setText("").toString()
            telefone = edtxt_telefone.setText("").toString()
            celular = edtxt_celular.setText("").toString()
            email = edtxt_email.setText("").toString()
            cpf = edtxt_cpf.setText("").toString()
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
