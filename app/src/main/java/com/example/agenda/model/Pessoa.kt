package com.example.agenda.model

class Pessoa (var nome: String,
              var senha: String,
              var confirmaSenha: String,
              var celular: String,
              var email: String,
              var cpf: String,
              var cidade: String){

    constructor(): this("", "", "", "", "", "", "")

    override fun toString(): String {
        return "$nome\n$senha\n$confirmaSenha\n$celular\n$email\n$cpf\n$cidade"
    }
}