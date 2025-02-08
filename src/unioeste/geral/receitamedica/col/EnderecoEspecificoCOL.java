package unioeste.geral.receitamedica.col;

import unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import unioeste.geral.endereco.col.EnderecoCOL;

public class EnderecoEspecificoCOL {

    public static boolean enderecoEspecificoValido(EnderecoEspecifico enderecoEspecifico) throws Exception {
        return enderecoEspecifico != null &&
                enderecoEspecifico.getComplemento() != null &&
                !enderecoEspecifico.getComplemento().trim().isEmpty() &&
                enderecoEspecifico.getNumero() != null &&
                !enderecoEspecifico.getNumero().trim().isEmpty() &&
                enderecoEspecifico.getEndereco() != null &&
                EnderecoCOL.enderecoValido(enderecoEspecifico.getEndereco()) &&
                EnderecoCOL.enderecoExiste(enderecoEspecifico.getEndereco());
    }
}
