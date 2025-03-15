package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.medicamento.Medicamento;

public class MedicamentoCOL {

    public boolean medicamentoValido(Medicamento medicamento) {
        return medicamento != null &&
                medicamento.getNome() != null &&
                !medicamento.getNome().trim().isEmpty();
    }

}
