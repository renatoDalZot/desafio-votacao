package com.renatodz.desafio_votacao.util;

import java.util.InputMismatchException;

public class UtilitariosAritmeticos {

    public static boolean isCPF(String CPF) {
        CPF = CPF.replaceAll("[^0-9]", "");

        if (CPF.length() != 11) {
            return false;
        }

        if (CPF.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int[] peso1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] peso2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (CPF.charAt(i) - '0') * peso1[i];
            }
            int digito1 = 11 - (soma % 11);
            digito1 = (digito1 > 9) ? 0 : digito1;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (CPF.charAt(i) - '0') * peso2[i];
            }
            int digito2 = 11 - (soma % 11);
            digito2 = (digito2 > 9) ? 0 : digito2;

            return (digito1 == CPF.charAt(9) - '0') && (digito2 == CPF.charAt(10) - '0');
        } catch (InputMismatchException e) {
            return false;
        }
    }
}
