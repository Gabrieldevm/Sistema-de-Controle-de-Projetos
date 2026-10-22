package com.controleprojetos.service;

import com.controleprojetos.dto.DadosProjetoDTO;
import com.controleprojetos.dto.FuncionarioDTO;
import com.controleprojetos.dto.ProjetoDTO;
import com.controleprojetos.exception.RegraNegocioException;
import com.controleprojetos.exception.ResourceNotFoundException;
import com.controleprojetos.model.Funcionario;
import com.controleprojetos.model.Projeto;
import com.controleprojetos.repository.FuncionarioRepository;
import com.controleprojetos.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioService funcionarioService;

    public ProjetoService(ProjetoRepository projetoRepository,
                          FuncionarioRepository funcionarioRepository,
                          FuncionarioService funcionarioService) {
        this.projetoRepository = projetoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioService = funcionarioService;
    }

    @Transactional
    public void adicionar(ProjetoDTO projetoDTO) {
        if (projetoDTO == null) {
            throw new RegraNegocioException("Dados do projeto não podem ser nulos.");
        }
        if (projetoDTO.getDescricao() == null || projetoDTO.getDescricao().trim().isEmpty()) {
            throw new RegraNegocioException("A descrição do projeto é obrigatória.");
        }
        if (projetoDTO.getDataInicio() == null) {
            throw new RegraNegocioException("A data de início do projeto é obrigatória.");
        }
        if (projetoDTO.getDataFim() != null && projetoDTO.getDataFim().isBefore(projetoDTO.getDataInicio())) {
            throw new RegraNegocioException("A data de fim não pode ser anterior à data de início do projeto.");
        }

        Projeto projeto = new Projeto(
                projetoDTO.getDescricao().trim(),
                projetoDTO.getDataInicio(),
                projetoDTO.getDataFim()
        );

        projetoRepository.save(projeto);
    }

    @Transactional(readOnly = true)
    public DadosProjetoDTO buscarProjetoPorId(Integer id) {
        if (id == null) {
            throw new RegraNegocioException("O ID do projeto deve ser informado.");
        }

        Projeto projeto = projetoRepository.findByIdWithFuncionarios(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com o ID: " + id));

        return converterParaDadosProjetoDTO(projeto);
    }

    @Transactional
    public void vincularFuncionario(Integer idProjeto, Integer idFuncionario) {
        if (idProjeto == null || idFuncionario == null) {
            throw new RegraNegocioException("IDs do projeto e do funcionário são obrigatórios.");
        }

        Projeto projeto = projetoRepository.findByIdWithFuncionarios(idProjeto)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com o ID: " + idProjeto));

        Funcionario funcionario = funcionarioRepository.findById(idFuncionario)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado com o ID: " + idFuncionario));

        boolean jaVinculado = projeto.getFuncionarios().stream()
                .anyMatch(f -> f.getId().equals(idFuncionario));

        if (jaVinculado) {
            throw new RegraNegocioException("O funcionário '" + funcionario.getNome() + "' já está vinculado ao projeto '" + projeto.getDescricao() + "'.");
        }

        projeto.adicionarFuncionario(funcionario);
        projetoRepository.save(projeto);
    }

    @Transactional(readOnly = true)
    public List<DadosProjetoDTO> buscarProjetosPorIntervalo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new RegraNegocioException("As datas de início e fim devem ser informadas para a consulta.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new RegraNegocioException("A data final não pode ser anterior à data inicial do intervalo.");
        }

        List<Projeto> projetos = projetoRepository.findByDataInicioBetween(dataInicio, dataFim);

        return projetos.stream()
                .map(this::converterParaDadosProjetoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DadosProjetoDTO> listarTodos() {
        List<Projeto> projetos = projetoRepository.findAllWithFuncionarios();
        return projetos.stream()
                .map(this::converterParaDadosProjetoDTO)
                .collect(Collectors.toList());
    }

    private DadosProjetoDTO converterParaDadosProjetoDTO(Projeto projeto) {
        List<FuncionarioDTO> funcionariosDTO = projeto.getFuncionarios() != null
                ? projeto.getFuncionarios().stream()
                .map(funcionarioService::converterParaDTO)
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
