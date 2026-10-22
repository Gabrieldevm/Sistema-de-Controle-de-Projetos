package com.controleprojetos.dto;

import jakarta.validation.constraints.NotBlank;

public class FuncionarioDTO {

    private Integer id;

    @NotBlank(message = "O nome do funcionário é obrigatório")
    private String nome;

    private Integer idSetor;

    private String nomeSetor;

    public FuncionarioDTO() {
    }

    public FuncionarioDTO(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public FuncionarioDTO(Integer id, String nome, Integer idSetor, String nomeSetor) {
        this.id = id;
        this.nome = nome;
        this.idSetor = idSetor;
        this.nomeSetor = nomeSetor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdSetor() {
        return idSetor;
    }

    public void setIdSetor(Integer idSetor) {
        this.idSetor = idSetor;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }
}
