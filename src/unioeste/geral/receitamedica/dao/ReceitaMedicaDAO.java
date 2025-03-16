package unioeste.geral.receitamedica.dao;

import unioeste.geral.endereco.bo.cidade.Cidade;
import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.bo.logradouro.Logradouro;
import unioeste.geral.endereco.bo.tipologradouro.TipoLogradouro;
import unioeste.geral.endereco.bo.unidadefederativa.UnidadeFederativa;
import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.pessoa.bo.sexo.Sexo;
import unioeste.geral.receitamedica.bo.crm.CRM;
import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;
import unioeste.geral.receitamedica.bo.medico.Medico;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.bo.receitamedica.ReceitaMedica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class ReceitaMedicaDAO {

    public ReceitaMedica inserirReceitaMedica(ReceitaMedica receitaMedica, Connection conexao) throws SQLException {
        String sql = "INSERT INTO receita_medica (data_emissao, id_medico, codigo_cid_diagnostico, id_paciente) " +
                "VALUES (?, ?, ?, ?) RETURNING numero_receita";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(receitaMedica.getDataEmissao()));
            preparedStatement.setLong(2, receitaMedica.getMedico().getId());
            preparedStatement.setString(3, receitaMedica.getDiagnosticoCID().getCodigo());
            preparedStatement.setLong(4, receitaMedica.getPaciente().getId());

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

    public ReceitaMedica selecionarReceitaMedicaPorNumero(int numeroReceita, Connection conexao) throws SQLException {
        String sql = """
                SELECT
                    rm.numero_receita,
                    rm.data_emissao,
                    d.codigo_cid_diagnostico,
                    d.descricao_diagnostico_cid,

                    -- Campos do médico
                    m.id_medico,
                    m.nome AS nome_medico,
                    m.cpf_medico,
                    m.crm_medico,
                    m.complemento_endereco AS complemento_endereco_medico,
                    m.numero_endereco AS numero_endereco_medico,
                    sm.sigla_sexo AS sigla_sexo_medico,
                    sm.nome AS nome_sexo_medico,
                    em.id_endereco AS id_endereco_medico,
                    em.cep AS cep_medico,
                    lom.id_logradouro AS id_logradouro_medico,
                    lom.nome AS nome_logradouro_medico,
                    tlm.sigla_tipo_logradouro AS sigla_tipo_logradouro_medico,
                    tlm.nome_tipo_logradouro AS nome_tipo_logradouro_medico,
                    cm.id_cidade AS id_cidade_medico,
                    cm.nome AS nome_cidade_medico,
                    ufm.sigla_uf AS sigla_uf_medico,
                    ufm.nome_uf AS nome_uf_medico,

                    -- Campos do paciente
                    p.id_paciente,
                    p.nome_paciente,
                    p.cpf_paciente,
                    p.complemento_endereco AS complemento_endereco_paciente,
                    p.numero_endereco AS numero_endereco_paciente,
                    sp.sigla_sexo AS sigla_sexo_paciente,
                    sp.nome AS nome_sexo_paciente,
                    ep.id_endereco AS id_endereco_paciente,
                    ep.cep AS cep_paciente,
                    lop.id_logradouro AS id_logradouro_paciente,
                    lop.nome AS nome_logradouro_paciente,
                    tlp.sigla_tipo_logradouro AS sigla_tipo_logradouro_paciente,
                    tlp.nome_tipo_logradouro AS nome_tipo_logradouro_paciente,
                    cp.id_cidade AS id_cidade_paciente,
                    cp.nome AS nome_cidade_paciente,
                    ufp.sigla_uf AS sigla_uf_paciente,
                    ufp.nome_uf AS nome_uf_paciente

                FROM receita_medica rm
                JOIN diagnostico_cid d ON rm.codigo_cid_diagnostico = d.codigo_cid_diagnostico
                JOIN medico m ON rm.id_medico = m.id_medico
                JOIN sexo sm ON m.sigla_sexo = sm.sigla_sexo
                JOIN endereco em ON m.id_endereco = em.id_endereco
                JOIN logradouro lom ON em.id_logradouro = lom.id_logradouro
                JOIN tipo_logradouro tlm ON lom.sigla_tipo_logradouro = tlm.sigla_tipo_logradouro
                JOIN cidade cm ON em.id_cidade = cm.id_cidade
                JOIN unidade_federativa ufm ON cm.sigla_uf = ufm.sigla_uf
                JOIN paciente p ON rm.id_paciente = p.id_paciente
                JOIN sexo sp ON p.sigla_sexo = sp.sigla_sexo
                JOIN endereco ep ON p.id_endereco = ep.id_endereco
                JOIN logradouro lop ON ep.id_logradouro = lop.id_logradouro
                JOIN tipo_logradouro tlp ON lop.sigla_tipo_logradouro = tlp.sigla_tipo_logradouro
                JOIN cidade cp ON ep.id_cidade = cp.id_cidade
                JOIN unidade_federativa ufp ON cp.sigla_uf = ufp.sigla_uf
                WHERE rm.numero_receita = ?
                        """;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setInt(1, numeroReceita);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Monta objeto principal da Receita
                    ReceitaMedica receita = new ReceitaMedica();
                    receita.setNumero(resultSet.getInt("numero_receita"));
                    receita.setDataEmissao(resultSet.getDate("data_emissao").toLocalDate());

                    // Diagnóstico
                    DiagnosticoCID diagnostico = new DiagnosticoCID();
                    diagnostico.setCodigo(resultSet.getString("codigo_cid_diagnostico"));
                    diagnostico.setDescricao(resultSet.getString("descricao_diagnostico_cid"));
                    receita.setDiagnosticoCID(diagnostico);

                    // Monta objeto Medico
                    Medico medico = new Medico();
                    medico.setId(resultSet.getLong("id_medico"));
                    medico.setNome(resultSet.getString("nome_medico"));

                    CPF cpfMedico = new CPF();
                    cpfMedico.setCpf(resultSet.getString("cpf_medico"));
                    medico.setCpf(cpfMedico);

                    CRM crmMedico = new CRM();
                    crmMedico.setCrm(resultSet.getString("crm_medico"));
                    medico.setCrm(crmMedico);

                    // Exemplo de manipular nome completo, se desejar
                    medico.setPrimeiroNome(resultSet.getString("nome_medico"));
                    medico.setNomeMeio("");
                    medico.setUltimoNome("");

                    // Sexo do médico
                    Sexo sexoMedico = new Sexo();
                    sexoMedico.setSigla(resultSet.getString("sigla_sexo_medico"));
                    sexoMedico.setNome(resultSet.getString("nome_sexo_medico"));
                    medico.setSexo(sexoMedico);

                    // Endereço específico do médico
                    EnderecoEspecifico enderecoEspecificoMedico = new EnderecoEspecifico();
                    enderecoEspecificoMedico.setNumero(resultSet.getString("numero_endereco_medico"));
                    enderecoEspecificoMedico.setComplemento(resultSet.getString("complemento_endereco_medico"));

                    Endereco enderecoMedico = new Endereco();
                    enderecoMedico.setId(resultSet.getLong("id_endereco_medico"));
                    enderecoMedico.setCep(resultSet.getString("cep_medico"));

                    Logradouro logradouroMedico = new Logradouro();
                    logradouroMedico.setId(resultSet.getLong("id_logradouro_medico"));
                    logradouroMedico.setNome(resultSet.getString("nome_logradouro_medico"));

                    TipoLogradouro tipoLogradouroMedico = new TipoLogradouro();
                    tipoLogradouroMedico.setSigla(resultSet.getString("sigla_tipo_logradouro_medico"));
                    tipoLogradouroMedico.setNome(resultSet.getString("nome_tipo_logradouro_medico"));
                    logradouroMedico.setTipoLogradouro(tipoLogradouroMedico);

                    enderecoMedico.setLogradouro(logradouroMedico);

                    Cidade cidadeMedico = new Cidade();
                    cidadeMedico.setId(resultSet.getLong("id_cidade_medico"));
                    cidadeMedico.setNome(resultSet.getString("nome_cidade_medico"));

                    UnidadeFederativa ufMedico = new UnidadeFederativa();
                    ufMedico.setSigla(resultSet.getString("sigla_uf_medico"));
                    ufMedico.setNome(resultSet.getString("nome_uf_medico"));
                    cidadeMedico.setUnidadeFederativa(ufMedico);

                    enderecoMedico.setCidade(cidadeMedico);
                    enderecoEspecificoMedico.setEndereco(enderecoMedico);
                    medico.setEnderecoEspecifico(enderecoEspecificoMedico);

                    // Atribui o médico à receita
                    receita.setMedico(medico);

                    // Monta objeto Paciente
                    Paciente paciente = new Paciente();
                    paciente.setId(resultSet.getLong("id_paciente"));
                    paciente.setNome(resultSet.getString("nome_paciente"));

                    CPF cpfPaciente = new CPF();
                    cpfPaciente.setCpf(resultSet.getString("cpf_paciente"));
                    paciente.setCpf(cpfPaciente);

                    paciente.setPrimeiroNome(resultSet.getString("nome_paciente"));
                    paciente.setNomeMeio("");
                    paciente.setUltimoNome("");

                    // Sexo do paciente
                    Sexo sexoPaciente = new Sexo();
                    sexoPaciente.setSigla(resultSet.getString("sigla_sexo_paciente"));
                    sexoPaciente.setNome(resultSet.getString("nome_sexo_paciente"));
                    paciente.setSexo(sexoPaciente);

                    // Endereço do paciente
                    EnderecoEspecifico enderecoEspecificoPaciente = new EnderecoEspecifico();
                    enderecoEspecificoPaciente.setNumero(resultSet.getString("numero_endereco_paciente"));
                    enderecoEspecificoPaciente.setComplemento(resultSet.getString("complemento_endereco_paciente"));

                    Endereco enderecoPaciente = new Endereco();
                    enderecoPaciente.setId(resultSet.getLong("id_endereco_paciente"));
                    enderecoPaciente.setCep(resultSet.getString("cep_paciente"));

                    Logradouro logradouroPaciente = new Logradouro();
                    logradouroPaciente.setId(resultSet.getLong("id_logradouro_paciente"));
                    logradouroPaciente.setNome(resultSet.getString("nome_logradouro_paciente"));

                    TipoLogradouro tipoLogradouroPaciente = new TipoLogradouro();
                    tipoLogradouroPaciente.setSigla(resultSet.getString("sigla_tipo_logradouro_paciente"));
                    tipoLogradouroPaciente.setNome(resultSet.getString("nome_tipo_logradouro_paciente"));
                    logradouroPaciente.setTipoLogradouro(tipoLogradouroPaciente);

                    enderecoPaciente.setLogradouro(logradouroPaciente);

                    Cidade cidadePaciente = new Cidade();
                    cidadePaciente.setId(resultSet.getLong("id_cidade_paciente"));
                    cidadePaciente.setNome(resultSet.getString("nome_cidade_paciente"));

                    UnidadeFederativa ufPaciente = new UnidadeFederativa();
                    ufPaciente.setSigla(resultSet.getString("sigla_uf_paciente"));
                    ufPaciente.setNome(resultSet.getString("nome_uf_paciente"));
                    cidadePaciente.setUnidadeFederativa(ufPaciente);

                    enderecoPaciente.setCidade(cidadePaciente);
                    enderecoEspecificoPaciente.setEndereco(enderecoPaciente);
                    paciente.setEnderecoEspecifico(enderecoEspecificoPaciente);

                    // Atribui o paciente à receita
                    receita.setPaciente(paciente);

                    // Retorna a receita já montada
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
