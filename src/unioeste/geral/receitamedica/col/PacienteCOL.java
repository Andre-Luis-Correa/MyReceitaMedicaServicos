package unioeste.geral.receitamedica.col;

import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.dao.PacienteDAO;

public class PacienteCOL {

    public static boolean idValido(Long id) {
        return id != null && id > 0;
    }

    public static boolean pacienteValido(Paciente paciente) {
        return paciente != null;
    }

    public static boolean pacienteExiste(Paciente paciente) throws Exception {
        return paciente != null && PacienteDAO.selectPacientePorCpf(paciente.getCpf().getCpf()) != null;
    }
}
