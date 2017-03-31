package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coodinacionpis.entidades.Curso;
import com.unicauca.coodinacionpis.entidades.Departamento;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil.PersistAction;
import com.unicauca.coordinacionpis.sessionbean.CursoFacade;
import com.unicauca.coordinacionpis.sessionbean.DepartamentoFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("registrarCursoDepartamentoController")
@SessionScoped
public class RegistrarCursoDepartamentoController implements Serializable {

    @EJB
    private CursoFacade ejbCurso;
    @EJB
    private DepartamentoFacade ejbDepartamento;
    
    private List<Departamento> listaDepartamentos;
            
    public RegistrarCursoDepartamentoController(){
      listaDepartamentos = new ArrayList<>();
      
      listaDepartamentos=ejbDepartamento.findAll();
    }

    public List<Departamento> getListaDepartamentos() {
        return listaDepartamentos;
    }

    public void setListaDepartamentos(List<Departamento> listaDepartamentos) {
        this.listaDepartamentos = listaDepartamentos;
    }

    
    public void registrarCurso(){
    
    }
    
   
}
