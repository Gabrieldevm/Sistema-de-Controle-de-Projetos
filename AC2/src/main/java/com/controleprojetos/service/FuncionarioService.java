package com.controleprojetos.service;

import com.controleprojetos.dto.DadosProjetoDTO;
import com.controleprojetos.dto.FuncionarioDTO;
import com.controleprojetos.exception.RegraNegocioException;
import com.controleprojetos.exception.ResourceNotFoundException;
import com.controleprojetos.model.Funcionario;
import com.controleprojetos.model.Projeto;
import com.controleprojetos.model.Setor;
import com.controleprojetos.repository.FuncionarioRepository;
import com.controleprojetos.repository.SetorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final SetorRepository setorRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, SetorRepository setorRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.setorRepository = setorRepository;
    }

    @Transactional
    public void adicionar(FuncionarioDTO funcionarioDTO) {
        if (funcionarioDTO == null) {
            throw new RegraNegocioException("Dados do funcionário não podem ser nulos.");
        }
        if (funcionarioDTO.getNome() == null || funcionarioDTO.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("O nome do funcionário é obrigatório.");
        }

        Funcionario funcionario = new Funcionario(funcionarioDTO.getNome().trim());

        if (funcionarioDTO.getIdSetor() != null) {
            Setor setor = setorRepository.findById(funcionarioDTO.getIdSetor())
                    .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado com o ID: " + funcionarioDTO.getIdSetor()));
            funcionario.setSetor(setor);
        }

        funcionarioRepository.save(funcionario);
    }

    @Transactional(readOnly = true)
    public List<DadosProjetoDTO> buscarProjetos(Integer idFuncionario) {
        if (idFuncionario == null) {
            throw new RegraNegocioException("O ID do funcionário deve ser informado.");
        }

        if (!funcionarioRepository.existsById(idFuncionario)) {
            throw new ResourceNotFoundException("Funcionário não encontrado com o ID: " + idFuncionario);
        }

        List<Projeto> projetos = funcionarioRepository.findProjetosByFuncionarioId(idFuncionario);

        return projetos.stream()
                .map(this::converterParaDadosProjetoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        List<Funcionario> funcionarios = funcionarioRepository.findAllWithSetor();
        return funcionarios.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO buscarPorId(Integer id) {
        Funcionario funcionario = funcionarioRepository.findByIdWithSetor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com o ID: " + id));
        return converterParaDTO(funcionario);
    }

    @Transactional(readOnly = true)
    public Funcionario buscarEntidadePorId(Integer id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com o ID: " + id));
    }

    public FuncionarioDTO converterParaDTO(Funcionario funcionario) {
        return new FuncionarioDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getSetor() != null ? funcionario.getSetor().getId() : null,
                funcionario.getSetor() != null ? funcionario.getSetor().getNome() : null
        );
    }

    private DadosProjetoDTO converterParaDadosProjetoDTO(Projeto projeto) {
        List<FuncionarioDTO> funcionariosDTO = projeto.getFuncionarios() != null
                ? projeto.getFuncionarios().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList())
                : List.of();

        return new DadosProjetoDTO(
                projeto.getId(),
                projeto.getDescricao(),
                projeto.getDataInicio(),
                projeto.getDataFim(),
                funcionariosDTO
        );
    }
}
