package unioeste.geral.receitamedica.service;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.endereco.exception.EnderecoException;
import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.bo.medicamento.Medicamento;
import unioeste.geral.receitamedica.bo.medicamentoreceitamedica.MedicamentoReceitaMedica;
import unioeste.geral.receitamedica.bo.medico.Medico;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.bo.receitamedica.ReceitaMedica;
import unioeste.geral.receitamedica.col.*;
import unioeste.geral.receitamedica.dao.*;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UCReceitaMedicaServicos {

    private ReceitaMedicaCOL receitaMedicaCOL;
    private ReceitaMedicaDAO receitaMedicaDAO;
    private MedicoCOL medicoCOL;
    private MedicoDAO medicoDAO;
    private PacienteCOL pacienteCOL;
    private PacienteDAO pacienteDAO;
    private DiagnosticoCidCOL diagnosticoCidCOL;
    private DiagnosticoCidDAO diagnosticoCidDAO;
    private MedicamentoCOL medicamentoCOL;
    private MedicamentoDAO medicamentoDAO;
    private MedicamentoReceitaMedicaCOL medicamentoReceitaMedicaCOL;
    private MedicamentoReceitaMedicaDAO medicamentoReceitaMedicaDAO;

    public UCReceitaMedicaServicos() {
        this.receitaMedicaCOL = new ReceitaMedicaCOL();
        this.receitaMedicaDAO = new ReceitaMedicaDAO();
        this.medicoCOL = new MedicoCOL();
        this.medicoDAO = new MedicoDAO();
        this.pacienteCOL = new PacienteCOL();
        this.pacienteDAO = new PacienteDAO();
        this.diagnosticoCidCOL = new DiagnosticoCidCOL();
        this.diagnosticoCidDAO = new DiagnosticoCidDAO();
        this.medicamentoCOL = new MedicamentoCOL();
        this.medicamentoDAO = new MedicamentoDAO();
        this.medicamentoReceitaMedicaCOL = new MedicamentoReceitaMedicaCOL();
        this.medicamentoReceitaMedicaDAO = new MedicamentoReceitaMedicaDAO();
    }


    public ReceitaMedica cadastrarReceitaMedica(ReceitaMedica receitaMedica) throws Exception {
        if(!receitaMedicaCOL.receitaMedicaValida(receitaMedica)) throw new ReceitaMedicaException("Receita Médica inválida");

        try(Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            if(medicoDAO.selecionarMedicoPorId(receitaMedica.getMedico().getId(), conexao) == null) {
                throw new ReceitaMedicaException("Médico não existe com id " + receitaMedica.getMedico().getId());
            }
            if(pacienteDAO.selecionarPacientePorId(receitaMedica.getPaciente().getId(), conexao) == null) {
                throw new ReceitaMedicaException("Paciente não existe com id " + receitaMedica.getPaciente().getId());
            }
            if(diagnosticoCidDAO.selecionarDiagnosticoCIDPorCodigo(receitaMedica.getDiagnosticoCID().getCodigo(), conexao) == null) {
                throw new ReceitaMedicaException("Diagnóstico CID não existe com código " + receitaMedica.getDiagnosticoCID().getCodigo());
            }

            if(!medicamentoReceitaMedicaCOL.isMedicamentoReceitaMedicaValido(receitaMedica.getMedicamentoReceitaMedicas())) {
                throw new ReceitaMedicaException("itens da receita médica inválidos!");
            }

            for(MedicamentoReceitaMedica medicamentoReceitaMedica : receitaMedica.getMedicamentoReceitaMedicas()) {
                if(medicamentoDAO.selecionarMedicamentoPorId(medicamentoReceitaMedica.getMedicamento().getId(), conexao) == null) {
                    throw new ReceitaMedicaException("Medicamento não existe com id " + medicamentoReceitaMedica.getMedicamento().getId());
                }
                if(!medicamentoReceitaMedicaCOL.isMedicamentoReceitaMedicaValido(medicamentoReceitaMedica)) {
                    throw new ReceitaMedicaException("Item da receita médica inválido!");
                }
            }

            ReceitaMedica receitaMedicaCadastrada;

            try {
                receitaMedicaCadastrada = receitaMedicaDAO.inserirReceitaMedica(receitaMedica, conexao);
                if(receitaMedicaCadastrada != null) {
                    medicamentoReceitaMedicaDAO.inserirMedicamentoReceitaMedica(receitaMedicaCadastrada, receitaMedica.getMedicamentoReceitaMedicas(), conexao);
                }
                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
                System.out.println(e.getMessage());
                throw new ReceitaMedicaException("Erro ao cadastrar receita médica.");
            }

            return receitaMedicaCadastrada;
        }
    }

    public ReceitaMedica consultarReceitaMedicaPorNumero(Integer numero) throws Exception {
        if(!receitaMedicaCOL.numeroValido(numero)) throw new ReceitaMedicaException("Número da receita inválido.");

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            ReceitaMedica receitaMedica;

            try {
                receitaMedica = receitaMedicaDAO.selecionarReceitaMedicaPorNumero(numero, conexao);

                if(receitaMedica != null) {
                    receitaMedica.setMedicamentoReceitaMedicas(medicamentoReceitaMedicaDAO.obterMedicamentosPorReceita(receitaMedica.getNumero(), conexao));
                }
                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
                throw new EnderecoException("Não foi possível buscar a receita médica pelo número " + numero + ".");
            }

            return receitaMedica;
        }
    }

    public List<ReceitaMedica> obterListaDeReceitasMedicas() throws Exception {
        List<ReceitaMedica> receitaMedicas = new ArrayList<>();

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            try {
                receitaMedicas = receitaMedicaDAO.selecionarTodasReceitasMedicas(conexao);
                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
            }
        }

        return receitaMedicas;
    }

    public static void main(String[] args) throws Exception {
        UCReceitaMedicaServicos ucReceitaMedicaServicos = new UCReceitaMedicaServicos();

        Medico medico = new Medico();
        medico.setId(1L);

        Paciente paciente = new Paciente();
        paciente.setId(1L);

        DiagnosticoCID diagnosticoCID = new DiagnosticoCID();
        diagnosticoCID.setCodigo("E34.8");

        Medicamento medicamento = new Medicamento();
        medicamento.setId(1L);

        MedicamentoReceitaMedica medicamentoReceitaMedica = new MedicamentoReceitaMedica();
        medicamentoReceitaMedica.setDataInicio(LocalDate.now().plusDays(1));
        medicamentoReceitaMedica.setDataFim(LocalDate.now().plusDays(3));
        medicamentoReceitaMedica.setMedicamento(medicamento);
        medicamentoReceitaMedica.setPosologia("Uma vez ao dia no almoço.");

        List<MedicamentoReceitaMedica> medicamentoReceitaMedicas = new ArrayList<>();
        medicamentoReceitaMedicas.add(medicamentoReceitaMedica);

        ReceitaMedica receitaMedica = new ReceitaMedica();
        receitaMedica.setDataEmissao(LocalDate.now());
        receitaMedica.setMedico(medico);
        receitaMedica.setPaciente(paciente);
        receitaMedica.setDiagnosticoCID(diagnosticoCID);
        receitaMedica.setMedicamentoReceitaMedicas(medicamentoReceitaMedicas);

        receitaMedica.setDataEmissao(LocalDate.now());

        ReceitaMedica receitaMedicaCadastrada = ucReceitaMedicaServicos.consultarReceitaMedicaPorNumero(1);

        if(receitaMedicaCadastrada != null) {
            System.out.println("Receita médica: " + receitaMedicaCadastrada.getNumero());
            System.out.println("CID: " + receitaMedicaCadastrada.getDiagnosticoCID().getDescricao());
            System.out.println("Médico: " + receitaMedicaCadastrada.getMedico().getNome());
            System.out.println("Paciente: " + receitaMedicaCadastrada.getPaciente().getNome());
            System.out.println("Data emissão: " + receitaMedicaCadastrada.getDataEmissao());

            for(MedicamentoReceitaMedica medicamentoReceitaMedica1 : receitaMedicaCadastrada.getMedicamentoReceitaMedicas()) {
                System.out.println("Dt incio: " + medicamentoReceitaMedica1.getDataInicio());
                System.out.println("Dt fim: " + medicamentoReceitaMedica1.getDataFim());
                System.out.println("Medicamento: " + medicamentoReceitaMedica1.getMedicamento().getNome());
                System.out.println("Posologia: " + medicamentoReceitaMedica1.getPosologia());
            }

        } else {
            System.out.println("NULO\n");
        }

        System.out.println(ucReceitaMedicaServicos.obterListaDeReceitasMedicas());
    }
}
