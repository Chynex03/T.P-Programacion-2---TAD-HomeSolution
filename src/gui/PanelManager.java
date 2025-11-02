package gui;

import entidades.IHomeSolution;

import javax.swing.*;
import java.awt.*;

public class PanelManager {

    private JFrame jFrame;
    private IHomeSolution homeSolution;
    private Integer seleccionado;
    private PanelPrincipal principal;
    private FormularioProyecto formularioProyecto;
    private FormularioEmpleado formularioEmpleado;
    private ListaProyectos listaProyectos;
    private JPanel[] paneles;
    private GestionProyectos gestionProyectos;
    private GestionEmpleados gestionEmpleados;

    public PanelManager(IHomeSolution h)
    {   jFrame=new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        principal=new PanelPrincipal(this);
        this.homeSolution=h;
        mostrar(principal);
    }
    public void mostrar(JPanel panel)
    {
        jFrame.getContentPane().removeAll();
        jFrame.getContentPane().add(BorderLayout.CENTER,panel);
        jFrame.getContentPane().validate();
        jFrame.getContentPane().repaint();
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.pack();
    }

    public IHomeSolution sistema() {
        return homeSolution;
    }

    public void seleccionar(Integer num)
    {
        seleccionado=num;
    }
    public Integer consultarSeleccionado(){
        return seleccionado;
    }
    public void mostrar(int codigoPantalla)
    {
        if (codigoPantalla==1)
             mostrar(principal);
        if (codigoPantalla==3){
            gestionEmpleados=gestionEmpleados==null?new GestionEmpleados(this):gestionEmpleados;
            mostrar(gestionEmpleados);
        }
        if (codigoPantalla==2){
            formularioEmpleado=formularioEmpleado==null?new FormularioEmpleado(this):formularioEmpleado;
            mostrar(formularioEmpleado);
        }
        if (codigoPantalla==4){
            formularioProyecto=formularioProyecto==null?new FormularioProyecto(this):formularioProyecto;
            mostrar(formularioProyecto);
        }
        if (codigoPantalla==5) {
           listaProyectos = listaProyectos == null ? new ListaProyectos(this) : listaProyectos;
            mostrar(listaProyectos);
        }
        if (codigoPantalla==6) {
            gestionProyectos= gestionProyectos == null ? new GestionProyectos(this) : gestionProyectos;
            mostrar(gestionProyectos);
        }

    }
}
