package com.controleprojetos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ManyToMany(mappedBy = "funcionarios", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Projeto> projetos = new ArrayList<>();

    public Funcionario() {
    }

    public Funcionario(String nome) {
        this.nome = nome;
    }

    public Funcionario(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Funcionario(Integer id, String nome, Setor setor) {
        this.id = id;
        this.nome = nome;
        this.setor = setor;
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

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public List<Projeto> getProjetos() {
        return projetos;
    }

    public void setProjetos(List<Projeto> projetos) {
        this.projetos = projetos;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", setor=" + (setor != null ? setor.getNome() : "null") +
                '}';
    }
}
