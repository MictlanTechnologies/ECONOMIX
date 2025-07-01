// Paquete de acciones genéricas ejecutables (usado para control de flujos)
package org.economix.vista.acciones;

/**
 * Interfaz funcional para ejecutar acciones desde consola u otras vistas.
 * Se utiliza para que cada módulo implemente un método de ejecución y un control opcional (flag).
 */
public interface Ejecutable {

    /**
     * Ejecuta la acción principal del módulo.
     * este método lanza el menú o funcionalidad del componente.
     */
    void run();

    /**
     * Asigna un valor booleano a una bandera interna (útil para configurar comportamiento dinámico).
     * @param flag valor booleano que puede ser interpretado de forma personalizada
     */
    void setFlag(boolean flag);
}
