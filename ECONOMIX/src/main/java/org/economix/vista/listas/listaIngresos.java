package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.model.catalogos.ingresos.ingresosCatalogo;
import org.economix.model.catalogos.ingresos.conceptoIngresosCatalogo;

public class listaIngresos extends leerAcciones
{
    private static org.economix.vista.listas.listaIngresos listaIngresos;

    private listaIngresos()
    {
    }

    public static org.economix.vista.listas.listaIngresos getInstance()
    {
        if(listaIngresos==null)
        {
            listaIngresos = new listaIngresos();
        }
        return listaIngresos;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t::: Catálogo de Usuario :::");
        System.out.println( "1.- Ingresos");
        System.out.println( "2.- Concepto Ingresos");
        System.out.println( "3.- Presupuesto");
        System.out.println( "4.- Salir " );
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
        return 4;
    }

    @Override
    public void procesaOpcion()
    {
        Ejecutable ejecutable = null;
        switch (opcion)
        {
            case 1:
                ejecutable = ingresosCatalogo.getInstance();
                break;
            case 2:
                ejecutable = conceptoIngresosCatalogo.getInstance();
                break;
            case 3:
                flag=false;
                break;
            case 4:
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


