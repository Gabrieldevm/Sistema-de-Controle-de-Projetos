package com.controleprojetos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("ProjetoController - Adicionar projeto válido deve retornar 201 Created")
    void testeAdicionarProjetoValido() throws Exception {
        Map<String, Object> projeto = new HashMap<>();
        projeto.put("descricao", "Sistema de Gestão Financeira");
        projeto.put("dataInicio", LocalDate.now().toString());
        projeto.put("dataFim", LocalDate.now().plusMonths(4).toString());

        mockMvc.perform(post("/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projeto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("ProjetoController - Adicionar projeto inválido deve retornar 400 Bad Request com JSON de erro")
    void testeAdicionarProjetoInvalido() throws Exception {
        Map<String, Object> projeto = new HashMap<>();
        projeto.put("descricao", "");
        projeto.put("dataInicio", LocalDate.now().toString());

        mockMvc.perform(post("/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projeto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("ProjetoController - Buscar projeto por ID deve retornar DadosProjetoDTO com funcionários")
    void testeBuscarProjetoPorId() throws Exception {
        mockMvc.perform(get("/projetos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Modernização do Portal Corporativo"))
                .andExpect(jsonPath("$.funcionarios", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.funcionarios[0].nome").isNotEmpty());
    }

    @Test
    @DisplayName("ProjetoController - Buscar projeto inexistente deve retornar 404 Not Found")
    void testeBuscarProjetoInexistente() throws Exception {
        mockMvc.perform(get("/projetos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Projeto não encontrado")));
    }

    @Test
    @DisplayName("ProjetoController - Vincular funcionário ao projeto")
    void testeVincularFuncionarioEndpoint() throws Exception {
        mockMvc.perform(post("/projetos/1/funcionarios/3"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/projetos/1/funcionarios/3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("já está vinculado")));
    }

    @Test
    @DisplayName("FuncionarioController - Adicionar funcionário e buscar seus projetos")
    void testeFuncionarioController() throws Exception {
        Map<String, Object> func = new HashMap<>();
        func.put("nome", "Novo Engenheiro");
        func.put("idSetor", 1);

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(func)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/funcionarios/1/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Iterable.class)));
    }

    @Test
    @DisplayName("SetorController - Adicionar setor e buscar por ID com funcionários")
    void testeSetorController() throws Exception {
        Map<String, Object> setor = new HashMap<>();
        setor.put("nome", "Qualidade & Testes");

        mockMvc.perform(post("/setores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(setor)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/setores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Tecnologia da Informação"))
                .andExpect(jsonPath("$.funcionarios").isArray());
    }
}
