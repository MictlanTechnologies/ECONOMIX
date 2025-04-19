package org.economix.vista.acciones;

import org.economix.model.Catalogo;
import org.economix.sql.GenericSql;

import java.util.List;

public abstract class GestorCatalogos<T extends Catalogo> extends LeerAcciones
{
    protected List<T> list;
    protected T t;
    protected boolean flag2;
    protected GenericSql<T> genericSql;

    public GestorCatalogos(GenericSql<T> genericSql)
    {
        this.genericSql = genericSql;
    }

    public abstract T newT();
    public abstract boolean processNewT(T t);
    public abstract boolean processEditT(T t);

    public void print()
    {
        List<T> list = genericSql.findAll();
        if(list.isEmpty())
        {
            System.out.println("> No hay elementos registrados.");
        }
        list.forEach(System.out::println);
    }

    public void add( )
    {
        t = newT( );
        if(processNewT( t ))
        {
            System.out.println("> Elemento añadido con éxito.");
        }
    }

    public void edit( )
    {
        List<T> list = genericSql.findAll();
        if( list.isEmpty( ) )
        {
            System.out.println( ">  No hay elementos para editar." );
            return;
        }
        flag2 = true;
        while ( flag2 )
        {
            list.forEach(System.out::println);
            System.out.print( "> Ingrese el ID del elemento a editar: " );

            t = list.stream()
                    .filter( e -> e.getId().equals( ReadUtil.readInt( ) ) )
                    .findFirst()
                    .orElse( null );

            if( t==null )
            {
                System.out.println( "> No se encontró el elemento." );
                System.out.print( "> Deseas volver a intentarlo? s/n: ");
                String respuesta = ReadUtil.read();

                flag2 = respuesta.equalsIgnoreCase("S");
            }
            else
            {
                if(processEditT(t))
                {
                    System.out.println( "> Elemento editado con éxito." );
                }
                flag2 = false;
            }
        }
    }

    public void remove( )
    {
        List<T> list = genericSql.findAll();
        if( list.isEmpty( ) )
        {
            System.out.println( ">  No hay elementos para eliminar." );
            return;
        }
        flag2 = true;
        while ( flag2 )
        {
            list.forEach(System.out::println);
            System.out.print( "> Ingrese el ID del elemento a eliminar: " );

            t = list.stream()
                    .filter( e -> e.getId().equals( ReadUtil.readInt( ) ) )
                    .findFirst()
                    .orElse( null );

            if( t==null )
            {
                System.out.println( "> No se encontró el elemento." );
                System.out.print( "> Deseas volver a intentarlo? s/n: ");
                String respuesta = ReadUtil.read();

                flag2 = respuesta.equalsIgnoreCase("S");
            }
            else
            {
                if(genericSql.delete(t))
                {
                    System.out.println( "> Elemento eliminado con éxito." );
                }
                flag2 = false;
            }
        }
    }

    public void findById()
    {
        System.out.print("> Ingresa un ID para buscar: ");
        t = genericSql.findById( ReadUtil.readInt() );

        if(t!=null)
        {
            System.out.println(t);
        }
        else
        {
            System.out.println("> No existe un elemento con dicho ID.");
        }
    }

    @Override
    public void despliegaMenu()
    {
        System.out.println("\n\t:: Gestión de catálogos ::");
        System.out.println("Seleccione una opción:");
        System.out.println("1.- Agregar");
        System.out.println("2.- Eliminar");
        System.out.println("3.- Editar");
        System.out.println("4.- Imprimir elementos en lista");
        System.out.println("5.- Obtener por su ID");
        System.out.println("6.- Salir");
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
        return 6;
    }

    @Override
    public void procesaOpcion()
    {
        switch (opcion)
        {
            case 1:
                add( );
                break;
            case 2:
                remove( );
                break;
            case 3:
                edit( );
                break;
            case 4:
                print( );
                break;
            case 5:
                findById( );
                break;
            default:
                Menu.opcionInvalida();
        }
    }
}
