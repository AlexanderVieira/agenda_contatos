package com.example.agenda.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda.R
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

const val TAG: String = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        var emailRecuperado = intent.getStringExtra(EXTRA_EMAIL_USUARIO)
        edtxt_email_login.setText(emailRecuperado)

        callbackManager = CallbackManager.Factory.create()
        login_button.setReadPermissions("email", "public_profile")

        setListeners()
    }

   /* override fun onStart() {
        super.onStart()
        user = mAuth.currentUser!!
        updateUI(user)
    }*/

    private fun setListeners(){

        btn_login.setOnClickListener {
            var email = edtxt_email_login.text.toString()
            var senha = edtxt_senha_login.text.toString()
            Log.i(TAG, email)
            Log.i(TAG, senha)

            mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "signInWithEmail:success")
                        user = mAuth.currentUser!!
                        updateUI(user)
                    } else {
                        Log.i(TAG, "signInWithEmail:failure", task.exception)
                        //showToast(loginActivity, "Usuário e/ou senha inválidos!")
                        updateUI(null)
                    }
                }
            //
        }

        btn_cadastrar_login.setOnClickListener {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        login_button.setOnClickListener{
            login_button.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    showToast(this@LoginActivity, "Usuário cancelou!")
                }

                override fun onError(error: FacebookException?) {
                    showToast(this@LoginActivity, error!!.message.toString())
                }
            })
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    user = mAuth.currentUser!!
                    updateUI(user)

                } else {

                    // If sign in fails, display a message to the user.
                    //updateUI(null)
                    showToast(baseContext, "Autenticação com o Facebook falhou!")
                    /*Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()*/
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            showSnackbar(btn_login, "Olá " + currentUser.email!!)
            startActivity(Intent(this, TarefasActivity::class.java))
        }
        else{
            showSnackbar(btn_login,"Olá, cadastre-se ou insira suas credenciais.")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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