package com.example.agenda

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

private const val FILE_NAME = "backup.txt"
private const val DELITER = "#"
private const val WRITE_REQUEST_CODE = 1
const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
const val AD_APP_UNIT_ID = "ca-app-pub-6747684087188676~8801996200"

class MainActivity : AppCompatActivity() {

    lateinit var nome: String
    lateinit var senha: String
    lateinit var confirmaSenha: String
    lateinit var celular: String
    lateinit var email: String
    lateinit var cpf: String
    lateinit var cidade: String
    private lateinit var mInterstitialAd: InterstitialAd
    private var contatos: MutableList<Pessoa> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, AD_APP_UNIT_ID)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = AD_UNIT_ID
        /**/mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        edtxt_cpf.addTextChangedListener(Mascara.mask("###.###.###-##", edtxt_cpf))
        setListeners()
    }

    fun setListeners(){
        btn_salvar.setOnClickListener {view ->
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
                requestContactsPermission()
                saveForm(view)
            }
            else{
                saveForm(view)
            }

        }
        btn_limpar.setOnClickListener {
            clear()
        }
        btn_visualizar.setOnClickListener {

            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.i("TAG", "The interstitial wasn't loaded yet.")
            }

            val resultIntent = Intent(this@MainActivity, ResultadoActivity::class.java)
            startActivity(resultIntent)
        }
    }

    fun saveForm(view: View) {
        nome = edtxt_nome.text.toString()
        senha = edtxt_senha.text.toString()
        confirmaSenha = edtxt_confirmaSenha.text.toString()
        celular = edtxt_celular.text.toString()
        email = edtxt_email.text.toString()
        cpf = edtxt_cpf.text.toString()
        cidade = edtxt_cidade.text.toString()

        if (nome.isNullOrEmpty() && senha.isNullOrEmpty()
            && confirmaSenha.isNullOrEmpty() && celular.isNullOrEmpty()
            && email.isNullOrEmpty() && cpf.isNullOrEmpty() && cidade.isNullOrEmpty()){
            Toast.makeText(this,"Preencha os campos obrigatórios!", Toast.LENGTH_LONG).show()
        }
        else if (nome.isNullOrEmpty()){
            Toast.makeText(this,"Nome é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (senha.isNullOrEmpty()){
            Toast.makeText(this,"Senha é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (confirmaSenha.isNullOrEmpty()){
            Toast.makeText(this,"Confirmar Senha é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (senha != confirmaSenha){
            Toast.makeText(this,"Senha não confere!", Toast.LENGTH_LONG).show()
        }
        else if (email.isNullOrEmpty()){
            Toast.makeText(this,"E-mail é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (celular.isNullOrEmpty()){
            Toast.makeText(this,"Celular é obrigatório!", Toast.LENGTH_LONG).show()
        }
        else if (cidade.isNullOrEmpty()){
            Toast.makeText(this,"Cidade é obrigatória!", Toast.LENGTH_LONG).show()
        }
        else if (!isEmailValid(email)) {
            //Toast.makeText(this, "Email inválido!", Toast.LENGTH_LONG).show();
            showSnackFeedback("Email Inválido1", false, view.btn_salvar)
        }
        else if (CPFUtil.myValidateCPF(cpf)){

            // Salva no firebase Database RealTime
            var myContato = Pessoa(nome, senha, confirmaSenha, celular, email, cpf, cidade)
            var dataBaseRef = FirebaseDatabase.getInstance().getReference()
            var contatoRef = dataBaseRef.child("usuarios")
            contatoRef.child("005").setValue(myContato)

            if(contatos.size > 0 || contatos != null){
                // Carrega o arquivo txt
                contatos = load(FILE_NAME)
            }

            var newContatos = contatos.plus(myContato)
            openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { fos->
                fos?.bufferedWriter().use { writer ->
                    newContatos.forEach { contato ->
                        writer?.appendln("#\n$contato")
                    }
                }
            }

            clear()
            showSnackFeedback("CPF válido", true, view.btn_salvar)
            Toast.makeText(this,"Contato salvo com sucesso!", Toast.LENGTH_LONG).show()
        }
        else {
            showSnackFeedback("CPF Inválido", false, view)
        }
    }

    fun clear(){
            nome = edtxt_nome.setText("").toString()
            senha = edtxt_senha.setText("").toString()
            confirmaSenha = edtxt_confirmaSenha.setText("").toString()
            celular = edtxt_celular.setText("").toString()
            email = edtxt_email.setText("").toString()
            cpf = edtxt_cpf.setText("").toString()
            cidade = edtxt_cidade.setText("").toString()
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
                        val senha = lines[index++]
                        val confirmaSenha = lines[index++]
                        val celular = lines[index++]
                        val email = lines[index++]
                        val cpf = lines[index++]
                        val cidade = lines[index++]
                        myContatos.add(Pessoa(nome, senha, confirmaSenha, celular, email, cpf, cidade))
                    } else {
                        Toast.makeText(this,"Erro Aplicativo!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return  myContatos
    }

    fun showSnackFeedback(message : String, isValid : Boolean, view : View){
        val snackbar : Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        var view : View = snackbar.view
        if (isValid)
            view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        else
            view.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

        snackbar.show()
    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
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

}
