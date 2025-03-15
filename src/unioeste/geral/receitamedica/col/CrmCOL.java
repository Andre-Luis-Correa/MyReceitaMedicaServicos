package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.crm.CRM;

public class CrmCOL {

    public boolean crmValido(CRM crm) {
        return crm != null && crm.getCrm() != null && !crm.getCrm().trim().isEmpty() && crm.getCrm().matches("^\\d{4,6}-[A-Z]{2}$");
    }
}
