package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.paciente.Paciente;

public class PacienteCOL {

    public boolean idValido(Long id) {
        return id != null && id > 0;
    }

    public boolean pacienteValido(Paciente paciente) {
        return paciente != null &&
                paciente.getNome() != null &&
                !paciente.getNome().trim().isEmpty() &&
                paciente.getEnderecoEspecifico() != null &&
                paciente.getTelefones() != null &&
                paciente.getEmails() != null &&
                paciente.getSexo() != null &&
                paciente.getCpf() != null;
    }
}
