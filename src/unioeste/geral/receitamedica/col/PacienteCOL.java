package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.paciente.Paciente;

public class PacienteCOL {

    public static boolean idValido(Long id) {
        return id != null && id > 0;
    }

    public static boolean pacienteValido(Paciente paciente) {
        return paciente != null;
    }
}
