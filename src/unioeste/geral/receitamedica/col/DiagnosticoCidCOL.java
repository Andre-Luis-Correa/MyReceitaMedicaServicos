package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.diagnosticocid.DiagnosticoCID;

public class DiagnosticoCidCOL {

    public boolean diagnosticoCIDValido(DiagnosticoCID diagnosticoCID) {
        return diagnosticoCID != null &&
                diagnosticoCID.getCodigo() != null &&
                !diagnosticoCID.getCodigo().trim().isEmpty() &&
                diagnosticoCID.getCodigo().matches("^[A-Z]\\d{2}(\\.\\d)?$") &&
                diagnosticoCID.getDescricao() != null &&
                !diagnosticoCID.getDescricao().trim().isEmpty();
    }

}
