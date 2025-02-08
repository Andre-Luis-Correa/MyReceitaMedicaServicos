package unioeste.geral.receitamedica.exception;

public class ReceitaMedicaException extends Exception {

    private static final long serialVersionUID = 1L;

    public ReceitaMedicaException(String errorMessage) {
        super(errorMessage);
    }
}
