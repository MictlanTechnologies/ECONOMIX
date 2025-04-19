package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;

public class listaCatalogos extends leerAcciones
{
    public static org.economix.vista.listas.listaCatalogos listaCatalogos;
    private listaCatalogos()
    {
    }
    public static org.economix.vista.listas.listaCatalogos getInstance( )
    {
        if(listaCatalogos==null)
        {
            listaCatalogos = new listaCatalogos();
        }
        return listaCatalogos;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t::: Menu ECONOMIX :::");
        System.out.println("\t> Selecciona una opción:");
        System.out.println( "1.- Usuario");
        System.out.println( "2.- Funciones");
        System.out.println( "3.- Salir");
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
        switch(opcion)
        {
            case 1:
                ejecutable = listaUsuario.getInstance();
                break;
            case 2:
                ejecutable = listaFunciones.getInstance();
                break;
            case 3:
                flag = false;
                break;
            default:
                Menu.opcionInvalida();
                break;
        }
        if(ejecutable!=null)
        {
            ejecutable.setFlag(true);
            ejecutable.run();
        }
    }
}

