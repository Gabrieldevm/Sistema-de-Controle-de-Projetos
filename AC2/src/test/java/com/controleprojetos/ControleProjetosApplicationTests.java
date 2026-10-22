package com.controleprojetos;

import com.controleprojetos.dto.DadosProjetoDTO;
import com.controleprojetos.dto.FuncionarioDTO;
import com.controleprojetos.dto.ProjetoDTO;
import com.controleprojetos.exception.RegraNegocioException;
import com.controleprojetos.model.Projeto;
import com.controleprojetos.model.Setor;
import com.controleprojetos.repository.FuncionarioRepository;
import com.controleprojetos.repository.ProjetoRepository;
import com.controleprojetos.repository.SetorRepository;
import com.controleprojetos.service.FuncionarioService;
import com.controleprojetos.service.ProjetoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ControleProjetosApplicationTests {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private SetorRepository setorRepository;

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private FuncionarioService funcionarioService;

    @Test
    @DisplayName("a. Teste da consulta personalizada findByIdWithFuncionarios em ProjetoRepository")
    void testeConsultaProjetoComFuncionarios() {
        Optional<Projeto> projetoOpt = projetoRepository.findByIdWithFuncionarios(1);
        assertTrue(projetoOpt.isPresent(), "O projeto 1 deve ser encontrado");
        Projeto projeto = projetoOpt.get();
        assertNotNull(projeto.getFuncionarios(), "A lista de funcionários vinculados não deve ser nula");
        assertFalse(projeto.getFuncionarios().isEmpty(), "O projeto 1 deve possuir funcionários vinculados");
    }

    @Test
    @DisplayName("b. Teste da consulta personalizada findByDataInicioBetween em ProjetoRepository")
    void testeConsultaProjetosPorIntervaloDataInicio() {
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now().plusDays(30);

        List<Projeto> projetos = projetoRepository.findByDataInicioBetween(inicio, fim);
        assertNotNull(projetos);
        assertFalse(projetos.isEmpty(), "Deve retornar projetos iniciados no intervalo");

        for (Projeto p : projetos) {
            assertTrue(!p.getDataInicio().isBefore(inicio) && !p.getDataInicio().isAfter(fim),
                    "Data de início deve estar dentro do intervalo");
        }
    }

    @Test
    @DisplayName("c. Teste da consulta personalizada findProjetosByFuncionarioId em FuncionarioRepository")
    void testeConsultaProjetosPorFuncionarioId() {
        List<Projeto> projetos = funcionarioRepository.findProjetosByFuncionarioId(1);
        assertNotNull(projetos);
        assertFalse(projetos.isEmpty(), "Funcionário 1 deve ter projetos vinculados");
    }

    @Test
    @DisplayName("d. Teste da consulta personalizada findAllWithFuncionarios em SetorRepository")
    void testeConsultaSetoresComFuncionarios() {
        List<Setor> setores = setorRepository.findAllWithFuncionarios();
        assertNotNull(setores);
        assertFalse(setores.isEmpty(), "Deve retornar lista de setores");
        boolean possuiFuncionarios = setores.stream().anyMatch(s -> !s.getFuncionarios().isEmpty());
        assertTrue(possuiFuncionarios, "Pelo menos um setor deve conter funcionários vinculados");
    }

    @Test
    @DisplayName("Teste de vinculação de funcionário ao projeto e validação de duplicidade")
    void testeVincularFuncionario() {
        FuncionarioDTO fDto = new FuncionarioDTO();
        fDto.setNome("Funcionário Teste");
        funcionarioService.adicionar(fDto);
        List<FuncionarioDTO> todos = funcionarioService.listarTodos();
        FuncionarioDTO novoFunc = todos.stream()
                .filter(f -> f.getNome().equals("Funcionário Teste"))
                .findFirst()
                .orElseThrow();

        ProjetoDTO pDto = new ProjetoDTO();
        pDto.setDescricao("Projeto de Teste Vinculação");
        pDto.setDataInicio(LocalDate.now());
        pDto.setDataFim(LocalDate.now().plusMonths(1));
        projetoService.adicionar(pDto);

        List<DadosProjetoDTO> todosProjetos = projetoService.listarTodos();
        DadosProjetoDTO novoProj = todosProjetos.stream()
                .filter(p -> p.getDescricao().equals("Projeto de Teste Vinculação"))
                .findFirst()
                .orElseThrow();

        projetoService.vincularFuncionario(novoProj.getId(), novoFunc.getId());

        DadosProjetoDTO projetoComFunc = projetoService.buscarProjetoPorId(novoProj.getId());
        boolean vinculado = projetoComFunc.getFuncionarios().stream()
                .anyMatch(f -> f.getId().equals(novoFunc.getId()));
        assertTrue(vinculado, "O funcionário deve estar na lista de vinculados do projeto");

        assertThrows(RegraNegocioException.class, () -> {
            projetoService.vincularFuncionario(novoProj.getId(), novoFunc.getId());
        }, "Deve lançar exceção ao tentar vincular funcionário já associado ao projeto");
    }

    @Test
    @DisplayName("Validação de datas no ProjetoService")
    void testeValidacaoDatasProjeto() {
        ProjetoDTO pDto = new ProjetoDTO();
        pDto.setDescricao("Projeto Data Inválida");
        pDto.setDataInicio(LocalDate.now());
        pDto.setDataFim(LocalDate.now().minusDays(1));

        assertThrows(RegraNegocioException.class, () -> {
            projetoService.adicionar(pDto);
        }, "Data final anterior à data inicial deve ser rejeitada");
    }
}
