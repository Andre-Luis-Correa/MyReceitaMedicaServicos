package unioeste.geral.receitamedica.dao;

import unioeste.apoio.bd.ConexaoBD;
import unioeste.geral.pessoa.bo.ddi.DDI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DDIDao {
    public static DDI selectDDIPorNumero(Integer numero) throws Exception {
        String sql = "SELECT numero_ddi FROM ddi WHERE numero_ddi = ?";

        try (Connection conn = new ConexaoBD().getConexaoComBD();
             PreparedStatement cmd = conn.prepareStatement(sql)) {

            cmd.setInt(1, numero);
            try (ResultSet result = cmd.executeQuery()) {
                if (result.next()) {
                    return new DDI(result.getInt("numero_ddi"));
                }
            }

        } catch (Exception e) {
            throw new Exception("Erro ao buscar DDI pelo número: " + numero, e);
        }

        return null;
    }

    public static List<DDI> selecionarTodosDDI() throws Exception {
        List<DDI> ddiList = new ArrayList<>();
        String sql = "SELECT * FROM ddi;";

        try (Connection conn = new ConexaoBD().getConexaoComBD();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DDI ddi = new DDI(rs.getInt("numero_ddi"));
                ddiList.add(ddi);
            }

        } catch (Exception e) {
            throw new Exception("Erro ao buscar DDIs", e);
        }

        return ddiList;
    }
}
