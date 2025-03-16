package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.medicamentoreceitamedica.MedicamentoReceitaMedica;

import java.time.LocalDate;
import java.util.List;

public class MedicamentoReceitaMedicaCOL {

    public boolean isMedicamentoReceitaMedicaValidos(MedicamentoReceitaMedica medicamentoReceitaMedica) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = medicamentoReceitaMedica.getDataInicio();
        LocalDate dataFim = medicamentoReceitaMedica.getDataFim();

        return medicamentoReceitaMedica.getMedicamento() != null &&
            medicamentoReceitaMedica.getDataInicio() != null &&
            medicamentoReceitaMedica.getDataFim() != null &&
            !dataInicio.isBefore(hoje) &&
            !dataFim.isBefore(dataInicio) &&
            medicamentoReceitaMedica.getPosologia() != null &&
            medicamentoReceitaMedica.getPosologia().trim().isEmpty();
    }

    public boolean isMedicamentoReceitaMedicaValidos(List<MedicamentoReceitaMedica> medicamentoReceitaMedicas) {
        return medicamentoReceitaMedicas != null && !medicamentoReceitaMedicas.isEmpty();
    }
}
