package org.economix.Inicio;

import org.economix.vista.acciones.seleccionEjecutable;

public class Inicio
{
    public static void main(String[] args)
    {
        System.out.println("\t<:: PixUp ::>");
        seleccionEjecutable.getInstance().run();
        System.out.println("\t¡Hasta pronto!");
    }
}