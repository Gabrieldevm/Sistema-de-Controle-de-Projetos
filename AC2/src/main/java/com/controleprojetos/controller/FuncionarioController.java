package com.controleprojetos.controller;

import com.controleprojetos.dto.DadosProjetoDTO;
import com.controleprojetos.dto.FuncionarioDTO;
import com.controleprojetos.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void adicionar(@RequestBody @Valid FuncionarioDTO funcionario) {
        funcionarioService.adicionar(funcionario);
    }

    @GetMapping("/{idFuncionario}/projetos")
    public List<DadosProjetoDTO> buscarProjetos(@PathVariable("idFuncionario") Integer idFuncionario) {
        return funcionarioService.buscarProjetos(idFuncionario);
    }

    @GetMapping
    public List<FuncionarioDTO> listarTodos() {
        return funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public FuncionarioDTO buscarPorId(@PathVariable("id") Integer id) {
        return funcionarioService.buscarPorId(id);
    }
}
