package unioeste.geral.receitamedica.service;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.endereco.bo.cidade.Cidade;
import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.col.DiagnosticoCidCOL;
import unioeste.geral.receitamedica.dao.DiagnosticoCidDAO;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UCDiagnosticoCIDServicos {

    private DiagnosticoCidCOL diagnosticoCidCOL;
    private DiagnosticoCidDAO diagnosticoCidDAO;

    public UCDiagnosticoCIDServicos() {
        this.diagnosticoCidCOL = new DiagnosticoCidCOL();
        this.diagnosticoCidDAO = new DiagnosticoCidDAO();
    }

    public DiagnosticoCID cadastrarDiagnosticoCID(DiagnosticoCID diagnosticoCID) throws Exception {
        if(!diagnosticoCidCOL.diagnosticoCIDValido(diagnosticoCID)) throw new ReceitaMedicaException("Diagnóstico CID inválido.");

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            if(diagnosticoCidDAO.selecionarDiagnosticoCIDPorCodigo(diagnosticoCID.getCodigo(), conexao) != null) {
                throw new ReceitaMedicaException("Diagnostico CID já cadastrado.");
            }

            DiagnosticoCID diagnosticoCIDCadastrado;

            try {
                diagnosticoCIDCadastrado = diagnosticoCidDAO.inserirDiagnosticoCID(diagnosticoCID, conexao);
                conexao.commit();

            } catch (Exception e) {
                conexao.rollback();
                System.out.println(e.getMessage());
                throw new ReceitaMedicaException("Erro ao cadastrar Diagnóstico CID.");
            }

            return diagnosticoCIDCadastrado;
        }
    }

    public List<DiagnosticoCID> obterListaDeDiagnosticosCID() throws Exception {
        List<DiagnosticoCID> diagnosticoCIDS = new ArrayList<>();

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            try {
                diagnosticoCIDS = diagnosticoCidDAO.selecionarTodosDiagnosticoCID(conexao);
                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
            }
        }

        return diagnosticoCIDS;
    }

    public static void main(String[] args) throws Exception {
        UCDiagnosticoCIDServicos ucDiagnosticoCIDServicos = new UCDiagnosticoCIDServicos();

        DiagnosticoCID diagnosticoCID = new DiagnosticoCID("E15.2", "Diabete");

        DiagnosticoCID diagnosticoCidCadastrado = ucDiagnosticoCIDServicos.cadastrarDiagnosticoCID(diagnosticoCID);

        if(diagnosticoCidCadastrado != null) {
            System.out.println("Código: " + diagnosticoCidCadastrado.getCodigo());
            System.out.println("Descrição: " + diagnosticoCidCadastrado.getDescricao() + "\n");
        } else {
            System.out.println("NULO\n");
        }

        List<DiagnosticoCID> diagnosticoCIDS = ucDiagnosticoCIDServicos.obterListaDeDiagnosticosCID();

        for(DiagnosticoCID diagnosticoCID1 : diagnosticoCIDS) {
            System.out.println("Código: " + diagnosticoCID1.getCodigo());
            System.out.println("Descrição: " + diagnosticoCID1.getDescricao() + "\n");
        }
    }
}
