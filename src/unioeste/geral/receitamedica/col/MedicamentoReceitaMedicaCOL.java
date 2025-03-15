package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.medicamentoreceitamedica.MedicamentoReceitaMedica;

import java.util.List;

public class MedicamentoReceitaMedicaCOL {

    public boolean isMedicamentoReceitaMedicaValidos(List<MedicamentoReceitaMedica> medicamentoReceitaMedicaList) {
        for(MedicamentoReceitaMedica medicamentoReceitaMedica : medicamentoReceitaMedicaList) {
            if(medicamentoReceitaMedica.getMedicamento() == null ||
            medicamentoReceitaMedica.getDataInicio() == null ||
            medicamentoReceitaMedica.getDataFim() == null ||
            medicamentoReceitaMedica.getPosologia() == null ||
            medicamentoReceitaMedica.getPosologia().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
