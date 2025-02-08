package unioeste.geral.receitamedica.service;

import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.service.UCEnderecoGeralServicos;
import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.pessoa.bo.email.Email;
import unioeste.geral.pessoa.bo.sexo.Sexo;
import unioeste.geral.pessoa.bo.telefone.Telefone;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.col.PacienteCOL;
import unioeste.geral.receitamedica.dao.PacienteDAO;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

import java.util.ArrayList;

public class UCPacienteServicos {

    public UCPacienteServicos() {

    }

    public static void cadastrarPaciente(Paciente paciente) throws Exception {
        if (!PacienteCOL.pacienteValido(paciente)) {
            throw new ReceitaMedicaException("Paciente inválido");
        } else if (PacienteCOL.pacienteExiste(paciente)) {
            throw new ReceitaMedicaException("Paciente já cadastrado");
        } else {
            PacienteDAO.insertPaciente(paciente);
            System.out.println("Paciente cadastrado");
        }

    }

    public static Paciente consultarPaciente(Long id) throws Exception {
        if (!PacienteCOL.idValido(id)) {
            throw new ReceitaMedicaException("ID do paciente inválido (" + id + ")");
        }

        Paciente paciente = PacienteDAO.selectPacientePorId(id);

        if (paciente == null) {
            throw new ReceitaMedicaException("Paciente não cadastrado");
        }

        return paciente;
    }

    public static void main(String[] arg) throws Exception {
        Paciente paciente = consultarPaciente(1L);

        if (paciente != null) {
            System.out.println("===== Dados do Paciente =====");
            System.out.println("ID: " + paciente.getId());
            System.out.println("Nome: " + paciente.getNome());
            System.out.println("CPF: " + paciente.getCpf().getCpf());
            System.out.println("Sexo: " + paciente.getSexo().getSigla()); // Caso tenha esse campo

            System.out.println("\n===== Contatos =====");
            // Imprimindo Emails
            System.out.println("Emails:");
            if (paciente.getEmails().isEmpty()) {
                System.out.println("Nenhum cadastrado");
            } else {
                paciente.getEmails().forEach(email -> System.out.println(" - " + email.getEmail()));
            }

            // Imprimindo Telefones
            System.out.println("Telefones:");
            if (paciente.getTelefones().isEmpty()) {
                System.out.println("Nenhum cadastrado");
            } else {
                paciente.getTelefones().forEach(telefone ->
                        System.out.println(" - " + telefone.getDdi().getNumeroDDI() + " " + telefone.getDdd().getNumeroDDD() + " " + telefone.getNumero())
                );
            };


            System.out.println("\n===== Endereço =====");
            Endereco endereco = paciente.getEnderecoEspecifico().getEndereco();
            if (endereco != null) {
                System.out.println("CEP: " + endereco.getCep());
                System.out.println("Logradouro: " + endereco.getLogradouro().getNome());
                System.out.println("Número: " + paciente.getEnderecoEspecifico().getNumero());
                System.out.println("Complemento: " + (paciente.getEnderecoEspecifico().getComplemento() != null ? paciente.getEnderecoEspecifico().getComplemento() : "Nenhum"));
                System.out.println("Bairro: " + endereco.getBairro().getNome());
                System.out.println("Cidade: " + endereco.getCidade().getNome() + " - " + endereco.getCidade().getUnidadeFederativa().getSigla());
            } else {
                System.out.println("Endereço não cadastrado.");
            }
        } else {
            System.out.println("Paciente não encontrado!");
        }


        System.out.println("=== Teste de Cadastro de Paciente ===");

        // Criando um endereço para o paciente
        Endereco endereco = UCEnderecoGeralServicos.obterEnderecoPorId(1L);

        // Criando um paciente para cadastrar
        Paciente pacienteNovo = new Paciente();
        pacienteNovo.setNome("João da Silva");
        pacienteNovo.setCpf(new CPF("13037025922"));
        pacienteNovo.setSexo(new Sexo("M", "Masculino"));
        pacienteNovo.setEnderecoEspecifico(new EnderecoEspecifico("numero", "complemento", endereco));
        pacienteNovo.setEmails(new ArrayList<>());
        pacienteNovo.setTelefones(new ArrayList<>());

        // Adicionando Email e Telefone
        pacienteNovo.getEmails().add(new Email("joaoTesteInserção@email.com"));
        pacienteNovo.getTelefones().add(new Telefone("991456211", ServicosUteisGeral.obterTodosDDD().get(0), ServicosUteisGeral.obterTodosDDI().get(0)));

        // Cadastrando o paciente
        cadastrarPaciente(pacienteNovo);

        System.out.println("\n=== Teste de Consulta de Paciente ===");

        // Consultando o paciente que acabamos de cadastrar
        Paciente pacienteConsultado = consultarPaciente(pacienteNovo.getId());

        if (pacienteConsultado != null) {
            System.out.println("===== Dados do Paciente =====");
            System.out.println("ID: " + pacienteConsultado.getId());
            System.out.println("Nome: " + pacienteConsultado.getNome());
            System.out.println("CPF: " + pacienteConsultado.getCpf().getCpf());
            System.out.println("Sexo: " + pacienteConsultado.getSexo().getSigla());

            System.out.println("\n===== Contatos =====");
            System.out.println("Emails:");
            if (pacienteConsultado.getEmails().isEmpty()) {
                System.out.println("Nenhum cadastrado");
            } else {
                pacienteConsultado.getEmails().forEach(email -> System.out.println(" - " + email.getEmail()));
            }

            System.out.println("Telefones:");
            if (pacienteConsultado.getTelefones().isEmpty()) {
                System.out.println("Nenhum cadastrado");
            } else {
                pacienteConsultado.getTelefones().forEach(telefone ->
                        System.out.println(" - " + telefone.getDdi() + " " + telefone.getDdd() + " " + telefone.getNumero())
                );
            }

            System.out.println("\n===== Endereço =====");
            Endereco enderecoConsultado = pacienteConsultado.getEnderecoEspecifico().getEndereco();
            if (enderecoConsultado != null) {
                System.out.println("CEP: " + enderecoConsultado.getCep());
                System.out.println("Logradouro: " + enderecoConsultado.getLogradouro().getNome());
                System.out.println("Número: " + pacienteConsultado.getEnderecoEspecifico().getNumero());
                System.out.println("Complemento: " + (pacienteConsultado.getEnderecoEspecifico().getComplemento() != null ? pacienteConsultado.getEnderecoEspecifico().getComplemento() : "Nenhum"));
                System.out.println("Bairro: " + enderecoConsultado.getBairro().getNome());
                System.out.println("Cidade: " + enderecoConsultado.getCidade().getNome() + " - " + enderecoConsultado.getCidade().getUnidadeFederativa().getSigla());
            } else {
                System.out.println("Endereço não cadastrado.");
            }
        } else {
            System.out.println("Paciente não encontrado!");
        }
    }
}
