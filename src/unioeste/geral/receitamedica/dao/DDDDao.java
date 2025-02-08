package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.ddd.DDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DDDDao {

    public static DDD selectDDDPorNumero(Integer numero) throws Exception {
        String sql = "SELECT numero_ddd FROM ddd WHERE numero_ddd = ?";

        try (Connection conn = new ConexaoBD().getConexaoComBD();
             PreparedStatement cmd = conn.prepareStatement(sql)) {

            cmd.setInt(1, numero);
            try (ResultSet result = cmd.executeQuery()) {
                if (result.next()) {
                    return new DDD(result.getInt("numero_ddd"));
                }
            }

        } catch (Exception e) {
            throw new Exception("Erro ao buscar DDD pelo número: " + numero, e);
        }

        return null;
    }
}
