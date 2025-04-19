package org.economix.vista.acciones;
import org.economix.util.readUtil;

public abstract class leerAcciones implements Ejecutable
{
    protected Integer opcion;
    protected boolean flag;

    public leerAcciones( )
    {
        flag = true;
    }

    public abstract void despliegaMenu( );
    public abstract int valorMinMenu( );
    public abstract int valorMaxMenu( );
    public abstract void procesaOpcion( );

    @Override
    public void run( )
    {
        while (flag)
        {
            despliegaMenu();
            opcion = readUtil.readInt( );
            if (opcion >= valorMinMenu( ) && opcion <= valorMaxMenu( ) )
            {
                if( opcion == valorMaxMenu( ) )
                {
                    flag = false;
                }
                else
                {
                    procesaOpcion( );
                }
            }
            else
            {
                Menu.opcionInvalida();
            }
        }
    }

    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }
}
