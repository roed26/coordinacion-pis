package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coordinacionpis.entidades.Departamento;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil.PersistAction;
import com.unicauca.coordinacionpis.sessionbean.DepartamentoFacade;

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

@Named("departamentoController")
@SessionScoped
public class DepartamentoController implements Serializable {

    @EJB
    private com.unicauca.coordinacionpis.sessionbean.DepartamentoFacade ejbFacade;
    private List<Departamento> items = null;
    private Departamento departamento;

    public DepartamentoController() {
        departamento = new Departamento();
    }

    public Departamento getSelected() {
        return departamento;
    }

    public void setSelected(Departamento selected) {
        this.departamento = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DepartamentoFacade getFacade() {
        return ejbFacade;
    }

    public Departamento prepareCreate() {
        departamento = new Departamento();
        initializeEmbeddableKey();
        return departamento;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DepartamentoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DepartamentoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DepartamentoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            departamento = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Departamento> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (departamento != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(departamento);
                } else {
                    getFacade().remove(departamento);
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Departamento getDepartamento(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Departamento> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Departamento> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Departamento.class)
    public static class DepartamentoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DepartamentoController controller = (DepartamentoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "departamentoController");
            return controller.getDepartamento(getKey(value));
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
            if (object instanceof Departamento) {
                Departamento o = (Departamento) object;
                return getStringKey(o.getIdDepartamento());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Departamento.class.getName()});
                return null;
            }
        }

    }

    public void registrarDepartamento() {

        ejbFacade.create(departamento);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('DepartamentoCreateDialog').hide()");
        items = ejbFacade.findAll();
        departamento = new Departamento();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La información se registró con éxito."));
        requestContext.execute("PF('mensajeRegistroExitoso').show()");
    }

    public void editarDepartamento() {

        ejbFacade.edit(departamento);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('DepartamentoEditDialog').hide()");
        items = ejbFacade.findAll();
        departamento = new Departamento();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La información se actualizó con éxito."));
        requestContext.execute("PF('mensajeRegistroExitoso').show()");
    }

    public void cancelarEdicion() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('DepartamentoEditDialog').hide()");
        departamento = new Departamento();
    }
    public void cancelarRegistro() {
        departamento = new Departamento();
    }
    public void cancelarEliminacion() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('mensajeConfirmarEliminar').hide()");
        departamento = new Departamento();
    }

    public void eliminarDepartamento() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if (departamento != null) {
            if (departamento.getMateriaList().isEmpty()) {
                ejbFacade.remove(departamento);
                requestContext.execute("PF('mensajeConfirmarEliminar').hide()");
                items = ejbFacade.findAll();
                requestContext.update("DepartamentoListForm:datalist");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Se elimino el departamento con éxito"));
                requestContext.execute("PF('mensajeRegistroExitoso').show()");
            } else {
                requestContext.execute("PF('mensajeConfirmarEliminar').hide()");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El departamento tiene asociadas materias"));
                requestContext.execute("PF('mensajeError').show()");
            }
        }
        departamento = new Departamento();

    }

    public void mostrarMensajeConfirmarEliminarDepartamento(Departamento departamento) {
        this.departamento = departamento;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('mensajeConfirmarEliminar').show()");

    }

}
