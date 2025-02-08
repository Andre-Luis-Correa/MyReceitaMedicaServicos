package unioeste.geral.receitamedica.col;

import unioeste.geral.pessoa.bo.email.Email;

import java.util.List;
import java.util.regex.Pattern;

public class EmailCOL {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean emailValidos(List<Email> emails) {
        for (Email email : emails) {
            if (!emailValido(email)) {
                return false;
            }
        }
        return true;
    }

    private static boolean emailValido(Email email) {
        return email != null &&
                email.getEmail() != null &&
                !email.getEmail().trim().isEmpty() &&
                EMAIL_PATTERN.matcher(email.getEmail()).matches();
    }
}
