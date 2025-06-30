package org.economix.ventana.model.simulador;

import org.economix.model.gastos.Gastos;
import org.economix.model.ingresos.Ingresos;
import org.economix.model.usuario.Usuario;
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
 * Panel con gráficos usando JFreeChart.
 */
public class GraficasPanel extends JPanel {
    public GraficasPanel(SessionFactory sf, Usuario usuario){
        setLayout(new GridLayout(1,2));
        add(crearPie(sf, usuario));
        add(crearBarra(sf, usuario));
    }

    private ChartPanel crearPie(SessionFactory sf, Usuario usuario){
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        try(Session s = sf.openSession()){
            List<Object[]> rows = s.createQuery(
                            "select g.articuloGasto, sum(g.montoGastos) " +
                                    "from Gastos g where g.usuario.id = :uid group by g.articuloGasto",
                            Object[].class)
                    .setParameter("uid", usuario.getId())
                    .list();
            for(Object[] r: rows){
                String art = (String) r[0];
                BigDecimal tot = (BigDecimal) r[1];
                ds.setValue(art, tot.doubleValue());
            }
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Gastos por categoría", ds, true, true, false);
        return new ChartPanel(chart);
    }

    private ChartPanel crearBarra(SessionFactory sf, Usuario usuario){
        Map<String, BigDecimal> ing = new HashMap<>();
        Map<String, BigDecimal> gas = new HashMap<>();
        try(Session s = sf.openSession()){
            List<Ingresos> ingresos = s.createQuery(
                            "from Ingresos where usuario.id = :uid", Ingresos.class)
                    .setParameter("uid", usuario.getId()).list();
            for(Ingresos i: ingresos){
                String mes = obtenerMes(i.getFechaIngresos());
                ing.merge(mes, i.getMontoIngreso(), BigDecimal::add);
            }
            List<Gastos> gastos = s.createQuery(
                            "from Gastos where usuario.id = :uid", Gastos.class)
                    .setParameter("uid", usuario.getId()).list();
            for(Gastos g: gastos){
                String mes = obtenerMes(g.getFechaGastos());
                gas.merge(mes, g.getMontoGastos(), BigDecimal::add);
            }
        }
        Set<String> meses = new TreeSet<>(ing.keySet());
        meses.addAll(gas.keySet());
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for(String m: meses){
            ds.addValue(ing.getOrDefault(m, BigDecimal.ZERO).doubleValue(), "Ingresos", m);
            ds.addValue(gas.getOrDefault(m, BigDecimal.ZERO).doubleValue(), "Gastos", m);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Ingresos vs Gastos", "Mes", "Monto", ds);
        return new ChartPanel(chart);
    }

    private String obtenerMes(Date date){
        LocalDate d = date.toLocalDate();
        return d.getYear()+"-"+String.format("%02d", d.getMonthValue());
    }
}