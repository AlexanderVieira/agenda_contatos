package com.example.agenda.ui

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
import com.example.agenda.R
import com.example.agenda.model.Pessoa
import com.example.agenda.util.CPFUtil
import com.example.agenda.util.Mascara
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

private const val FILE_NAME = "backup.txt"
private const val DELITER = "#"
private const val WRITE_REQUEST_CODE = 1
const val TAG_MAIN: String = "MainActivity"
const val EXTRA_EMAIL_USUARIO = "emailUsuario"
const val EXTRA_CAMPO_EMAIL = "campoEmail"
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
    lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var dataBaseRef: DatabaseReference
    lateinit var contatoRef: DatabaseReference
    lateinit var contato: Pessoa
    private var contatos: MutableList<Pessoa> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().getReference()
        contatoRef = dataBaseRef.child("usuarios")

        MobileAds.initialize(this, AD_APP_UNIT_ID)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = AD_UNIT_ID
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        edtxt_cpf.addTextChangedListener(Mascara.mask("###.###.###-##", edtxt_cpf))
        setListeners()
    }

    /*override fun onStart() {
        super.onStart()
        var currentUser = mAuth.currentUser
        updateUI(currentUser)
    }*/

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
        btn_cancelar.setOnClickListener {

            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.i(TAG_MAIN, "The interstitial wasn't loaded yet.")
            }

            val resultIntent = Intent(this@MainActivity, LoginActivity::class.java)
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
            showSnackFeedback("Email Inválido!", false, view.btn_salvar)
        }
        else if (CPFUtil.myValidateCPF(cpf)){

            mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i(TAG_MAIN, "createUserWithEmail:success")
                        user = mAuth.currentUser!!
                        updateUI(user)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i(TAG_MAIN, "createUserWithEmail:failure", task.exception)
                        /*Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()*/
                        updateUI(null)
                    }
                    // ...
                }

            contato = Pessoa(nome, senha, confirmaSenha, celular, email, cpf, cidade)

            user = mAuth.currentUser!!
            if (user != null){
                contatoRef.child(user.uid).setValue(contato)

                if(contatos.size > 0 || contatos != null){
                    // Carrega o arquivo txt
                    contatos = load(FILE_NAME)
                }

                var newContatos = contatos.plus(contato)
                openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { fos->
                    fos?.bufferedWriter().use { writer ->
                        newContatos.forEach { contato ->
                            writer?.appendln("#\n$contato")
                        }
                    }
                }

                /*var resultIntentLogin = Intent(this@MainActivity, LoginActivity::class.java )
                resultIntentLogin.putExtra(EXTRA_EMAIL_USUARIO,  contato.email)
                startActivity(resultIntentLogin)*/

                //clear()
                //showSnackFeedback("CPF válido", true, view.btn_salvar)
                //Toast.makeText(this,"Contato salvo com sucesso!", Toast.LENGTH_LONG).show()
            }

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
        openFileInput(FILE_NAME).use { fis->
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
                        myContatos.add(
                            Pessoa(
                                nome,
                                senha,
                                confirmaSenha,
                                celular,
                                email,
                                cpf,
                                cidade
                            )
                        )
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

            Snackbar.make(cl_root_main_activity, "Permitir ao Aplicativo Agenda acesso aos contatos.", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK") {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_CONTACTS),
                        WRITE_REQUEST_CODE
                    )
                }.show()
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null){
            clear()
            showSnackbar(btn_salvar, "Olá " + currentUser.email!!)
            showSnackFeedback("CPF válido", true, btn_salvar)
            var resultIntentLogin = Intent(this@MainActivity, LoginActivity::class.java )
            resultIntentLogin.putExtra(EXTRA_EMAIL_USUARIO,  contato.email)
            startActivity(resultIntentLogin)
            finish()

            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.i(TAG_MAIN, "The interstitial wasn't loaded yet.")
            }

        }
        else{
            showSnackbar(btn_salvar, "Olá, cadastre-se ou insira suas credenciais.")
        }
    }

    private fun showSnackbar(view: View, msg: String) {
        Snackbar.make( view, msg, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun showToast(context: Context, msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

}
