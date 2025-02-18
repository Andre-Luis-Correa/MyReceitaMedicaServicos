package unioeste.geral.receitamedica.service;

import unioeste.geral.pessoa.bo.ddd.DDD;
import unioeste.geral.pessoa.bo.ddi.DDI;
import unioeste.geral.pessoa.bo.sexo.Sexo;
import unioeste.geral.receitamedica.dao.DDDDao;
import unioeste.geral.receitamedica.dao.DDIDao;
import unioeste.geral.receitamedica.dao.SexoDAO;

import java.util.List;

public class ServicosUteisGeral {

    public ServicosUteisGeral() {

    }

    public static List<DDD> obterTodosDDD() throws Exception {
        return DDDDao.selecionarTodosDDD();
    }

    public static List<DDI> obterTodosDDI() throws Exception {
        return DDIDao.selecionarTodosDDI();
    }

    public static List<Sexo> obterTodosSexo() throws Exception {
        return SexoDAO.selecionarTodosSexo();
    }

}
