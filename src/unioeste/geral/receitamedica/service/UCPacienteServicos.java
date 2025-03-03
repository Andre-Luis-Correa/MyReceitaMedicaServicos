package unioeste.geral.receitamedica.service;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.endereco.exception.EnderecoException;
import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.pessoa.bo.ddd.DDD;
import unioeste.geral.pessoa.bo.ddi.DDI;
import unioeste.geral.pessoa.bo.email.Email;
import unioeste.geral.pessoa.bo.sexo.Sexo;
import unioeste.geral.pessoa.bo.telefone.Telefone;
import unioeste.geral.pessoa.col.*;
import unioeste.geral.pessoa.dao.DDDDao;
import unioeste.geral.pessoa.dao.DDIDao;
import unioeste.geral.pessoa.dao.SexoDAO;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.col.PacienteCOL;
import unioeste.geral.receitamedica.dao.EmailPacienteDAO;
import unioeste.geral.receitamedica.dao.PacienteDAO;
import unioeste.geral.receitamedica.dao.TelefonePacienteDAO;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

import java.sql.Connection;
import java.util.ArrayList;

public class UCPacienteServicos {

    private PacienteDAO pacienteDAO;
    private PacienteCOL pacienteCOL;
    private EnderecoEspecificoCOL enderecoEspecificoCOL;
    private EnderecoDAO enderecoDAO;
    private TelefoneCOL telefoneCOL;
    private TelefonePacienteDAO telefonePacienteDAO;
    private DDDDao dddDao;
    private DDIDao ddiDao;
    private SexoCOL sexoCOL;
    private SexoDAO sexoDAO;
    private CPFCOL cpfCol;
    private EmailCOL emailCOL;
    private EmailPacienteDAO emailPacienteDAO;

    public UCPacienteServicos() {
        this.pacienteDAO = new PacienteDAO();
        this.pacienteCOL = new PacienteCOL();
        this.enderecoEspecificoCOL = new EnderecoEspecificoCOL();
        this.enderecoDAO = new EnderecoDAO();
        this.telefoneCOL = new TelefoneCOL();
        this.telefonePacienteDAO = new TelefonePacienteDAO();
        this.dddDao = new DDDDao();
        this.ddiDao = new DDIDao();
        this.sexoCOL = new SexoCOL();
        this.sexoDAO = new SexoDAO();
        this.cpfCol = new CPFCOL();
        this.emailCOL = new EmailCOL();
        this.emailPacienteDAO = new EmailPacienteDAO();
    }

