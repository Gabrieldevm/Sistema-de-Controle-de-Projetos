package com.controleprojetos.controller;

import com.controleprojetos.dto.DadosProjetoDTO;
import com.controleprojetos.dto.ProjetoDTO;
import com.controleprojetos.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/projetos")
@CrossOrigin(origins = "*")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void adicionar(@RequestBody @Valid ProjetoDTO projeto) {
        projetoService.adicionar(projeto);
    }

    @GetMapping("/{id}")
    public DadosProjetoDTO buscarProjetoPorId(@PathVariable("id") Integer id) {
        return projetoService.buscarProjetoPorId(id);
    }

    @PostMapping("/{idProjeto}/funcionarios/{idFuncionario}")
    @ResponseStatus(HttpStatus.OK)
    public void vincularFuncionario(@PathVariable("idProjeto") Integer idProjeto,
                                   @PathVariable("idFuncionario") Integer idFuncionario) {
        projetoService.vincularFuncionario(idProjeto, idFuncionario);
    }

    @GetMapping
    public List<DadosProjetoDTO> listarTodos() {
        return projetoService.listarTodos();
    }

    @GetMapping("/intervalo")
    public List<DadosProjetoDTO> buscarPorIntervalo(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return projetoService.buscarProjetosPorIntervalo(dataInicio, dataFim);
    }
}
