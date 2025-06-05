package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.model.catalogos.gastos.conceptoGastosCatalogo;
import org.economix.model.catalogos.gastos.gastosCatalogo;

public class listaGastos extends leerAcciones
{
    private static org.economix.vista.listas.listaGastos listaGastos;

    private listaGastos()
    {
    }

    public static org.economix.vista.listas.listaGastos getInstance()
    {
        if(listaGastos ==null)
        {
            listaGastos = new listaGastos();
        }
        return listaGastos;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t::: Catálogo de Usuario :::");
        System.out.println( "1.- Gastos");
        System.out.println( "2.- Conceptos Gastos");
        System.out.println( "3.- Salir " );
        Menu.seleccionaOpcion();
    }

    @Override
    public int valorMinMenu()
    {
        return 1;
    }

    @Override
    public int valorMaxMenu()
    {
        return 3;
    }

    @Override
    public void procesaOpcion()
    {
        Ejecutable ejecutable = null;
        switch (opcion)
        {
            case 1:
                ejecutable = gastosCatalogo.getInstance();
                break;
            case 2:
                ejecutable = conceptoGastosCatalogo.getInstance();
                break;
            case 3:
                flag=false;
                break;
            default:
                Menu.opcionInvalida();
                break;
        }

        if(ejecutable!=null)
        {
            ejecutable.setFlag( true );
            ejecutable.run( );
        }
    }
}

