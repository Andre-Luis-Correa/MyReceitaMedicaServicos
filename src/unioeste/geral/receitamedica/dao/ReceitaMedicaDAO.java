package unioeste.geral.receitamedica.dao;

import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.receitamedica.bo.crm.CRM;
import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.bo.medico.Medico;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.bo.receitamedica.ReceitaMedica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceitaMedicaDAO {

    public ReceitaMedica inserirReceitaMedica(ReceitaMedica receitaMedica, Connection conexao) throws SQLException {
        String sql = "INSERT INTO receita_medica (numero_receita, data_emissao, id_medico, codigo_cid_diagnostico, id_paciente) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING numero_receita";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setInt(1, receitaMedica.getNumero());
            preparedStatement.setDate(2, java.sql.Date.valueOf(receitaMedica.getDataEmissao()));
            preparedStatement.setLong(3, receitaMedica.getMedico().getId());
            preparedStatement.setString(4, receitaMedica.getDiagnosticoCID().getCodigo());
            preparedStatement.setLong(5, receitaMedica.getPaciente().getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    receitaMedica.setId(resultSet.getLong("numero_receita"));
                    receitaMedica.setNumero(resultSet.getInt("numero_receita"));
                    return receitaMedica;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    public ReceitaMedica obterReceitaMedicaPorNumero(int numeroReceita, Connection conexao) throws SQLException {
        String sql = "SELECT rm.numero_receita, rm.data_emissao, " +
                "m.id_medico, m.nome AS nome_medico, m.crm_medico, " +
                "p.id_paciente, p.nome_paciente, p.cpf_paciente, " +
                "d.codigo_cid_diagnostico, d.descricao_diagnostico_cid " +
                "FROM receita_medica rm " +
                "JOIN medico m ON rm.id_medico = m.id_medico " +
                "JOIN paciente p ON rm.id_paciente = p.id_paciente " +
                "JOIN diagnostico_cid d ON rm.codigo_cid_diagnostico = d.codigo_cid_diagnostico " +
                "WHERE rm.numero_receita = ?";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setInt(1, numeroReceita);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    ReceitaMedica receita = new ReceitaMedica();
                    receita.setNumero(resultSet.getInt("numero_receita"));
                    receita.setDataEmissao(resultSet.getDate("data_emissao").toLocalDate());

                    Medico medico = new Medico();
                    medico.setId(resultSet.getLong("id_medico"));
                    medico.setNome(resultSet.getString("nome_medico"));
                    medico.setCrm(new CRM(resultSet.getString("crm_medico")));
                    receita.setMedico(medico);

                    Paciente paciente = new Paciente();
                    paciente.setId(resultSet.getLong("id_paciente"));
                    paciente.setNome(resultSet.getString("nome_paciente"));
                    paciente.setCpf(new CPF(resultSet.getString("cpf_paciente")));
                    receita.setPaciente(paciente);

                    DiagnosticoCID diagnostico = new DiagnosticoCID();
                    diagnostico.setCodigo(resultSet.getString("codigo_cid_diagnostico"));
                    diagnostico.setDescricao(resultSet.getString("descricao_diagnostico_cid"));
                    receita.setDiagnosticoCID(diagnostico);

                    return receita;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }
}
