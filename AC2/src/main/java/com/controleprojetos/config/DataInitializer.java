package com.controleprojetos.config;

import com.controleprojetos.model.Funcionario;
import com.controleprojetos.model.Projeto;
import com.controleprojetos.model.Setor;
import com.controleprojetos.repository.FuncionarioRepository;
import com.controleprojetos.repository.ProjetoRepository;
import com.controleprojetos.repository.SetorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SetorRepository setorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProjetoRepository projetoRepository;

    public DataInitializer(SetorRepository setorRepository,
                           FuncionarioRepository funcionarioRepository,
                           ProjetoRepository projetoRepository) {
        this.setorRepository = setorRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.projetoRepository = projetoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Setor ti = new Setor("Tecnologia da Informação");
        Setor rh = new Setor("Recursos Humanos");
        Setor financeiro = new Setor("Financeiro & Controladoria");
        setorRepository.save(ti);
        setorRepository.save(rh);
        setorRepository.save(financeiro);

        Funcionario f1 = new Funcionario("Lucas Silva");
        f1.setSetor(ti);
        Funcionario f2 = new Funcionario("Mariana Costa");
        f2.setSetor(ti);
        Funcionario f3 = new Funcionario("Carlos Eduardo");
        f3.setSetor(rh);
        Funcionario f4 = new Funcionario("Beatriz Lima");
        f4.setSetor(financeiro);

        funcionarioRepository.save(f1);
        funcionarioRepository.save(f2);
        funcionarioRepository.save(f3);
        funcionarioRepository.save(f4);

        Projeto p1 = new Projeto(
                "Modernização do Portal Corporativo",
                LocalDate.now().minusDays(15),
                LocalDate.now().plusMonths(3)
        );
        p1.adicionarFuncionario(f1);
        p1.adicionarFuncionario(f2);
        projetoRepository.save(p1);

        Projeto p2 = new Projeto(
                "Implantação de Sistema ERP em Nuvem",
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6)
        );
        p2.adicionarFuncionario(f1);
        p2.adicionarFuncionario(f4);
        projetoRepository.save(p2);

        Projeto p3 = new Projeto(
                "Programa de Treinamento e Onboarding 2026",
                LocalDate.now().plusDays(10),
                LocalDate.now().plusMonths(2)
        );
        p3.adicionarFuncionario(f3);
        projetoRepository.save(p3);
    }
}
