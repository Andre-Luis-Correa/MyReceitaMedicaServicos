package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.endereco.bo.cidade.Cidade;
import unioeste.geral.endereco.bo.endereco.Endereco;
import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.bo.logradouro.Logradouro;
import unioeste.geral.endereco.bo.tipologradouro.TipoLogradouro;
import unioeste.geral.endereco.bo.unidadefederativa.UnidadeFederativa;
import unioeste.geral.endereco.col.EnderecoCOL;
import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.endereco.service.UCEnderecoGeralServicos;
import unioeste.geral.pessoa.bo.cpf.CPF;
import unioeste.geral.pessoa.bo.sexo.Sexo;
import unioeste.geral.pessoa.dao.SexoDAO;
import unioeste.geral.receitamedica.bo.paciente.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacienteDAO {

    public Paciente inserirPaciente(Paciente paciente, Connection conexao) throws SQLException {
        String sql = "INSERT INTO paciente (nome_paciente, cpf_paciente, id_endereco, complemento_endereco, numero_endereco, sigla_sexo) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id_paciente";

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, paciente.getNome());
            preparedStatement.setString(2, paciente.getCpf().getCpf());
            preparedStatement.setLong(3, paciente.getEnderecoEspecifico().getEndereco().getId());
            preparedStatement.setString(4, paciente.getEnderecoEspecifico().getComplemento());
            preparedStatement.setString(5, paciente.getEnderecoEspecifico().getNumero());
            preparedStatement.setString(6, paciente.getSexo().getSigla());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    paciente.setId(resultSet.getLong("id_paciente"));
                    return paciente;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return null;
    }

    public Paciente selecionarPacientePorId(Long id, Connection conexao) throws Exception {
        String sql = "SELECT p.id_paciente, p.nome_paciente, p.cpf_paciente, p.complemento_endereco, p.numero_endereco, " +
                "s.sigla_sexo, s.nome AS nome_sexo, " +
                "e.id_endereco, e.cep, " +
                "l.id_logradouro, l.nome AS nome_logradouro, " +
                "tl.sigla_tipo_logradouro, tl.nome_tipo_logradouro, " +
                "c.id_cidade, c.nome AS nome_cidade, uf.sigla_uf, uf.nome_uf " +
                "FROM paciente p " +
                "JOIN sexo s ON p.sigla_sexo = s.sigla_sexo " +
                "JOIN endereco e ON p.id_endereco = e.id_endereco " +
                "JOIN logradouro l ON e.id_logradouro = l.id_logradouro " +
                "JOIN tipo_logradouro tl ON l.sigla_tipo_logradouro = tl.sigla_tipo_logradouro " +
                "JOIN cidade c ON e.id_cidade = c.id_cidade " +
                "JOIN unidade_federativa uf ON c.sigla_uf = uf.sigla_uf " +
                "WHERE p.id_paciente = ?";

        Paciente paciente = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    paciente = new Paciente();
                    paciente.setId(resultSet.getLong("id_paciente"));
                    paciente.setNome(resultSet.getString("nome_paciente"));

                    CPF cpf = new CPF();
                    cpf.setCpf(resultSet.getString("cpf_paciente"));
                    paciente.setCpf(cpf);

                    paciente.setPrimeiroNome(resultSet.getString("nome_paciente"));
                    paciente.setNomeMeio("");
                    paciente.setUltimoNome("");

                    Sexo sexo = new Sexo();
                    sexo.setSigla(resultSet.getString("sigla_sexo"));
                    sexo.setNome(resultSet.getString("nome_sexo"));
                    paciente.setSexo(sexo);

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
                    paciente.setEnderecoEspecifico(enderecoEspecifico);
                }
            }
        }

        return paciente;
    }

    public Paciente selecionarPacientePorCPF(String cpfPaciente, Connection conexao) throws SQLException {
        String sql = "SELECT p.id_paciente, p.nome_paciente, p.cpf_paciente, p.complemento_endereco, p.numero_endereco, " +
                "s.sigla_sexo, s.nome AS nome_sexo, " +
                "e.id_endereco, e.cep, " +
                "l.id_logradouro, l.nome AS nome_logradouro, " +
                "tl.sigla_tipo_logradouro, tl.nome_tipo_logradouro, " +
                "c.id_cidade, c.nome AS nome_cidade, uf.sigla_uf, uf.nome_uf " +
                "FROM paciente p " +
                "JOIN sexo s ON p.sigla_sexo = s.sigla_sexo " +
                "JOIN endereco e ON p.id_endereco = e.id_endereco " +
                "JOIN logradouro l ON e.id_logradouro = l.id_logradouro " +
                "JOIN tipo_logradouro tl ON l.sigla_tipo_logradouro = tl.sigla_tipo_logradouro " +
                "JOIN cidade c ON e.id_cidade = c.id_cidade " +
                "JOIN unidade_federativa uf ON c.sigla_uf = uf.sigla_uf " +
                "WHERE p.cpf_paciente = ?";

        Paciente paciente = null;

        try (PreparedStatement preparedStatement = conexao.prepareStatement(sql)) {
            preparedStatement.setString(1, cpfPaciente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    paciente = new Paciente();
                    paciente.setId(resultSet.getLong("id_paciente"));
                    paciente.setNome(resultSet.getString("nome_paciente"));

                    CPF cpf = new CPF();
                    cpf.setCpf(resultSet.getString("cpf_paciente"));
                    paciente.setCpf(cpf);

                    paciente.setPrimeiroNome(resultSet.getString("nome_paciente"));
                    paciente.setNomeMeio("");
                    paciente.setUltimoNome("");

                    Sexo sexo = new Sexo();
                    sexo.setSigla(resultSet.getString("sigla_sexo"));
                    sexo.setNome(resultSet.getString("nome_sexo"));
                    paciente.setSexo(sexo);

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
                    paciente.setEnderecoEspecifico(enderecoEspecifico);
                }
            }
        }

        return paciente;
    }
}
