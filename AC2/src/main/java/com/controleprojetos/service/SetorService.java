package com.controleprojetos.service;

import com.controleprojetos.dto.DadosSetorDTO;
import com.controleprojetos.dto.FuncionarioDTO;
import com.controleprojetos.dto.SetorDTO;
import com.controleprojetos.exception.RegraNegocioException;
import com.controleprojetos.exception.ResourceNotFoundException;
import com.controleprojetos.model.Setor;
import com.controleprojetos.repository.SetorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetorService {

    private final SetorRepository setorRepository;

    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    @Transactional
    public void adicionar(SetorDTO setorDTO) {
        if (setorDTO == null) {
            throw new RegraNegocioException("Dados do setor não podem ser nulos.");
        }
        if (setorDTO.getNome() == null || setorDTO.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("O nome do setor é obrigatório.");
        }

        Setor setor = new Setor(setorDTO.getNome().trim());
        setorRepository.save(setor);
    }

    @Transactional(readOnly = true)
    public DadosSetorDTO buscarSetorPorId(Integer idSetor) {
        if (idSetor == null) {
            throw new RegraNegocioException("O ID do setor deve ser informado.");
        }

        Setor setor = setorRepository.findByIdWithFuncionarios(idSetor)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado com o ID: " + idSetor));

        return converterParaDadosSetorDTO(setor);
    }

    @Transactional(readOnly = true)
    public List<DadosSetorDTO> listarTodos() {
        List<Setor> setores = setorRepository.findAllWithFuncionarios();
        return setores.stream()
                .map(this::converterParaDadosSetorDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Setor buscarEntidadePorId(Integer idSetor) {
        return setorRepository.findById(idSetor)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado com o ID: " + idSetor));
    }

    private DadosSetorDTO converterParaDadosSetorDTO(Setor setor) {
        List<FuncionarioDTO> funcionariosDTO = setor.getFuncionarios() != null
                ? setor.getFuncionarios().stream()
                .map(f -> new FuncionarioDTO(f.getId(), f.getNome(), setor.getId(), setor.getNome()))
                .collect(Collectors.toList())
                : List.of();

        return new DadosSetorDTO(setor.getId(), setor.getNome(), funcionariosDTO);
    }
}
