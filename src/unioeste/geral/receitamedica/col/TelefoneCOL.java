package unioeste.geral.receitamedica.col;

import unioeste.geral.pessoa.bo.telefone.Telefone;

import java.util.List;

public class TelefoneCOL {

    private static boolean numeroValido(String numero) {
        return numero != null && !numero.trim().isEmpty() && numero.length() <= 15;
    }

    public static boolean telefonesValidos(List<Telefone> telefones) throws Exception {
        for(Telefone telefone : telefones) {
            if(!telefoneValido(telefone)) {
                return false;
            }
        }
        return true;
    }

    private static boolean telefoneValido(Telefone telefone) throws Exception {
        return telefone != null &&
                numeroValido(telefone.getNumero()) &&
                DDDCOL.dddValido(telefone.getDdd()) &&
                DDDCOL.dddExiste(telefone.getDdd()) &&
                DDICOL.ddiValido(telefone.getDdi()) &&
                DDICOL.ddiExiste(telefone.getDdi());
    }
}
