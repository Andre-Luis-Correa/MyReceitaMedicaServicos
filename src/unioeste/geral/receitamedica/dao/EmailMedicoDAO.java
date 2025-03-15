package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.email.Email;
import unioeste.geral.receitamedica.bo.medico.Medico;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailMedicoDAO {


    public static List<Email> selectTodosEmailPorIdMedico(Long id) throws Exception {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT email FROM email_medico WHERE id_medico = ?";

        try (Connection conn = new ConexaoBD().getConexaoComBD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Email email = new Email(rs.getString("email"));
                emails.add(email);
            }

        } catch (Exception e) {
            throw new Exception("Erro ao buscar emails do médico pelo ID: " + id, e);
        }

        return emails;
    }

    public static void insertEmails(Medico medico, Connection conexao) throws SQLException {
        String sql = "INSERT INTO email_medico (email, id_medico) VALUES (?, ?)";

        try (PreparedStatement cmd = conexao.prepareStatement(sql)) {
            for (Email email : medico.getEmails()) {
                cmd.setString(1, email.getEmail());
                cmd.setLong(2, medico.getId());
                cmd.executeUpdate();
            }
        }
    }

    public Email selecionarEmailMedicoPorEmail(String email, Connection conexao) throws SQLException {
        String sql = "SELECT email FROM email_medico WHERE email = ?";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Email email1 = new Email();
                    email1.setEmail(resultSet.getString("email"));
                    return email1;
                }
            }
        }

        return null;
    }

    public void inserirEmailMedico(Medico medico, List<Email> emails, Connection conexao) throws SQLException {
        String sql = "INSERT INTO email_medico (email, id_medico) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            for (Email email : emails) {
                preparedStatement.setString(1, email.getEmail());
                preparedStatement.setLong(2, medico.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public List<Email> selecionarEmailsMedico(Long id, Connection conexao) throws Exception {
        String sql = "SELECT ep.email " +
                "FROM email_medico ep " +
                "WHERE ep.id_medico = ?";

        List<Email> emails = new ArrayList<>();

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Email email = new Email();
                    email.setEmail(resultSet.getString("email"));
                    emails.add(email);
                }
            }
        }
        return emails;
    }
}
