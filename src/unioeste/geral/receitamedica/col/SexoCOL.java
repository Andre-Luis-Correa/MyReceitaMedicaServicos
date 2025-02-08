package unioeste.geral.receitamedica.col;

import unioeste.geral.pessoa.bo.sexo.Sexo;
import unioeste.geral.receitamedica.dao.SexoDAO;

public class SexoCOL {

    public static boolean sexoValido(Sexo sexo) {
        return sexo != null &&
                sexo.getSigla() != null &&
                !sexo.getSigla().trim().isEmpty() &&
                sexo.getNome() != null &&
                !sexo.getNome().trim().isEmpty();
    }

    public static boolean sexoExiste(Sexo sexo) throws Exception {
        return sexo != null && sexo.getSigla() != null && SexoDAO.selectSexoPorSigla(sexo.getSigla()) != null;
    }
}
