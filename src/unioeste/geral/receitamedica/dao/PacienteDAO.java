package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

}
