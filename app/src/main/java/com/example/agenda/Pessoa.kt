package com.example.agenda

class Pessoa (val nome: String,
              val senha: String,
              val confirmaSenha: String,
              val celular: String,
              val email: String,
              val cpf: String,
              val cidade: String){

    constructor(): this("", "", "", "", "", "", "")

    override fun toString(): String {
        return "$nome\n$senha\n$confirmaSenha\n$celular\n$email\n$cpf\n$cidade"
    }
}