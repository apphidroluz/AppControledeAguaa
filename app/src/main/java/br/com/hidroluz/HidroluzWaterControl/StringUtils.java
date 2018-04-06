package br.com.hidroluz.HidroluzWaterControl;

import android.widget.EditText;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static Boolean validaNome(EditText u) {
        return Boolean.valueOf(Pattern.compile("[A-Z a-z]{2,35}").matcher(u.getText().toString()).matches());
    }

    public static Boolean validaEmail(EditText u) {
        return Boolean.valueOf(Pattern.compile(".+@.+\\.[a-z]+").matcher(u.getText().toString()).matches());
    }

    public static Boolean validaTelefone(String u) {
        return Boolean.valueOf(Pattern.compile("[0-9]{8,11}").matcher(u).matches());
    }
    
    

    public static boolean filter_nome(EditText edt) {
        if (validaNome(edt).booleanValue()) {
            return true;
        }
        edt.setError("Campo Invalido");
        return false;
    }

    public static boolean filter_email(EditText edt) {
        if (validaEmail(edt).booleanValue()) {
            return true;
        }
        edt.setError("Campo Invalido");
        return false;
    }

    public static boolean filter_telefone(String edt) {
        if (validaTelefone(edt).booleanValue()) {
            return true;
        }
         return false;
    }
}
