package com.example.agenda

class Pessoa (val nome: String,
              val telefone: String,
              val email: String,
              val cidade: String){

    override fun toString(): String {
        return "$nome\n$telefone\n$email\n$cidade"
    }
}