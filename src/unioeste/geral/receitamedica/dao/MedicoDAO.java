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
import unioeste.geral.receitamedica.bo.medico.Medico;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicoDAO {

    public Medico inserirMedico(Medico medico, Connection conexao) throws SQLException {
        String sql = "INSERT INTO medico (nome, cpf_medico, id_endereco, complemento_endereco, numero_endereco, sigla_sexo, crm_medico) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_medico";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, medico.getNome());
            preparedStatement.setString(2, medico.getCpf().getCpf());
            preparedStatement.setLong(3, medico.getEnderecoEspecifico().getEndereco().getId());
            preparedStatement.setString(4, medico.getEnderecoEspecifico().getComplemento());
            preparedStatement.setString(5, medico.getEnderecoEspecifico().getNumero());
            preparedStatement.setString(6, medico.getSexo().getSigla());
            preparedStatement.setString(7, medico.getCrm().getCrm());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    medico.setId(resultSet.getLong("id_medico"));
                    return medico;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    public Medico selecionarMedicoPorId(Long id, Connection conexao) throws Exception {
        String sql = "SELECT m.id_medico, m.nome, m.cpf_medico, m.crm_medico, m.complemento_endereco, m.numero_endereco, " +
                "s.sigla_sexo, s.nome AS nome_sexo, " +
                "e.id_endereco, e.cep, " +
                "l.id_logradouro, l.nome AS nome_logradouro, " +
                "tl.sigla_tipo_logradouro, tl.nome_tipo_logradouro, " +
                "c.id_cidade, c.nome AS nome_cidade, uf.sigla_uf, uf.nome_uf " +
                "FROM medico m " +
                "JOIN sexo s ON m.sigla_sexo = s.sigla_sexo " +
                "JOIN endereco e ON m.id_endereco = e.id_endereco " +
                "JOIN logradouro l ON e.id_logradouro = l.id_logradouro " +
                "JOIN tipo_logradouro tl ON l.sigla_tipo_logradouro = tl.sigla_tipo_logradouro " +
                "JOIN cidade c ON e.id_cidade = c.id_cidade " +
                "JOIN unidade_federativa uf ON c.sigla_uf = uf.sigla_uf " +
                "WHERE m.id_medico = ?";

        Medico medico = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    medico = new Medico();
                    medico.setId(resultSet.getLong("id_medico"));
                    medico.setNome(resultSet.getString("nome"));

                    CPF cpf = new CPF();
                    cpf.setCpf(resultSet.getString("cpf_medico"));
                    medico.setCpf(cpf);

                    CRM crm = new CRM();
                    crm.setCrm(resultSet.getString("crm_medico"));
                    medico.setCrm(crm);

                    medico.setPrimeiroNome(resultSet.getString("nome"));
                    medico.setNomeMeio("");
                    medico.setUltimoNome("");

                    Sexo sexo = new Sexo();
                    sexo.setSigla(resultSet.getString("sigla_sexo"));
                    sexo.setNome(resultSet.getString("nome_sexo"));
                    medico.setSexo(sexo);

                    EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico();
                    enderecoEspecifico.setNumero(resultSet.getString("numero_endereco"));
                    enderecoEspecifico.setComplemento(resultSet.getString("complemento_endereco"));

                    Endereco endereco = new Endereco();
                    endereco.setId(resultSet.getLong("id_endereco"));
                    endereco.setCep(resultSet.getString("cep"));

                    Logradouro logradouro = new Logradouro();
                    logradouro.setId(resultSet.getLong("id_logradouro"));
                    logradouro.setNome(resultSet.getString("nome_logradouro"));

                    TipoLogradouro tipoLogradouro = new TipoLogradouro();
                    tipoLogradouro.setSigla(resultSet.getString("sigla_tipo_logradouro"));
                    tipoLogradouro.setNome(resultSet.getString("nome_tipo_logradouro"));
                    logradouro.setTipoLogradouro(tipoLogradouro);

                    endereco.setLogradouro(logradouro);

                    Cidade cidade = new Cidade();
                    cidade.setId(resultSet.getLong("id_cidade"));
                    cidade.setNome(resultSet.getString("nome_cidade"));

                    UnidadeFederativa uf = new UnidadeFederativa();
                    uf.setSigla(resultSet.getString("sigla_uf"));
                    uf.setNome(resultSet.getString("nome_uf"));
                    cidade.setUnidadeFederativa(uf);

                    endereco.setCidade(cidade);
                    enderecoEspecifico.setEndereco(endereco);
                    medico.setEnderecoEspecifico(enderecoEspecifico);
                }
            }
        }

        return medico;
    }

    public Medico selecionarMedicoPorCPF(String cpfMedico, Connection conexao) throws SQLException {
        String sql = "SELECT m.id_medico, m.nome, m.cpf_medico, m.crm_medico, m.complemento_endereco, m.numero_endereco, " +
                "s.sigla_sexo, s.nome AS nome_sexo, " +
                "e.id_endereco, e.cep, " +
                "l.id_logradouro, l.nome AS nome_logradouro, " +
                "tl.sigla_tipo_logradouro, tl.nome_tipo_logradouro, " +
                "c.id_cidade, c.nome AS nome_cidade, uf.sigla_uf, uf.nome_uf " +
                "FROM medico m " +
                "JOIN sexo s ON m.sigla_sexo = s.sigla_sexo " +
                "JOIN endereco e ON m.id_endereco = e.id_endereco " +
                "JOIN logradouro l ON e.id_logradouro = l.id_logradouro " +
                "JOIN tipo_logradouro tl ON l.sigla_tipo_logradouro = tl.sigla_tipo_logradouro " +
                "JOIN cidade c ON e.id_cidade = c.id_cidade " +
                "JOIN unidade_federativa uf ON c.sigla_uf = uf.sigla_uf " +
                "WHERE m.cpf_medico = ?";

        Medico medico = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, cpfMedico);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    medico = new Medico();
                    medico.setId(resultSet.getLong("id_medico"));
                    medico.setNome(resultSet.getString("nome"));

                    CPF cpf = new CPF();
                    cpf.setCpf(resultSet.getString("cpf_medico"));
                    medico.setCpf(cpf);

                    CRM crm = new CRM();
                    crm.setCrm(resultSet.getString("crm_medico"));
                    medico.setCrm(crm);

                    medico.setPrimeiroNome(resultSet.getString("nome"));
                    medico.setNomeMeio("");
                    medico.setUltimoNome("");

                    Sexo sexo = new Sexo();
                    sexo.setSigla(resultSet.getString("sigla_sexo"));
                    sexo.setNome(resultSet.getString("nome_sexo"));
                    medico.setSexo(sexo);

                    EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico();
                    enderecoEspecifico.setNumero(resultSet.getString("numero_endereco"));
                    enderecoEspecifico.setComplemento(resultSet.getString("complemento_endereco"));

                    Endereco endereco = new Endereco();
                    endereco.setId(resultSet.getLong("id_endereco"));
                    endereco.setCep(resultSet.getString("cep"));

                    Logradouro logradouro = new Logradouro();
                    logradouro.setId(resultSet.getLong("id_logradouro"));
                    logradouro.setNome(resultSet.getString("nome_logradouro"));

                    TipoLogradouro tipoLogradouro = new TipoLogradouro();
                    tipoLogradouro.setSigla(resultSet.getString("sigla_tipo_logradouro"));
                    tipoLogradouro.setNome(resultSet.getString("nome_tipo_logradouro"));
                    logradouro.setTipoLogradouro(tipoLogradouro);

                    endereco.setLogradouro(logradouro);

                    Cidade cidade = new Cidade();
                    cidade.setId(resultSet.getLong("id_cidade"));
                    cidade.setNome(resultSet.getString("nome_cidade"));

                    UnidadeFederativa uf = new UnidadeFederativa();
                    uf.setSigla(resultSet.getString("sigla_uf"));
                    uf.setNome(resultSet.getString("nome_uf"));
                    cidade.setUnidadeFederativa(uf);

                    endereco.setCidade(cidade);
                    enderecoEspecifico.setEndereco(endereco);
                    medico.setEnderecoEspecifico(enderecoEspecifico);
                }
            }
        }

        return medico;
    }

    public Medico selecionarMedicoPorCRM(String crmPaciente, Connection conexao) throws SQLException {
        String sql = "SELECT m.id_medico, m.nome, m.cpf_medico, m.crm_medico, m.complemento_endereco, m.numero_endereco, " +
                "s.sigla_sexo, s.nome AS nome_sexo, " +
                "e.id_endereco, e.cep, " +
                "l.id_logradouro, l.nome AS nome_logradouro, " +
                "tl.sigla_tipo_logradouro, tl.nome_tipo_logradouro, " +
                "c.id_cidade, c.nome AS nome_cidade, uf.sigla_uf, uf.nome_uf " +
                "FROM medico m " +
                "JOIN sexo s ON m.sigla_sexo = s.sigla_sexo " +
                "JOIN endereco e ON m.id_endereco = e.id_endereco " +
                "JOIN logradouro l ON e.id_logradouro = l.id_logradouro " +
                "JOIN tipo_logradouro tl ON l.sigla_tipo_logradouro = tl.sigla_tipo_logradouro " +
                "JOIN cidade c ON e.id_cidade = c.id_cidade " +
                "JOIN unidade_federativa uf ON c.sigla_uf = uf.sigla_uf " +
                "WHERE m.crm_medico = ?";

        Medico medico = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, crmPaciente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    medico = new Medico();
                    medico.setId(resultSet.getLong("id_medico"));
                    medico.setNome(resultSet.getString("nome"));

                    CPF cpf = new CPF();
                    cpf.setCpf(resultSet.getString("cpf_medico"));
                    medico.setCpf(cpf);

                    CRM crm = new CRM();
                    crm.setCrm(resultSet.getString("crm_medico"));
                    medico.setCrm(crm);

                    medico.setPrimeiroNome(resultSet.getString("nome"));
                    medico.setNomeMeio("");
                    medico.setUltimoNome("");

                    Sexo sexo = new Sexo();
                    sexo.setSigla(resultSet.getString("sigla_sexo"));
                    sexo.setNome(resultSet.getString("nome_sexo"));
                    medico.setSexo(sexo);

                    EnderecoEspecifico enderecoEspecifico = new EnderecoEspecifico();
                    enderecoEspecifico.setNumero(resultSet.getString("numero_endereco"));
                    enderecoEspecifico.setComplemento(resultSet.getString("complemento_endereco"));

                    Endereco endereco = new Endereco();
                    endereco.setId(resultSet.getLong("id_endereco"));
                    endereco.setCep(resultSet.getString("cep"));

                    Logradouro logradouro = new Logradouro();
                    logradouro.setId(resultSet.getLong("id_logradouro"));
                    logradouro.setNome(resultSet.getString("nome_logradouro"));

                    TipoLogradouro tipoLogradouro = new TipoLogradouro();
                    tipoLogradouro.setSigla(resultSet.getString("sigla_tipo_logradouro"));
                    tipoLogradouro.setNome(resultSet.getString("nome_tipo_logradouro"));
                    logradouro.setTipoLogradouro(tipoLogradouro);

                    endereco.setLogradouro(logradouro);

                    Cidade cidade = new Cidade();
                    cidade.setId(resultSet.getLong("id_cidade"));
                    cidade.setNome(resultSet.getString("nome_cidade"));

                    UnidadeFederativa uf = new UnidadeFederativa();
                    uf.setSigla(resultSet.getString("sigla_uf"));
                    uf.setNome(resultSet.getString("nome_uf"));
                    cidade.setUnidadeFederativa(uf);

                    endereco.setCidade(cidade);
                    enderecoEspecifico.setEndereco(endereco);
                    medico.setEnderecoEspecifico(enderecoEspecifico);
                }
            }
        }

        return medico;
    }
}
