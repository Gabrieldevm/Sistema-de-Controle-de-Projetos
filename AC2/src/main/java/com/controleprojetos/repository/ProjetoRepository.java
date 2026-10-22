package com.controleprojetos.repository;

import com.controleprojetos.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer> {

    @Query("SELECT p FROM Projeto p LEFT JOIN FETCH p.funcionarios WHERE p.id = :id")
    Optional<Projeto> findByIdWithFuncionarios(@Param("id") Integer id);

    @Query("SELECT p FROM Projeto p WHERE p.dataInicio BETWEEN :dataInicio AND :dataFim")
    List<Projeto> findByDataInicioBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);

    @Query("SELECT DISTINCT p FROM Projeto p LEFT JOIN FETCH p.funcionarios")
    List<Projeto> findAllWithFuncionarios();
}
