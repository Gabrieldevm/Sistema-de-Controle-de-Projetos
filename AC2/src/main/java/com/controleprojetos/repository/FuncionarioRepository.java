package com.controleprojetos.repository;

import com.controleprojetos.model.Funcionario;
import com.controleprojetos.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    @Query("SELECT p FROM Projeto p JOIN p.funcionarios f WHERE f.id = :idFuncionario")
    List<Projeto> findProjetosByFuncionarioId(@Param("idFuncionario") Integer idFuncionario);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.setor WHERE f.id = :id")
    Optional<Funcionario> findByIdWithSetor(@Param("id") Integer id);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.setor")
    List<Funcionario> findAllWithSetor();
}
