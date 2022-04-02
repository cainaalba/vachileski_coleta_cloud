package pneus.vachileski_mobi_funcoes_genericas;

public class ValidaCPFeCNPJ {

    public static boolean isDocValid(String documento) {
        boolean isDocValid = false;
        char digito10 = ' ', digito11 = ' ';
        int soma10 = 0, soma11 = 0, peso;

        if (documento.trim().length() == 11) {
            if ((documento.trim().equals("00000000000")) || (documento.trim().equals("11111111111")) ||
                    (documento.trim().equals("22222222222")) || (documento.trim().equals("33333333333")) ||
                    (documento.trim().equals("44444444444")) || (documento.trim().equals("55555555555")) ||
                    (documento.trim().equals("66666666666")) || (documento.trim().equals("77777777777")) ||
                    (documento.trim().equals("88888888888")) || (documento.trim().equals("99999999999"))) {

                isDocValid = false;
            } else {
                //DV 1
                digito10 = documento.trim().charAt(9);
                peso = documento.trim().length() - 1; // peso inicial 10 para CPF
                for (int i = 0; i < documento.trim().length() - 2; i++) {
                    soma10 += (peso * ((int) documento.trim().charAt(i) - 48));
                    peso--;
                }

                // DV 2
                digito11 = documento.trim().charAt(10);
                peso = documento.trim().length(); // peso inicial 11 para CPF
                for (int i = 0; i < documento.trim().length() - 1; i++) {
                    soma11 += (peso * ((int) documento.trim().charAt(i) - 48));
                    peso--;
                }
            }
        } else if (documento.trim().length() == 14) {
            if ((documento.trim().equals("00000000000000")) || (documento.trim().equals("11111111111111")) ||
                    (documento.trim().equals("22222222222222")) || (documento.trim().equals("33333333333333")) ||
                    (documento.trim().equals("44444444444444")) || (documento.trim().equals("55555555555555")) ||
                    (documento.trim().equals("66666666666666")) || (documento.trim().equals("77777777777777")) ||
                    (documento.trim().equals("88888888888888")) || (documento.trim().equals("99999999999999"))) {

                isDocValid = false;
            } else {
                digito10 = documento.trim().charAt(12);
                peso = documento.trim().length() - 9; // Peso inicial 5
                for (int i = 0; i < documento.trim().length() - 2; i++) {
                    soma10 += (peso * ((int) documento.trim().charAt(i) - 48));
                    peso--;
                    if (peso < 2) {
                        peso = 9;
                    }
                }

                digito11 = documento.trim().charAt(13);
                peso = documento.trim().length() - 8; // Peso inicial 6
                for (int i = 0; i < documento.trim().length() - 1; i++) {
                    soma11 += (peso * ((int) documento.trim().charAt(i) - 48));
                    peso--;
                    if (peso < 2) {
                        peso = 9;
                    }
                }
            }
        }

        //DV 1
        soma10 = 11 - (soma10 % 11);
        if (soma10 > 9) {
            soma10 = 0;
        }

        //DV 2
        soma11 = 11 - (soma11 % 11);
        if (soma11 > 9) {
            soma11 = 0;
        }

        if ((soma10 == digito10 - 48) && (soma11 == digito11 - 48)) {
            isDocValid = true;
        }

        return isDocValid;
    }
}
