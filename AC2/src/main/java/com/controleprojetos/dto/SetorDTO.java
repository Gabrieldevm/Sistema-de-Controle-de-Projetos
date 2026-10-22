package com.controleprojetos.dto;

import jakarta.validation.constraints.NotBlank;

public class SetorDTO {

    private Integer id;

    @NotBlank(message = "O nome do setor é obrigatório")
    private String nome;

    public SetorDTO() {
    }

    public SetorDTO(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
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
}
