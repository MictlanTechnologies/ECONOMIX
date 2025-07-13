package org.economix.ventana.model;

// Importaciones de entidades, conexión Hibernate y JFreeChart
import org.economix.model.gastos.Gastos;
import org.economix.model.ingresos.Ingresos;
import org.economix.model.usuario.Usuario;
import org.economix.util.IconUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Panel gráfico que presenta visualizaciones estadísticas del usuario mediante JFreeChart.
 * Contiene dos gráficos principales:
 * - Un gráfico de pastel que muestra el total de gastos por categoría.
 * - Un gráfico de barras que compara ingresos vs. gastos por mes.
 *
 * Esta clase se integra como un JPanel reutilizable y actualizable dentro de la aplicación ECONOMIX.
 */
public class GraficasPanel extends JPanel {
    private final SessionFactory sf;  // Fábrica de sesiones Hibernate para acceder a BD
    private final Usuario usuario;    // Usuario autenticado cuyos datos se grafican
    private final JPanel graficas = new JPanel(new GridLayout(1, 2));
    /**
     * Constructor principal que inicializa el panel y dibuja los gráficos.
     *
     * @param sf fábrica de sesiones Hibernate.
     * @param usuario usuario autenticado.
     */

    public GraficasPanel(SessionFactory sf, Usuario usuario){
        this.sf = sf;
        this.usuario = usuario;
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel title = new JLabel("ECONOMIX");
        title.setFont(new Font("Roboto", Font.BOLD, 16));
        JLabel logo = new JLabel(IconUtil.getAppIconMini());
        header.add(title);
        header.add(logo);
        add(header, BorderLayout.NORTH);

        add(graficas, BorderLayout.CENTER); // Panel dividido en dos columnas para mostrar gráficos
        actualizar();                     // Cargar gráficos al iniciar
    }

    /**
     * Actualiza y recarga los gráficos al panel.
     * Puede llamarse explícitamente cada vez que se desea refrescar los datos.
     */
    public void actualizar(){
        graficas.removeAll();              // Limpiar gráficos anteriores
        graficas.add(crearPie());         // Agregar gráfico de pastel
        graficas.add(crearBarra());       // Agregar gráfico de barras
        graficas.revalidate();            // Validar cambios visuales
        graficas.repaint();              // Redibujar panel
    }

    /**
     * Crea un gráfico de pastel que muestra la proporción de gastos agrupados por tipo de artículo.
     *
     * @return ChartPanel con el gráfico generado.
     */
    private ChartPanel crearPie(){
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();

        // Consulta los gastos del usuario y agrupa por artículo
        try(Session s = sf.openSession()){
            List<Object[]> rows = s.createQuery(
                            "select g.articuloGasto, sum(g.montoGastos) " +
                                    "from Gastos g where g.usuario.id = :uid group by g.articuloGasto",
                            Object[].class)
                    .setParameter("uid", usuario.getId())
                    .list();

            // Llenar el dataset del gráfico de pastel con categoría y total
            for(Object[] r: rows){
                String art = (String) r[0];
                BigDecimal tot = (BigDecimal) r[1];
                ds.setValue(art, tot.doubleValue());
            }
        }

        // Crear y retornar el panel con el gráfico
        JFreeChart chart = ChartFactory.createPieChart(
                "Gastos por categoría", ds, true, true, false);
        return new ChartPanel(chart);
    }

    /**
     * Crea un gráfico de barras que compara ingresos vs. gastos por mes.
     *
     * @return ChartPanel con el gráfico generado.
     */
    private ChartPanel crearBarra(){
        Map<String, BigDecimal> ing = new HashMap<>();  // Ingresos agrupados por mes
        Map<String, BigDecimal> gas = new HashMap<>();  // Gastos agrupados por mes

        // Consulta a la base de datos y agrupación de montos por mes
        try(Session s = sf.openSession()){
            // Obtener ingresos y agrupar por mes
            List<Ingresos> ingresos = s.createQuery(
                            "from Ingresos where usuario.id = :uid", Ingresos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            for(Ingresos i: ingresos){
                String mes = obtenerMes(i.getFechaIngresos());
                ing.merge(mes, i.getMontoIngreso(), BigDecimal::add);
            }

            // Obtener gastos y agrupar por mes
            List<Gastos> gastos = s.createQuery(
                            "from Gastos where usuario.id = :uid", Gastos.class)
                    .setParameter("uid", usuario.getId())
                    .list();
            for(Gastos g: gastos){
                String mes = obtenerMes(g.getFechaGastos());
                gas.merge(mes, g.getMontoGastos(), BigDecimal::add);
            }
        }

        // Unificar los meses disponibles de ingresos y gastos
        Set<String> meses = new TreeSet<>(ing.keySet());
        meses.addAll(gas.keySet());

        // Crear el dataset para el gráfico de barras
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for(String m: meses){
            ds.addValue(ing.getOrDefault(m, BigDecimal.ZERO).doubleValue(), "Ingresos", m);
            ds.addValue(gas.getOrDefault(m, BigDecimal.ZERO).doubleValue(), "Gastos", m);
        }

        // Crear y retornar el panel con el gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                "Ingresos vs Gastos", "Mes", "Monto", ds);
        return new ChartPanel(chart);
    }

    /**
     * Formatea una fecha de tipo {@link Date} a un string tipo "YYYY-MM" (por ejemplo "2025-06").
     * Se utiliza para agrupar ingresos y gastos por mes.
     *
     * @param date fecha original en formato SQL.
     * @return cadena con el año y el mes extraídos de la fecha.
     */
    private String obtenerMes(Date date){
        LocalDate d = date.toLocalDate();
        return d.getYear() + "-" + String.format("%02d", d.getMonthValue());
    }
}
