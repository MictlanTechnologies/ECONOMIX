package org.economix.vista.listas;

import org.economix.vista.acciones.Ejecutable;
import org.economix.vista.acciones.Menu;
import org.economix.vista.acciones.leerAcciones;
import org.economix.model.catalogos.usuario.domicilioCatalogo;
import org.economix.model.catalogos.usuario.personaCatalogo;
import org.economix.model.catalogos.usuario.usuarioCatalogo;
import org.economix.model.catalogos.usuario.contactoCatalogo;

public class listaUsuario extends leerAcciones
{
    private static org.economix.vista.listas.listaUsuario listaUsuario;

    private listaUsuario()
    {
    }

    public static org.economix.vista.listas.listaUsuario getInstance()
    {
        if(listaUsuario==null)
        {
            listaUsuario = new listaUsuario();
        }
        return listaUsuario;
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t::: Catálogo de Usuario :::");
        System.out.println( "1.- Usuario");
        System.out.println( "2.- Persona");
        System.out.println( "3.- Domicilio");
        System.out.println( "4.- Contacto");
        System.out.println( "5.- Salir " );
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
        return 5;
    }

    @Override
    public void procesaOpcion()
    {
        Ejecutable ejecutable = null;
        switch (opcion)
        {
            case 1:
                ejecutable = usuarioCatalogo.getInstance();
                break;
            case 2:
                ejecutable = personaCatalogo.getInstance();
                break;
            case 3:
                ejecutable = domicilioCatalogo.getInstance();
                break;
            case 4:
                ejecutable = contactoCatalogo.getInstance();
                break;
            case 5:
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

