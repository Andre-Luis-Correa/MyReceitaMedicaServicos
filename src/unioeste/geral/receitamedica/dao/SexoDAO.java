package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.sexo.Sexo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SexoDAO {

    public static Sexo selectSexoPorSigla(String sigla) throws Exception {
        String sql = "SELECT nome FROM sexo WHERE sigla_sexo = ?";

        try (Connection conexaoBD = new ConexaoBD().getConexaoComBD();
             PreparedStatement cmd = conexaoBD.prepareStatement(sql)) {

            cmd.setString(1, sigla);
            try (ResultSet result = cmd.executeQuery()) {
                if (result.next()) {
                    return new Sexo(sigla, result.getString("nome"));
                }
            }
        } catch (Exception e) {
            throw new Exception("Erro ao buscar sexo pela sigla: " + sigla, e);
        }

        return null;
    }
}
