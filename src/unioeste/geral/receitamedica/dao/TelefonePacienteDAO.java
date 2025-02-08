package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.telefone.Telefone;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelefonePacienteDAO {

    public static List<Telefone> selectTodosTelefonePorIdpaciente(Long id) throws Exception {
        List<Telefone> telefones = new ArrayList<>();
        String sql = "SELECT numero_telefone, numero_ddd, numero_ddi FROM telefone_paciente WHERE id_paciente = ?";

        try (Connection conn = new ConexaoBD().getConexaoComBD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                Telefone telefone = new Telefone();
                telefone.setDdd(DDDDao.selectDDDPorNumero(result.getInt("numero_ddd")));
                telefone.setDdi(DDIDao.selectDDIPorNumero(result.getInt("numero_ddi")));
                telefone.setNumero(result.getString("numero_telefone"));
                telefones.add(telefone);
            }

        } catch (Exception e) {
            throw new Exception("Erro ao buscar telefones do paciente pelo ID: " + id, e);
        }

        return telefones;
    }

    public static void insertTelefones(Paciente paciente, Connection conexao) throws SQLException {
        String sql = "INSERT INTO telefone_paciente (numero_telefone, numero_ddd, numero_ddi, id_paciente) VALUES (?, ?, ?, ?)";

        try (PreparedStatement cmd = conexao.prepareStatement(sql)) {
            for (Telefone telefone : paciente.getTelefones()) {
                cmd.setString(1, telefone.getNumero());
                cmd.setInt(2, telefone.getDdd().getNumeroDDD());
                cmd.setInt(3, telefone.getDdi().getNumeroDDI());
                cmd.setLong(4, paciente.getId());
                cmd.executeUpdate();
            }
        }
    }

}
