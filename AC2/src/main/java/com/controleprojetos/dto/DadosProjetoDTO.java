package com.controleprojetos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DadosProjetoDTO {

    private Integer id;
    private String descricao;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;

    private List<FuncionarioDTO> funcionarios = new ArrayList<>();

    public DadosProjetoDTO() {
    }

    public DadosProjetoDTO(Integer id, String descricao, LocalDate dataInicio, LocalDate dataFim) {
        this.id = id;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public DadosProjetoDTO(Integer id, String descricao, LocalDate dataInicio, LocalDate dataFim, List<FuncionarioDTO> funcionarios) {
        this.id = id;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.funcionarios = funcionarios != null ? funcionarios : new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public List<FuncionarioDTO> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<FuncionarioDTO> funcionarios) {
        this.funcionarios = funcionarios;
    }
}
