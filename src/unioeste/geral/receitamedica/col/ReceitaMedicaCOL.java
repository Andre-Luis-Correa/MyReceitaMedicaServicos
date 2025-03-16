package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.receitamedica.ReceitaMedica;

import java.time.LocalDate;

public class ReceitaMedicaCOL {

    public boolean receitaMedicaValida(ReceitaMedica receitaMedica) {
        return receitaMedica != null &&
                receitaMedica.getDataEmissao() != null &&
                receitaMedica.getMedico() != null &&
                receitaMedica.getPaciente() != null &&
                receitaMedica.getDiagnosticoCID() != null &&
                receitaMedica.getMedicamentoReceitaMedicas() != null &&
                !receitaMedica.getMedicamentoReceitaMedicas().isEmpty();
    }
}
