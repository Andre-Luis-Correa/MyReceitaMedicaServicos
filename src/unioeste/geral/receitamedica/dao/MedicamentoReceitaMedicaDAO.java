package unioeste.geral.receitamedica.dao;

import unioeste.geral.receitamedica.bo.medicamento.Medicamento;
import unioeste.geral.receitamedica.bo.medicamentoreceitamedica.MedicamentoReceitaMedica;
import unioeste.geral.receitamedica.bo.receitamedica.ReceitaMedica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoReceitaMedicaDAO {

    public void inserirMedicamentoReceitaMedica(ReceitaMedica receitaMedica, List<MedicamentoReceitaMedica> medicamentoReceitaMedicaList, Connection conexao) throws SQLException {
        String sqlMedicamento = "INSERT INTO receita_medica_medicamento (id_medicamento, numero_receita, data_inicio_medicamento, data_termino_medicamento, posologia) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sqlMedicamento)) {
            for (MedicamentoReceitaMedica medicamento : medicamentoReceitaMedicaList) {
                preparedStatement.setLong(1, medicamento.getMedicamento().getId());
                preparedStatement.setLong(2, receitaMedica.getId());
                preparedStatement.setDate(3, java.sql.Date.valueOf(medicamento.getDataInicio()));
                preparedStatement.setDate(4, java.sql.Date.valueOf(medicamento.getDataFim()));
                preparedStatement.setString(5, medicamento.getPosologia());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }

    public List<MedicamentoReceitaMedica> obterMedicamentosPorReceita(int numeroReceita, Connection conexao) throws SQLException {
        String sql = "SELECT rmm.id_medicamento, rmm.numero_receita, rmm.data_inicio_medicamento, " +
                "rmm.data_termino_medicamento, rmm.posologia, m.nome_medicamento " +
                "FROM receita_medica_medicamento rmm " +
                "JOIN medicamento m ON rmm.id_medicamento = m.id_medicamento " +
                "WHERE rmm.numero_receita = ?";

        List<MedicamentoReceitaMedica> medicamentos = new ArrayList<>();

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setInt(1, numeroReceita);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    MedicamentoReceitaMedica medicamentoReceita = new MedicamentoReceitaMedica();

                    Medicamento medicamento = new Medicamento();
                    medicamento.setId(resultSet.getLong("id_medicamento"));
                    medicamento.setNome(resultSet.getString("nome_medicamento"));
                    medicamentoReceita.setMedicamento(medicamento);

                    medicamentoReceita.setDataInicio(resultSet.getDate("data_inicio_medicamento").toLocalDate());
                    medicamentoReceita.setDataFim(resultSet.getDate("data_termino_medicamento").toLocalDate());
                    medicamentoReceita.setPosologia(resultSet.getString("posologia"));

                    medicamentos.add(medicamentoReceita);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return medicamentos;
    }
}
