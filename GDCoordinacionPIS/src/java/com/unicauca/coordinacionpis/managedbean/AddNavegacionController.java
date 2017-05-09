/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.managedbean;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author edwin
 */
@Named(value = "addNavegacionController")
@SessionScoped
public class AddNavegacionController implements Serializable {

    private MenuModel menuNavegacion = new DefaultMenuModel();

    public MenuModel getMenu() {
        return menuNavegacion;
    }

    public AddNavegacionController() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem inicio = new DefaultMenuItem();
        inicio.setValue("Inicio");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(inicio);
    }

    public void addOfertaAcademicaGestion() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem ofertaAcademica = new DefaultMenuItem();
        ofertaAcademica.setValue("Oferta académica");

        DefaultMenuItem ofertaAcademicaGestion = new DefaultMenuItem();
        ofertaAcademicaGestion.setValue("Gestión de ofertas académicas");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(ofertaAcademica);
        this.menuNavegacion.addElement(ofertaAcademicaGestion);

    }

    public void addOfertaAcademicaLista() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem ofertaAcademica = new DefaultMenuItem();
        ofertaAcademica.setValue("Oferta académica");

        DefaultMenuItem ofertaAcademicaGestion = new DefaultMenuItem();
        ofertaAcademicaGestion.setValue("Listado de ofertas académicas");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(ofertaAcademica);
        this.menuNavegacion.addElement(ofertaAcademicaGestion);

    }

    public void addOfertaAcademicaRegistroMaterias() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem ofertaAcademica = new DefaultMenuItem();
        ofertaAcademica.setValue("Oferta académica");

        DefaultMenuItem ofertaAcademicaGestion = new DefaultMenuItem();
        ofertaAcademicaGestion.setValue("Registrar materia");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(ofertaAcademica);
        this.menuNavegacion.addElement(ofertaAcademicaGestion);

    }

    public void addOfertaAcademicaRegistroDepFac() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem ofertaAcademica = new DefaultMenuItem();
        ofertaAcademica.setValue("Oferta académica");

        DefaultMenuItem ofertaAcademicaGestion = new DefaultMenuItem();
        ofertaAcademicaGestion.setValue("Registrar departamento o facultad");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(ofertaAcademica);
        this.menuNavegacion.addElement(ofertaAcademicaGestion);

    }

    public void addPlanesDeEstudioGestion() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem planesDeEstudio = new DefaultMenuItem();
        planesDeEstudio.setValue("Planes de estudio");

        DefaultMenuItem planesDeEstudioGestion = new DefaultMenuItem();
        planesDeEstudioGestion.setValue("Gestión planes de estudio");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(planesDeEstudio);
        this.menuNavegacion.addElement(planesDeEstudioGestion);

    }

    public void addAnteproyectoGestion() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem anteproyecto = new DefaultMenuItem();
        anteproyecto.setValue("Anteproyecto");

        DefaultMenuItem anteproyectoGestion = new DefaultMenuItem();
        anteproyectoGestion.setValue("Gestión de anteproyectos");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(anteproyecto);
        this.menuNavegacion.addElement(anteproyectoGestion);

    }

    public void cambioDeOpciones() {
        this.menuNavegacion = new DefaultMenuModel();
        DefaultMenuItem index = new DefaultMenuItem();
        index.setValue("Index");

        DefaultMenuItem inicio = new DefaultMenuItem();
        inicio.setValue("Inicio");

        this.menuNavegacion.addElement(index);
        this.menuNavegacion.addElement(inicio);
    }

}
