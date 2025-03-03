package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.email.Email;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailPacienteDAO {


    public static List<Email> selectTodosEmailPorIdPaciente(Long id) throws Exception {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT email_paciente FROM email_paciente WHERE id_paciente = ?";

        try (Connection conn = new ConexaoBD().getConexaoComBD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Email email = new Email(rs.getString("email_paciente"));
                emails.add(email);
            }

        } catch (Exception e) {
            throw new Exception("Erro ao buscar emails do paciente pelo ID: " + id, e);
        }

        return emails;
    }

    public static void insertEmails(Paciente paciente, Connection conexao) throws SQLException {
        String sql = "INSERT INTO email_paciente (email_paciente, id_paciente) VALUES (?, ?)";

        try (PreparedStatement cmd = conexao.prepareStatement(sql)) {
            for (Email email : paciente.getEmails()) {
                cmd.setString(1, email.getEmail());
                cmd.setLong(2, paciente.getId());
                cmd.executeUpdate();
            }
        }
    }

    public Email selecionarEmailPacientePorEmail(String email, Connection conexao) throws SQLException {
        String sql = "SELECT email_paciente FROM email_paciente WHERE email_paciente = ?";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Email email1 = new Email();
                    email1.setEmail(resultSet.getString("email_paciente"));
                    return email1;
                }
            }
        }

        return null;
    }

    public void inserirEmailPaciente(Paciente paciente, List<Email> emails, Connection conexao) throws SQLException {
        String sql = "INSERT INTO email_paciente (email_paciente, id_paciente) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            for (Email email : emails) {
                preparedStatement.setString(1, email.getEmail());
                preparedStatement.setLong(2, paciente.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public List<Email> selecionarEmailsPaciente(Long id, Connection conexao) throws Exception {
        String sql = "SELECT ep.email_paciente " +
                "FROM email_paciente ep " +
                "WHERE ep.id_paciente = ?";

        List<Email> emails = new ArrayList<>();

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Email email = new Email();
                    email.setEmail(resultSet.getString("email_paciente"));
                    emails.add(email);
                }
            }
        }
        return emails;
    }
}
