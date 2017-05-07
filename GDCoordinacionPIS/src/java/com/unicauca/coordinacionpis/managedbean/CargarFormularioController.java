/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.managedbean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named(value = "cargarFormulariosController")
@SessionScoped
public class CargarFormularioController implements Serializable {

    private String ruta;

    public String getRuta() {
        return ruta;
    }

    public CargarFormularioController() {

    }

    public void cargarGestionOfertaAcademica() {
        this.ruta = "/coordinador/OfertaAcademica/ofertasAcademicas.xhtml";
    }
    
    public void cargarRegistrarDepartamento() {
        this.ruta = "/coordinador/departamento/listarDepartamento.xhtml";
    }

    public void cargarRegistrarMateria() {
        this.ruta = "/coordinador/materia/ListarMaterias.xhtml";
    }

    public void cargarPlanesdeEstudio() {
        this.ruta = "/coordinador/PlandeEstudio/PlandeEstudio.xhtml";
    }

    public void cargarGestionAnteproyecto() {
        this.ruta = "/coordinador/anteproyecto/FormatoA/formatoA.xhtml";
    }

    //jefe
    public void cargarListaOfertaAcademica() {
        this.ruta = "/jefe/OfertaAcademica/ofertasAcademicas.xhtml";
    }
}
