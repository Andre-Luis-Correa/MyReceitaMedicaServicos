package unioeste.geral.receitamedica.col;

import unioeste.geral.pessoa.bo.cpf.CPF;

public class CPFCOL {

    public static boolean cpfValido(CPF cpf) {
        return cpf != null &&
                cpf.getCpf() != null &&
                !cpf.getCpf().trim().isEmpty() &&
                cpf.getCpf().matches("\\d{11}");
    }
}
