package unioeste.geral.receitamedica.col;

import unioeste.geral.pessoa.col.*;
import unioeste.geral.receitamedica.bo.paciente.Paciente;
import unioeste.geral.receitamedica.dao.PacienteDAO;

public class PacienteCOL {

    public static boolean idValido(Long id) {
        return id != null && id > 0;
    }

    public static boolean pacienteValido(Paciente paciente) throws Exception {
        return paciente != null &&
                paciente.getNome() != null &&
                !paciente.getNome().trim().isEmpty() &&
                EnderecoEspecificoCOL.enderecoEspecificoValido(paciente.getEnderecoEspecifico()) &&
                TelefoneCOL.telefonesValidos(paciente.getTelefones()) &&
                EmailCOL.emailValidos(paciente.getEmails()) &&
                SexoCOL.sexoValido(paciente.getSexo()) &&
                SexoCOL.sexoExiste(paciente.getSexo()) &&
                CPFCOL.cpfValido(paciente.getCpf());
    }

    public static boolean pacienteExiste(Paciente paciente) throws Exception {
        return paciente != null && PacienteDAO.selectPacientePorCpf(paciente.getCpf().getCpf()) != null;
    }
}
