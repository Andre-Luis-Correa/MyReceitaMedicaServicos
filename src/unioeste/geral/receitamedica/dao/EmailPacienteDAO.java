package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.email.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}
