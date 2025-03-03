package unioeste.geral.receitamedica.dao;

import unioeste.geral.pessoa.bo.ddd.DDD;
import unioeste.geral.pessoa.bo.ddi.DDI;
import unioeste.geral.pessoa.bo.telefone.Telefone;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TelefonePacienteDAO {

    public Telefone selecionarTelefonePacientePorNumero(String numero, Connection conexao) throws Exception {
        String sql = "SELECT tp.numero_telefone, d.numero_ddd, di.numero_ddi " +
                "FROM telefone_paciente tp " +
                "JOIN ddd d ON tp.numero_ddd = d.numero_ddd " +
                "JOIN ddi di ON tp.numero_ddi = di.numero_ddi " +
                "WHERE tp.numero_telefone = ?";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, numero);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Telefone telefone = new Telefone();
                    telefone.setNumero(resultSet.getString("numero_telefone"));

                    DDD ddd = new DDD();
                    ddd.setNumeroDDD(resultSet.getInt("numero_ddd"));
                    telefone.setDdd(ddd);

                    DDI ddi = new DDI();
                    ddi.setNumeroDDI(resultSet.getInt("numero_ddi"));
                    telefone.setDdi(ddi);

                    return telefone;
                }
            }
        }

        return null;
    }

    public void inserirTelefonesPaciente(Paciente paciente, List<Telefone> telefones, Connection conexao) throws Exception {
        String sql = "INSERT INTO telefone_paciente (numero_telefone, numero_ddd, numero_ddi, id_paciente) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            for (Telefone telefone : telefones) {
                preparedStatement.setString(1, telefone.getNumero());
                preparedStatement.setInt(2, telefone.getDdd().getNumeroDDD());
                preparedStatement.setInt(3, telefone.getDdi().getNumeroDDI());
                preparedStatement.setLong(4, paciente.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public List<Telefone> selecionarTelefonesPaciente(Long id, Connection conexao) throws Exception {
        String sql = "SELECT tp.numero_telefone, d.numero_ddd, di.numero_ddi " +
                "FROM telefone_paciente tp " +
                "JOIN ddd d ON tp.numero_ddd = d.numero_ddd " +
                "JOIN ddi di ON tp.numero_ddi = di.numero_ddi " +
                "WHERE tp.id_paciente = ?";

        List<Telefone> telefones = new ArrayList<>();

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Telefone telefone = new Telefone();
                    telefone.setNumero(resultSet.getString("numero_telefone"));

                    DDD ddd = new DDD();
                    ddd.setNumeroDDD(resultSet.getInt("numero_ddd"));
                    telefone.setDdd(ddd);

                    DDI ddi = new DDI();
                    ddi.setNumeroDDI(resultSet.getInt("numero_ddi"));
                    telefone.setDdi(ddi);

                    telefones.add(telefone);
                }
            }
        }
        return telefones;
    }

}
