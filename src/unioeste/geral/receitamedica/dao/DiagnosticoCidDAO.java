package unioeste.geral.receitamedica.dao;

import unioeste.geral.endereco.bo.unidadefederativa.UnidadeFederativa;
import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.bo.medico.Medico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoCidDAO {

    public DiagnosticoCID inserirDiagnosticoCID(DiagnosticoCID diagnosticoCID, Connection conexao) throws SQLException {
        String sql = "INSERT INTO diagnostico_cid (codigo_cid_diagnostico, descricao_diagnostico_cid) " +
                "VALUES (?, ?) RETURNING codigo_cid_diagnostico";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, diagnosticoCID.getCodigo());
            preparedStatement.setString(2, diagnosticoCID.getDescricao());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    diagnosticoCID.setCodigo(resultSet.getString("codigo_cid_diagnostico"));
                    return diagnosticoCID;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<DiagnosticoCID> selecionarTodosDiagnosticoCID(Connection conexao) throws SQLException {
        String sql = """
        SELECT d.codigo_cid_diagnostico, d.descricao_diagnostico_cid
        FROM diagnostico_cid d
        ORDER BY d.codigo_cid_diagnostico;
    """;

        List<DiagnosticoCID> diagnosticoCIDS = new ArrayList<>();

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                DiagnosticoCID cid = new DiagnosticoCID();
                cid.setCodigo(resultSet.getString("codigo_cid_diagnostico"));
                cid.setDescricao(resultSet.getString("descricao_diagnostico_cid"));

                diagnosticoCIDS.add(cid);
            }
        }

        return diagnosticoCIDS;
    }

    public DiagnosticoCID selecionarDiagnosticoCIDPorCodigo(String codigo, Connection conexao) throws SQLException {
        String sql = """
        SELECT d.codigo_cid_diagnostico, d.descricao_diagnostico_cid
        FROM diagnostico_cid d
        WHERE d.codigo_cid_diagnostico = ?
        ORDER BY d.codigo_cid_diagnostico;
        """;

        DiagnosticoCID diagnosticoCID = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, codigo);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    diagnosticoCID = new DiagnosticoCID();
                    diagnosticoCID.setCodigo(resultSet.getString("codigo_cid_diagnostico"));
                    diagnosticoCID.setDescricao(resultSet.getString("descricao_diagnostico_cid"));
                }
            }
        }

        return diagnosticoCID;
    }
}
