// Paquete de utilidades para lectura de datos desde consola
package org.economix.util;

import org.economix.vista.acciones.Menu;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Clase singleton para lectura de datos del usuario desde consola.
 * Proporciona métodos seguros para leer enteros, decimales y textos.
 */
public class readUtil {

    // Escáner único para entrada estándar
    private static Scanner scanner;

    // Instancia única de esta clase (Singleton)
    private static readUtil readUtil;

    // Constructor privado para Singleton
    private readUtil() {
        scanner = new Scanner(System.in);
    }

    /**
     * Devuelve el escáner interno.
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Devuelve la instancia única de readUtil.
     */
    public static readUtil getInstance() {
        if (readUtil == null) {
            readUtil = new readUtil();
        }
        return readUtil;
    }

    /**
     * Lee una línea de texto desde consola.
     */
    public static String read() {
        return getInstance().getScanner().nextLine();
    }

    /**
     * Lee un valor entero, validando formato.
     * Reintenta hasta que el usuario escriba un número válido.
     */
    public static Integer readInt() {
        String valor;
        Integer aux;

        while (true) {
            valor = read();
            if (valor != null && !valor.isEmpty()) {
                try {
                    aux = Integer.valueOf(valor);
                    return aux;
                } catch (Exception e) {
                    Menu.errorDato(); // Muestra mensaje de error genérico
                }
            }
        }
    }

    /**
     * Lee un valor decimal (Double), asegurando que sea positivo.
     */
    public static Double readDouble() {
        String valor;
        Double aux;

        while (true) {
            valor = read();
            if (valor != null && !valor.isEmpty()) {
                try {
                    aux = Double.valueOf(valor);
                    if (aux >= 0) {
                        return aux;
                    } else {
                        System.out.println("> Error: Ingresa un número positivo.");
                    }
                } catch (Exception e) {
                    Menu.errorDato(); // Muestra mensaje de error
                }
            }
        }
    }

    /**
     * Convierte un String a Integer de forma segura.
     * devuelve null si no es posible convertir.
     */
    public static Integer string2Integer(String valor) {
        try {
            return Integer.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte un String a Double de forma segura.
     * devuelve null si no es posible convertir.
     */
    public static Double string2Double(String valor) {
        try {
            return Double.valueOf(valor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lee un valor BigDecimal desde consola, validando que tenga formato correcto.
     */
    public static BigDecimal readBigDecimal() {
        while (true) {
            String in = scanner.nextLine().trim();
            try {
                return new BigDecimal(in);
            } catch (NumberFormatException ex) {
                System.out.print("> Formato numérico inválido. Intenta de nuevo: ");
            }
        }
    }
}
