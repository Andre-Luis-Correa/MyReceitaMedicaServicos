package unioeste.geral.receitamedica.service;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.bo.medicamento.Medicamento;
import unioeste.geral.receitamedica.col.DiagnosticoCidCOL;
import unioeste.geral.receitamedica.col.MedicamentoCOL;
import unioeste.geral.receitamedica.dao.DiagnosticoCidDAO;
import unioeste.geral.receitamedica.dao.MedicamentoDAO;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCMedicamentoServicos {

    private MedicamentoCOL medicamentoCOL;
    private MedicamentoDAO medicamentoDAO;

    public UCMedicamentoServicos() {
        this.medicamentoCOL = new MedicamentoCOL();
        this.medicamentoDAO = new MedicamentoDAO();
    }

    public Medicamento cadastrarMedicamento(Medicamento medicamento) throws Exception {
        if(!medicamentoCOL.medicamentoValido(medicamento)) throw new ReceitaMedicaException("Medicamento inválido.");

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            Medicamento medicamentoCadastrado;

            try {
                medicamentoCadastrado = medicamentoDAO.inserirMedicamento(medicamento, conexao);
                conexao.commit();

            } catch (Exception e) {
                conexao.rollback();
                System.out.println(e.getMessage());
                throw new ReceitaMedicaException("Erro ao cadastrar Medicamento.");
            }

            return medicamentoCadastrado;
        }
    }

    public List<Medicamento> obterListaDeMedicamentos() throws Exception {
        List<Medicamento> medicamentos = new ArrayList<>();

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            try {
                medicamentos = medicamentoDAO.selecionarTodosMedicamento(conexao);
                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
            }
        }

        return medicamentos;
    }

    public static void main(String[] args) throws Exception {
        UCMedicamentoServicos ucMedicamentoServicos = new UCMedicamentoServicos();

        Medicamento medicamento = new Medicamento();
        medicamento.setNome("Dipirona");

        Medicamento medicamentoCadastrado = ucMedicamentoServicos.cadastrarMedicamento(medicamento);

        if(medicamentoCadastrado != null) {
            System.out.println("id: " + medicamentoCadastrado.getId());
            System.out.println("nome: " + medicamentoCadastrado.getNome() + "\n");
        } else {
            System.out.println("NULO\n");
        }

        List<Medicamento> medicamentos = ucMedicamentoServicos.obterListaDeMedicamentos();

        for(Medicamento medicamento1 : medicamentos) {
            System.out.println("id: " + medicamento1.getId());
            System.out.println("nome: " + medicamento1.getNome() + "\n");
        }
    }
}
