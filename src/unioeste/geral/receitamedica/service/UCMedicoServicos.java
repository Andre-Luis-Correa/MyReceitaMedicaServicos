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
import unioeste.geral.receitamedica.bo.crm.CRM;
import unioeste.geral.receitamedica.bo.medico.Medico;
import unioeste.geral.receitamedica.col.CrmCOL;
import unioeste.geral.receitamedica.col.MedicoCOL;
import unioeste.geral.receitamedica.dao.EmailMedicoDAO;
import unioeste.geral.receitamedica.dao.MedicoDAO;
import unioeste.geral.receitamedica.dao.TelefoneMedicoDAO;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

import java.sql.Connection;
import java.util.ArrayList;

public class UCMedicoServicos {

    private MedicoDAO medicoDAO;
    private MedicoCOL medicoCOL;
    private EnderecoEspecificoCOL enderecoEspecificoCOL;
    private EnderecoDAO enderecoDAO;
    private TelefoneCOL telefoneCOL;
    private TelefoneMedicoDAO telefoneMedicoDAO;
    private DDDDao dddDao;
    private DDIDao ddiDao;
    private SexoCOL sexoCOL;
    private SexoDAO sexoDAO;
    private CPFCOL cpfCol;
    private CrmCOL crmCOL;
    private EmailCOL emailCOL;
    private EmailMedicoDAO emailMedicoDAO;

    public UCMedicoServicos() {
        this.medicoDAO = new MedicoDAO();
        this.medicoCOL = new MedicoCOL();
        this.enderecoEspecificoCOL = new EnderecoEspecificoCOL();
        this.enderecoDAO = new EnderecoDAO();
        this.telefoneCOL = new TelefoneCOL();
        this.telefoneMedicoDAO = new TelefoneMedicoDAO();
        this.dddDao = new DDDDao();
        this.ddiDao = new DDIDao();
        this.sexoCOL = new SexoCOL();
        this.sexoDAO = new SexoDAO();
        this.cpfCol = new CPFCOL();
        this.crmCOL = new CrmCOL();
        this.emailCOL = new EmailCOL();
        this.emailMedicoDAO = new EmailMedicoDAO();
    }

