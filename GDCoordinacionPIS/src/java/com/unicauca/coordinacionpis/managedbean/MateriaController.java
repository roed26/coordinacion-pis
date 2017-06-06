package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coordinacionpis.entidades.Departamento;
import com.unicauca.coordinacionpis.entidades.Materia;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil.PersistAction;
import com.unicauca.coordinacionpis.sessionbean.MateriaFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;

@Named("materiaController")
@SessionScoped
public class MateriaController implements Serializable {

    @EJB
    private com.unicauca.coordinacionpis.sessionbean.MateriaFacade ejbFacade;
    private List<Materia> items = null;
    private Materia materia;
    private Departamento departamento;
    private String datoBusqueda;

    public MateriaController() {
        materia= new Materia();
        departamento= new Departamento();
        datoBusqueda="";
    }

    public Materia getSelected() {
        return materia;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public void setSelected(Materia selected) {
        this.materia = selected;
        this.departamento= selected.getIdDepartamento();
    }

    public String getDatoBusqueda() {
        return datoBusqueda;
    }

    public void setDatoBusqueda(String datoBusqueda) {
        this.datoBusqueda = datoBusqueda;
    }

    public List<Materia> getListaMaterias(){
        
        return ejbFacade.buscarMateria(datoBusqueda);
    }
    public void registrarMateria(){
        materia.setIdDepartamento(departamento);
        ejbFacade.create(materia);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('MateriaCreateDialog').hide()");
        items = ejbFacade.findAll();
        departamento = new Departamento();
        materia = new Materia();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Materia registrada con exito."));
        requestContext.execute("PF('mensajeRegistroExitoso').show()");
    }
    
    public void editarMateria(){
        materia.setIdDepartamento(departamento);
        ejbFacade.edit(materia);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('MateriaEditDialog').hide()");
        items = ejbFacade.findAll();
        departamento = new Departamento();
        materia = new Materia();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Materia editada con exito."));
        requestContext.execute("PF('mensajeRegistroExitoso').show()");
    }
    public void cancelarEdicion() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('MateriaEditDialog').hide()");
        departamento = new Departamento();
        materia = new Materia();
    }
    public void cancelarRegistro(){
        
        departamento = new Departamento();
        materia = new Materia();
    }
    
    public void confirmarEliminacion(Materia materia){
        RequestContext context= RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "¿Desea Eliminar?"));
        context.execute("PF('Confirmacion').show()");
        this.materia = materia;
    }
    
    public void eliminarMateria(){
        ejbFacade.remove(materia);
        items = ejbFacade.findAll();
        RequestContext context= RequestContext.getCurrentInstance();
        context.update("MateriaListForm:datalist");
        context.execute("PF('Confirmacion').hide()");
        departamento = new Departamento();
        materia = new Materia();
       
    }
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MateriaFacade getFacade() {
        return ejbFacade;
    }

    


    public List<Materia> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (materia != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(materia);
                } else {
                    getFacade().remove(materia);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleMateria").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleMateria").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Materia getMateria(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Materia> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Materia> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Materia.class)
    public static class MateriaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MateriaController controller = (MateriaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "materiaController");
            return controller.getMateria(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Materia) {
                Materia o = (Materia) object;
                return getStringKey(o.getIdMateria());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Materia.class.getName()});
                return null;
            }
        }

    }

}