    public Paciente cadastrarPaciente(Paciente paciente) throws Exception {
        if (!pacienteCOL.pacienteValido(paciente)) throw new ReceitaMedicaException("Paciente inválido.");

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            if (!enderecoEspecificoCOL.enderecoEspecificoValido(paciente.getEnderecoEspecifico()) || enderecoDAO.selecionarEnderecoPorId(paciente.getEnderecoEspecifico().getEndereco().getId(), conexao) == null) {
                throw new ReceitaMedicaException("Endereço específico inválido ou endereço não existe.");
            }

            for (Telefone telefone : paciente.getTelefones()) {
                if (telefoneCOL.telefoneValido(telefone)) {
                    DDD ddd = dddDao.selecionarDDDPorNumero(telefone.getDdd().getNumeroDDD(), conexao);
                    DDI ddi = ddiDao.selecionarDDIPorNumero(telefone.getDdi().getNumeroDDI(), conexao);
                    Telefone telefoneEncontrado = telefonePacienteDAO.selecionarTelefonePacientePorNumero(telefone.getNumero(), conexao);

                    if (ddd == null) throw new ReceitaMedicaException("DDD não existe");
                    if (ddi == null) throw new ReceitaMedicaException("DDI não existe");
                    if (telefoneEncontrado != null) throw new ReceitaMedicaException("Telefone " + telefone.getDdi().getNumeroDDI() + " " + telefone.getDdd().getNumeroDDD() + " " + telefone.getNumero() + " já está sendo utilizado.");
                } else {
                    throw new ReceitaMedicaException("Telefone inválido " + telefone.getDdi().getNumeroDDI() + " " + telefone.getDdd().getNumeroDDD() + " " + telefone.getNumero());
                }
            }

            for (Email email : paciente.getEmails()) {
                if (emailCOL.emailValido(email)) {
                    Email emailEncontrado = emailPacienteDAO.selecionarEmailPacientePorEmail(email.getEmail(), conexao);

                    if (emailEncontrado != null) throw new ReceitaMedicaException("Email " + emailEncontrado.getEmail() + " já está sendo utilizado.");
                } else {
                    throw new ReceitaMedicaException("Email inválidoo " + email.getEmail());
                }
            }

            if (!sexoCOL.sexoValido(paciente.getSexo()) || sexoDAO.selecionarSexoPorSigla(paciente.getSexo().getSigla(), conexao) == null) {
                throw new ReceitaMedicaException("Sexo inválido ou não existe.");
            }

            if(!cpfCol.cpfValido(paciente.getCpf()) || pacienteDAO.selecionarPacientePorCPF(paciente.getCpf().getCpf(), conexao) != null) {
                throw new ReceitaMedicaException("CPF inválido ou já cadastrado.");
            }

            Paciente pacienteCadastrado;

            try {
                pacienteCadastrado = pacienteDAO.inserirPaciente(paciente, conexao);
                if(pacienteCadastrado != null) {
                    telefonePacienteDAO.inserirTelefonesPaciente(pacienteCadastrado, paciente.getTelefones(), conexao);
                    emailPacienteDAO.inserirEmailPaciente(pacienteCadastrado, paciente.getEmails(), conexao);
                }

                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
                System.out.println(e.getMessage());
                throw new ReceitaMedicaException("Erro ao cadastrar paciente.");
            }

            return pacienteCadastrado;
        }
    }

    public Paciente consultarPaciente(Long id) throws Exception {
        if (!pacienteCOL.idValido(id)) {
            throw new ReceitaMedicaException("Id do paciente inválido " + id + ".");
        }

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            Paciente paciente;
            try {
                paciente = pacienteDAO.selecionarPacientePorId(id, conexao);

                if (paciente != null) {
                    paciente.setTelefones(telefonePacienteDAO.selecionarTelefonesPaciente(id, conexao));
                    paciente.setEmails(emailPacienteDAO.selecionarEmailsPaciente(id, conexao));
                }

                conexao.commit();
                if(paciente == null) {
                    throw new EnderecoException("Não foi possível buscar o paciente pelo id " + id + ".");
                }
            } catch (Exception e) {
                conexao.rollback();
                throw new EnderecoException("Não foi possível buscar o paciente pelo id " + id + ".");
            }

            return paciente;
        }
    }

    public static void main(String[] args) {
        try {
            UCPacienteServicos pacienteServicos = new UCPacienteServicos();

            // Criando um novo paciente
            Paciente paciente = new Paciente();
            paciente.setNome("Bruna");

            // Criando CPF
            CPF cpf = new CPF("13698745621");
            paciente.setCpf(cpf);

            // Criando Sexo
            Sexo sexo = new Sexo();
            sexo.setSigla("M");
            sexo.setNome("Masculino");
            paciente.setSexo(sexo);

            // Criando Endereço
            Endereco endereco = new Endereco();
            endereco.setId(2L); // Supondo que já existe um endereço no banco

            EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico("123", "Apto 101", endereco);
            paciente.setEnderecoEspecifico(enderecoEspecifico);

            // Criando Telefone
            Telefone telefone = new Telefone();
            telefone.setNumero("991456333");

            Telefone telefone1 = new Telefone();
            telefone1.setNumero("991458766");

            DDD ddd = new DDD();
            ddd.setNumeroDDD(41); // Supondo que esse DDD existe no banco
            telefone.setDdd(ddd);
            telefone1.setDdd(ddd);

            DDI ddi = new DDI();
            ddi.setNumeroDDI(55); // Supondo que esse DDI existe no banco
            telefone.setDdi(ddi);
            telefone1.setDdi(ddi);

            paciente.setTelefones(new ArrayList<>());
            paciente.getTelefones().add(telefone);
            paciente.getTelefones().add(telefone1);

            // Criando Email
            Email email = new Email();
            email.setEmail("brunaaaaaaa@email.com");
            Email email1 = new Email();
            email1.setEmail("bruna.teste@email.com");

            paciente.setEmails(new ArrayList<>());
            paciente.getEmails().add(email);
            paciente.getEmails().add(email1);

            // Cadastrando Paciente
            Paciente pacienteCadastrado = pacienteServicos.cadastrarPaciente(paciente);

            if (pacienteCadastrado != null) {
                System.out.println("Paciente cadastrado com sucesso! ID: " + pacienteCadastrado.getId());

                // Consultando Paciente
                Paciente pacienteConsultado = pacienteServicos.consultarPaciente(pacienteCadastrado.getId());

                if (pacienteConsultado != null) {
                    System.out.println("Paciente consultado: " + pacienteConsultado.getNome());
                    System.out.println("CPF: " + pacienteConsultado.getCpf().getCpf());
                    System.out.println("Sexo: " + pacienteConsultado.getSexo().getSigla());
                    System.out.println("Endereço: " + pacienteConsultado.getEnderecoEspecifico().getEndereco().getId() +
                            ", Número: " + pacienteConsultado.getEnderecoEspecifico().getNumero() +
                            ", Complemento: " + pacienteConsultado.getEnderecoEspecifico().getComplemento());

                    System.out.println("Telefones:");
                    for (Telefone t : pacienteConsultado.getTelefones()) {
                        System.out.println("  DDI: " + t.getDdi().getNumeroDDI() +
                                ", DDD: " + t.getDdd().getNumeroDDD() +
                                ", Número: " + t.getNumero());
                    }

                    System.out.println("Emails:");
                    for (Email e : pacienteConsultado.getEmails()) {
                        System.out.println("  Email: " + e.getEmail());
                    }
                } else {
                    System.out.println("Erro ao consultar paciente.");
                }
            } else {
                System.out.println("Erro ao cadastrar paciente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