    public Medico cadastrarMedico(Medico medico) throws Exception {
        if (!medicoCOL.medicoValido(medico)) throw new ReceitaMedicaException("Médico inválido.");

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            if (!enderecoEspecificoCOL.enderecoEspecificoValido(medico.getEnderecoEspecifico()) || enderecoDAO.selecionarEnderecoPorId(medico.getEnderecoEspecifico().getEndereco().getId(), conexao) == null) {
                throw new ReceitaMedicaException("Endereço específico inválido ou endereço não existe.");
            }

            for (Telefone telefone : medico.getTelefones()) {
                if (telefoneCOL.telefoneValido(telefone)) {
                    DDD ddd = dddDao.selecionarDDDPorNumero(telefone.getDdd().getNumeroDDD(), conexao);
                    DDI ddi = ddiDao.selecionarDDIPorNumero(telefone.getDdi().getNumeroDDI(), conexao);
                    Telefone telefoneEncontrado = telefoneMedicoDAO.selecionarTelefoneMedicoPorNumero(telefone.getNumero(), conexao);

                    if (ddd == null) throw new ReceitaMedicaException("DDD não existe");
                    if (ddi == null) throw new ReceitaMedicaException("DDI não existe");
                    if (telefoneEncontrado != null) throw new ReceitaMedicaException("Telefone " + telefone.getDdi().getNumeroDDI() + " " + telefone.getDdd().getNumeroDDD() + " " + telefone.getNumero() + " já está sendo utilizado.");
                } else {
                    throw new ReceitaMedicaException("Telefone inválido " + telefone.getDdi().getNumeroDDI() + " " + telefone.getDdd().getNumeroDDD() + " " + telefone.getNumero());
                }
            }

            for (Email email : medico.getEmails()) {
                if (emailCOL.emailValido(email)) {
                    Email emailEncontrado = emailMedicoDAO.selecionarEmailMedicoPorEmail(email.getEmail(), conexao);

                    if (emailEncontrado != null) throw new ReceitaMedicaException("Email " + emailEncontrado.getEmail() + " já está sendo utilizado.");
                } else {
                    throw new ReceitaMedicaException("Email inválidoo " + email.getEmail());
                }
            }

            if (!sexoCOL.sexoValido(medico.getSexo()) || sexoDAO.selecionarSexoPorSigla(medico.getSexo().getSigla(), conexao) == null) {
                throw new ReceitaMedicaException("Sexo inválido ou não existe.");
            }

            if(!cpfCol.cpfValido(medico.getCpf()) || medicoDAO.selecionarMedicoPorCPF(medico.getCpf().getCpf(), conexao) != null) {
                throw new ReceitaMedicaException("CPF inválido ou já cadastrado.");
            }

            if(!crmCOL.crmValido(medico.getCrm()) || medicoDAO.selecionarMedicoPorCRM(medico.getCrm().getCrm(), conexao) != null) {
                throw new ReceitaMedicaException("CRM inválido ou já cadastrado.");
            }

            Medico medicoCadastrado;

            try {
                medicoCadastrado = medicoDAO.inserirMedico(medico, conexao);
                if(medicoCadastrado != null) {
                    telefoneMedicoDAO.inserirTelefonesMedico(medicoCadastrado, medico.getTelefones(), conexao);
                    emailMedicoDAO.inserirEmailMedico(medicoCadastrado, medico.getEmails(), conexao);
                }

                conexao.commit();
            } catch (Exception e) {
                conexao.rollback();
                System.out.println(e.getMessage());
                throw new ReceitaMedicaException("Erro ao cadastrar médico.");
            }

            return medicoCadastrado;
        }
    }

    public Medico consultarMedicoPorId(Long id) throws Exception {
        if (!medicoCOL.idValido(id)) {
            throw new ReceitaMedicaException("Id do médico inválido " + id + ".");
        }

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            Medico medico;
            try {
                medico = medicoDAO.selecionarMedicoPorId(id, conexao);

                if (medico != null) {
                    medico.setTelefones(telefoneMedicoDAO.selecionarTelefonesMedico(id, conexao));
                    medico.setEmails(emailMedicoDAO.selecionarEmailsMedico(id, conexao));
                }

                conexao.commit();
                if(medico == null) {
                    throw new EnderecoException("Não foi possível buscar o médico pelo id " + id + ".");
                }
            } catch (Exception e) {
                conexao.rollback();
                throw new EnderecoException("Não foi possível buscar o médico pelo id " + id + ".");
            }

            return medico;
        }
    }

    public Medico consultarMedicoPorCPF(String cpf) throws Exception {
        if (!cpfCol.cpfValido(new CPF(cpf))) {
            throw new ReceitaMedicaException("CPF do médico inválido " + cpf + ".");
        }

        try (Connection conexao = new ConexaoBD().getConexaoComBD()) {
            conexao.setAutoCommit(false);

            Medico medico;
            try {
                medico = medicoDAO.selecionarMedicoPorCPF(cpf, conexao);

                if (medico != null) {
                    medico.setTelefones(telefoneMedicoDAO.selecionarTelefonesMedico(medico.getId(), conexao));
                    medico.setEmails(emailMedicoDAO.selecionarEmailsMedico(medico.getId(), conexao));
                }

                conexao.commit();
                if(medico == null) {
                    throw new EnderecoException("Não foi possível buscar o médico pelo cpf " + cpf + ".");
                }
            } catch (Exception e) {
                conexao.rollback();
                throw new EnderecoException("Não foi possível buscar o médico pelo cpf " + cpf + ".");
            }

            return medico;
        }
    }

    public static void main(String[] args) {
        try {
            UCMedicoServicos ucMedicoServicos = new UCMedicoServicos();

            // Criando um novo paciente
            Medico medico = new Medico();
            medico.setNome("André");

            // Criando CPF
            CPF cpf = new CPF("13698745622");
            medico.setCpf(cpf);

            CRM crm = new CRM("12344-PR");
            medico.setCrm(crm);

            // Criando Sexo
            Sexo sexo = new Sexo();
            sexo.setSigla("M");
            sexo.setNome("Masculino");
            medico.setSexo(sexo);

            // Criando Endereço
            Endereco endereco = new Endereco();
            endereco.setId(2L); // Supondo que já existe um endereço no banco

            EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico("123", "Apto 101", endereco);
            medico.setEnderecoEspecifico(enderecoEspecifico);

            // Criando Telefone
            Telefone telefone = new Telefone();
            telefone.setNumero("991457711");

            Telefone telefone1 = new Telefone();
            telefone1.setNumero("998908766");

            DDD ddd = new DDD();
            ddd.setNumeroDDD(41); // Supondo que esse DDD existe no banco
            telefone.setDdd(ddd);
            telefone1.setDdd(ddd);

            DDI ddi = new DDI();
            ddi.setNumeroDDI(55); // Supondo que esse DDI existe no banco
            telefone.setDdi(ddi);
            telefone1.setDdi(ddi);

            medico.setTelefones(new ArrayList<>());
            medico.getTelefones().add(telefone);
            medico.getTelefones().add(telefone1);

            // Criando Email
            Email email = new Email();
            email.setEmail("braaaaaa@email.com");
            Email email1 = new Email();
            email1.setEmail("brunatee@email.com");

            medico.setEmails(new ArrayList<>());
            medico.getEmails().add(email);
            medico.getEmails().add(email1);

            // Cadastrando Paciente
            Medico medicoCadastrado = ucMedicoServicos.cadastrarMedico(medico);

            if (medicoCadastrado != null) {
                System.out.println("Paciente cadastrado com sucesso! ID: " + medicoCadastrado.getId());

                // Consultando Paciente
                Medico medicoConsultado = ucMedicoServicos.consultarMedicoPorCPF(medicoCadastrado.getCpf().getCpf());

                if (medicoConsultado != null) {
                    System.out.println("Paciente consultado: " + medicoConsultado.getNome());
                    System.out.println("CPF: " + medicoConsultado.getCpf().getCpf());
                    System.out.println("Sexo: " + medicoConsultado.getSexo().getSigla());
                    System.out.println("Endereço: " + medicoConsultado.getEnderecoEspecifico().getEndereco().getId() +
                            ", Número: " + medicoConsultado.getEnderecoEspecifico().getNumero() +
                            ", Complemento: " + medicoConsultado.getEnderecoEspecifico().getComplemento());

                    System.out.println("Telefones:");
                    for (Telefone t : medicoConsultado.getTelefones()) {
                        System.out.println("  DDI: " + t.getDdi().getNumeroDDI() +
                                ", DDD: " + t.getDdd().getNumeroDDD() +
                                ", Número: " + t.getNumero());
                    }

                    System.out.println("Emails:");
                    for (Email e : medicoConsultado.getEmails()) {
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
