package com.example.agenda.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TarefaRequest implements Serializable
{

    @SerializedName("tarefa")
    @Expose
    private List<Tarefa> tarefa = new ArrayList<Tarefa>();
    private final static long serialVersionUID = 193243742633717497L;

    /**
     * No args constructor for use in serialization
     *
     */
    public TarefaRequest() {
    }

    /**
     *
     * @param tarefa
     */
    public TarefaRequest(List<Tarefa> tarefa) {
        super();
        this.tarefa = tarefa;
    }

    public List<Tarefa> getTarefa() {
        return tarefa;
    }

    public void setTarefa(List<Tarefa> tarefa) {
        this.tarefa = tarefa;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("tarefa", tarefa).toString();
    }

}