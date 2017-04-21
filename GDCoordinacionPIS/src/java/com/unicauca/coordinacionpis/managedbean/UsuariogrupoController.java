package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coordinacionpis.entidades.Usuariogrupo;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil.PersistAction;
import com.unicauca.coordinacionpis.sessionbean.UsuariogrupoFacade;

import java.io.Serializable;
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

@Named("usuariogrupoController")
@SessionScoped
public class UsuariogrupoController implements Serializable {

    @EJB
    private com.unicauca.coordinacionpis.sessionbean.UsuariogrupoFacade ejbFacade;
    private List<Usuariogrupo> items = null;
    private Usuariogrupo selected;

    public UsuariogrupoController() {
    }

    public Usuariogrupo getSelected() {
        return selected;
    }

    public void setSelected(Usuariogrupo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getUsuariogrupoPK().setGruid(selected.getGrupo().getGruid());
        selected.getUsuariogrupoPK().setUsuid(selected.getUsuario().getUsuid());
    }

    protected void initializeEmbeddableKey() {
        selected.setUsuariogrupoPK(new com.unicauca.coordinacionpis.entidades.UsuariogrupoPK());
    }

    private UsuariogrupoFacade getFacade() {
        return ejbFacade;
    }

    public Usuariogrupo prepareCreate() {
        selected = new Usuariogrupo();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleAdmin").getString("UsuariogrupoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleAdmin").getString("UsuariogrupoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleAdmin").getString("UsuariogrupoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Usuariogrupo> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleAdmin").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleAdmin").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Usuariogrupo getUsuariogrupo(com.unicauca.coordinacionpis.entidades.UsuariogrupoPK id) {
        return getFacade().find(id);
    }

    public List<Usuariogrupo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Usuariogrupo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Usuariogrupo.class)
    public static class UsuariogrupoControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuariogrupoController controller = (UsuariogrupoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuariogrupoController");
            return controller.getUsuariogrupo(getKey(value));
        }

        com.unicauca.coordinacionpis.entidades.UsuariogrupoPK getKey(String value) {
            com.unicauca.coordinacionpis.entidades.UsuariogrupoPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.unicauca.coordinacionpis.entidades.UsuariogrupoPK();
            key.setGruid(values[0]);
            key.setUsuid(Long.parseLong(values[1]));
            return key;
        }

        String getStringKey(com.unicauca.coordinacionpis.entidades.UsuariogrupoPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getGruid());
            sb.append(SEPARATOR);
            sb.append(value.getUsuid());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Usuariogrupo) {
                Usuariogrupo o = (Usuariogrupo) object;
                return getStringKey(o.getUsuariogrupoPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Usuariogrupo.class.getName()});
                return null;
            }
        }

    }

}
