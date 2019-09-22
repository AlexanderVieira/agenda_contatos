package com.example.agenda.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;


public class Tarefa implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("descricao")
    @Expose
    private String descricao;
    @SerializedName("imagem")
    @Expose
    private String imagem;
    private final static long serialVersionUID = 3392172130603884887L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Tarefa() {
    }

    /**
     *
     * @param id
     * @param imagem
     * @param descricao
     */
    public Tarefa(String id, String descricao, String imagem) {
        super();
        this.id = id;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("descricao", descricao)
                .append("imagem", imagem).toString();
    }

}