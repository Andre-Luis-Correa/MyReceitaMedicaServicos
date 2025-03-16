package unioeste.geral.receitamedica.dao;

import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.bo.medicamento.Medicamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    public Medicamento inserirMedicamento(Medicamento medicamento, Connection conexao) throws SQLException {
        String sql = "INSERT INTO medicamento (nome_medicamento) " +
                "VALUES (?) RETURNING id_medicamento";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, medicamento.getNome());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    medicamento.setId(resultSet.getLong("id_medicamento"));

                    return medicamento;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<Medicamento> selecionarTodosMedicamento(Connection conexao) throws SQLException {
        String sql = """
        SELECT m.id_medicamento, m.nome_medicamento
        FROM medicamento m
        ORDER BY m.nome_medicamento;
    """;

        List<Medicamento> medicamentos = new ArrayList<>();

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Medicamento medicamento = new Medicamento();
                medicamento.setId(resultSet.getLong("id_medicamento"));
                medicamento.setNome(resultSet.getString("nome_medicamento"));

                medicamentos.add(medicamento);
            }
        }

        return medicamentos;
    }

    public Medicamento selecionarMedicamentoPorId(Long id, Connection conexao) throws SQLException {
        String sql = """
        SELECT m.nome_medicamento
        FROM medicamento m
        WHERE m.id_medicamento = ?
        ORDER BY m.nome_medicamento;
        """;

        Medicamento medicamento = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    medicamento = new Medicamento();
                    medicamento.setId(resultSet.getLong("id_medicamento"));
                    medicamento.setNome(resultSet.getString("nome_medicamento"));
                }
            }
        }

        return medicamento;
    }
}
