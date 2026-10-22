package com.controleprojetos.controller;

import com.controleprojetos.dto.DadosSetorDTO;
import com.controleprojetos.dto.SetorDTO;
import com.controleprojetos.service.SetorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setores")
@CrossOrigin(origins = "*")
public class SetorController {

    private final SetorService setorService;

    public SetorController(SetorService setorService) {
        this.setorService = setorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void adicionar(@RequestBody @Valid SetorDTO setor) {
        setorService.adicionar(setor);
    }

    @GetMapping("/{idSetor}")
    public DadosSetorDTO buscarSetorPorId(@PathVariable("idSetor") Integer idSetor) {
        return setorService.buscarSetorPorId(idSetor);
    }

    @GetMapping
    public List<DadosSetorDTO> listarTodos() {
        return setorService.listarTodos();
    }
}
