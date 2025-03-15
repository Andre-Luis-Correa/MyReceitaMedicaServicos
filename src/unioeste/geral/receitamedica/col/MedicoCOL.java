package unioeste.geral.receitamedica.col;

import unioeste.geral.receitamedica.bo.medico.Medico;

public class MedicoCOL {

    public boolean idValido(Long id) {
        return id != null && id > 0;
    }

    public boolean medicoValido(Medico medico) {
        return medico != null &&
                medico.getNome() != null &&
                !medico.getNome().trim().isEmpty() &&
                medico.getEnderecoEspecifico() != null &&
                medico.getTelefones() != null &&
                medico.getEmails() != null &&
                medico.getSexo() != null &&
                medico.getCpf() != null &&
                medico.getCrm() != null;
    }
}
