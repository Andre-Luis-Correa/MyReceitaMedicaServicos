package unioeste.geral.receitamedica.service;

import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.col.PacienteCOL;
import unioeste.geral.receitamedica.dao.PacienteDAO;
import unioeste.geral.receitamedica.exception.ReceitaMedicaException;

public class UCPacienteServicos {

    public static void cadastrarPaciente(Paciente paciente) {

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

    }
}
