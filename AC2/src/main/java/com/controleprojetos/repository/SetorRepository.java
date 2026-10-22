package com.controleprojetos.repository;

import com.controleprojetos.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SetorRepository extends JpaRepository<Setor, Integer> {

    @Query("SELECT DISTINCT s FROM Setor s LEFT JOIN FETCH s.funcionarios")
    List<Setor> findAllWithFuncionarios();

    @Query("SELECT s FROM Setor s LEFT JOIN FETCH s.funcionarios WHERE s.id = :id")
    Optional<Setor> findByIdWithFuncionarios(@Param("id") Integer id);
}
