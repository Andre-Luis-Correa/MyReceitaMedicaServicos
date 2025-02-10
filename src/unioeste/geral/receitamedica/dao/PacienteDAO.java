package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.col.EnderecoCOL;
import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.endereco.service.UCEnderecoGeralServicos;
import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacienteDAO {

    public static Paciente selectPacientePorId(Long id) throws Exception {
        String sql = "SELECT id_paciente, nome_paciente, cpf_paciente, id_endereco, complemento_endereco, numero_endereco, sigla_sexo FROM paciente WHERE id_paciente = ?";

        try (Connection conexaoBD = new ConexaoBD().getConexaoComBD();
             PreparedStatement cmd = conexaoBD.prepareStatement(sql)) {

            cmd.setLong(1, id);
            try (ResultSet result = cmd.executeQuery()) {
                if (result.next()) {
                    Paciente paciente = new Paciente();
                    paciente.setId(id);
                    paciente.setNome(result.getString("nome_paciente"));
                    paciente.setCpf(new CPF(result.getString("cpf_paciente")));
                    Endereco endereco = EnderecoDAO.selectEnderecoPorId(result.getLong("id_endereco"));
                    paciente.setEnderecoEspecifico(new EnderecoEspecifico(result.getString("numero_endereco"), result.getString("complemento_endereco"), endereco));
                    paciente.setSexo(SexoDAO.selectSexoPorSigla(result.getString("sigla_sexo")));
                    paciente.setEmails(EmailPacienteDAO.selectTodosEmailPorIdPaciente(id));
                    paciente.setTelefones(TelefonePacienteDAO.selectTodosTelefonePorIdpaciente(id));
                    return paciente;
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao buscar o paciente pelo ID: " + id, e);
        }

        return null;
    }

    public static Object selectPacientePorCpf(String cpf) throws Exception {
        String sql = "SELECT id_paciente, nome_paciente, cpf_paciente, id_endereco, complemento_endereco, numero_endereco, sigla_sexo FROM paciente WHERE cpf_paciente = ?";

        try (Connection conexaoBD = new ConexaoBD().getConexaoComBD();
             PreparedStatement cmd = conexaoBD.prepareStatement(sql)) {

            cmd.setString(1, cpf);
            try (ResultSet result = cmd.executeQuery()) {
                if (result.next()) {
                    Paciente paciente = new Paciente();
                    paciente.setId(result.getLong("id_paciente"));
                    paciente.setNome(result.getString("nome_paciente"));
                    paciente.setCpf(new CPF(result.getString("cpf_paciente")));
                    Endereco endereco = EnderecoDAO.selectEnderecoPorId(result.getLong("id_endereco"));
                    paciente.setEnderecoEspecifico(new EnderecoEspecifico(result.getString("numero_endereco"), result.getString("complemento_endereco"), endereco));
                    paciente.setSexo(SexoDAO.selectSexoPorSigla(result.getString("sigla_sexo")));
                    paciente.setEmails(EmailPacienteDAO.selectTodosEmailPorIdPaciente(result.getLong("id_paciente")));
                    paciente.setTelefones(TelefonePacienteDAO.selectTodosTelefonePorIdpaciente(result.getLong("id_paciente")));
                    return paciente;
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao buscar o paciente pelo cpf: " + cpf, e);
        }

        return null;
    }

    public static Paciente insertPaciente(Paciente paciente) throws Exception {
        String sql = "INSERT INTO paciente(nome_paciente, cpf_paciente, id_endereco, complemento_endereco, numero_endereco, sigla_sexo) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conexao = null;
        PreparedStatement cmd = null;
        ResultSet generatedKeys = null;

        try {
            conexao = new ConexaoBD().getConexaoComBD();
            conexao.setAutoCommit(false);

            cmd = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            cmd.setString(1, paciente.getNome());
            cmd.setString(2, paciente.getCpf().getCpf());
            cmd.setLong(3, paciente.getEnderecoEspecifico().getEndereco().getId());
            cmd.setString(4, paciente.getEnderecoEspecifico().getComplemento());
            cmd.setString(5, paciente.getEnderecoEspecifico().getNumero());
            cmd.setString(6, paciente.getSexo().getSigla());

            int affectedRows = cmd.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir paciente, nenhum registro foi adicionado.");
            }

            generatedKeys = cmd.getGeneratedKeys();
            if (generatedKeys.next()) {
                long idPaciente = generatedKeys.getLong(1);
                paciente.setId(idPaciente);

                EmailPacienteDAO.insertEmails(paciente, conexao);
                TelefonePacienteDAO.insertTelefones(paciente, conexao);

                conexao.commit(); // Confirma a transação
                System.out.println("Paciente cadastrado com sucesso! ID: " + paciente.getId());
            } else {
                throw new SQLException("Falha ao obter o ID do paciente.");
            }

        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback();
                    System.err.println("Transação revertida devido a erro: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    throw new Exception("Erro ao reverter transação: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new Exception("Erro ao inserir paciente: " + paciente, e);

        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (cmd != null) cmd.close();
                if (conexao != null) conexao.setAutoCommit(true);
                if (conexao != null) conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return paciente;
    }
}
