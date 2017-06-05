package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coordinacionpis.entidades.Cargo;
import com.unicauca.coordinacionpis.entidades.Grupo;
import com.unicauca.coordinacionpis.entidades.Usuario;
import com.unicauca.coordinacionpis.entidades.Usuariogrupo;
import com.unicauca.coordinacionpis.entidades.UsuariogrupoPK;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil;
import com.unicauca.coordinacionpis.managedbean.util.JsfUtil.PersistAction;
import com.unicauca.coordinacionpis.sessionbean.UsuarioFacade;
import com.unicauca.coordinacionpis.sessionbean.UsuariogrupoFacade;
import com.unicauca.coordinacionpis.utilidades.Cifrar;
import com.unicauca.coordinacionpis.utilidades.RedimensionadorImagenes;
import com.unicauca.coordinacionpis.utilidades.Utilidades;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.PhaseId;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@Named("usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    @EJB
    private com.unicauca.coordinacionpis.sessionbean.UsuarioFacade ejbUsuario;
    @EJB
    private UsuariogrupoFacade ejbUsuarioGrupo;

    private List<Usuario> items = null;
    private Cargo cargo;
    private Grupo grupo;
    private List<Usuario> filtroBusqueda;

    private boolean campoFoto;
    private boolean campoContrasena;

    private String contrasena;
    private String datoBusqueda;

    private Usuario usuario;
    private UploadedFile file;

    private SimpleDateFormat formatoFecha;

    public UsuarioController() {
        this.usuario = new Usuario();
        this.cargo = new Cargo();
        this.grupo = new Grupo();
        usuario.setUsugenero('M');
        this.campoFoto = true;
        this.campoContrasena = true;
        this.formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
    }

    @PostConstruct
    public void init() {

    }

    public Usuario getSelected() {
        return usuario;
    }

    public void setSelected(Usuario selected) {
        this.usuario = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsuarioFacade getFacade() {
        return ejbUsuario;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public boolean isCampoContrasena() {
        return campoContrasena;
    }

    public void setCampoContrasena(boolean campoContrasena) {
        this.campoContrasena = campoContrasena;
    }

    public List<Usuario> getFiltroBusqueda() {
        return filtroBusqueda;
    }

    public void setFiltroBusqueda(List<Usuario> filtroBusqueda) {
        this.filtroBusqueda = filtroBusqueda;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getDatoBusqueda() {
        return datoBusqueda;
    }

    public void setDatoBusqueda(String datoBusqueda) {
        this.datoBusqueda = datoBusqueda;
    }

    public SimpleDateFormat getFormatoFecha() {
        return formatoFecha;
    }

    public void setFormatoFecha(SimpleDateFormat formatoFecha) {
        this.formatoFecha = formatoFecha;
    }

    public Usuario prepareCreate() {
        usuario = new Usuario();
        initializeEmbeddableKey();
        return usuario;
    }

    public boolean isCampoFoto() {
        return campoFoto;
    }

    public void setCampoFoto(boolean campoFoto) {
        this.campoFoto = campoFoto;
    }

    public void registrarUsuario() {
        this.usuario.setUsucontrasena(Cifrar.sha256(this.usuario.getUsucontrasena()));
        this.usuario.setUsufoto(inputStreamToByteArray(file));
        usuario.setCarid(cargo);
        ejbUsuario.create(usuario);
        Usuariogrupo usuarioGrupo = new Usuariogrupo();
        UsuariogrupoPK usuarioGrupoPK = new UsuariogrupoPK();

        usuarioGrupoPK.setGruid(grupo.getGruid());
        usuarioGrupoPK.setUsuid(this.usuario.getUsuid());
        usuarioGrupo.setUsuariogrupoPK(usuarioGrupoPK);
        usuarioGrupo.setUsunombreusuario(this.usuario.getUsunombreusuario());
        this.ejbUsuarioGrupo.create(usuarioGrupo);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('UsuarioCreateDialog').hide()");
        items = ejbUsuario.findAll();
        usuario = new Usuario();
        usuario.setUsugenero('M');
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La información se registró con éxito."));
        requestContext.execute("PF('mensajeRegistroExitoso').show()");
    }

    public void editarUsuario() {

        usuario.setCarid(cargo);

        Usuariogrupo usuarioGrupo = new Usuariogrupo();
        UsuariogrupoPK usuarioGrupoPK = new UsuariogrupoPK();
        ejbUsuario.edit(usuario);
        usuarioGrupoPK.setGruid(grupo.getGruid());
        usuarioGrupoPK.setUsuid(this.usuario.getUsuid());
        usuarioGrupo.setUsuariogrupoPK(usuarioGrupoPK);
        usuarioGrupo.setUsunombreusuario(this.usuario.getUsunombreusuario());
        this.ejbUsuarioGrupo.edit(usuarioGrupo);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute("PF('UsuarioEditDialog').hide()");
        items = ejbUsuario.findAll();
        usuario = new Usuario();
        usuario.setUsugenero('M');
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La información se edito con éxito."));
        requestContext.execute("PF('mensajeRegistroExitoso').show()");
    }

    public void seleccionarUsuarioEditar(Usuario usuario) {
        this.usuario = usuario;
        this.cargo = usuario.getCarid();
        this.grupo = ejbUsuarioGrupo.buscarPorNombreUsuarioObj(usuario.getUsunombreusuario()).getGrupo();
    }

    public void seleccionarUsuarioVer(Usuario usuario) {
        this.usuario = usuario;
        this.cargo = usuario.getCarid();
        this.grupo = ejbUsuarioGrupo.buscarPorNombreUsuarioObj(usuario.getUsunombreusuario()).getGrupo();
    }

    public void mostrarModificarContrasena() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        this.campoContrasena = false;
        requestContext.update("UsuarioEditForm");
    }

    public void cancelarActualizarContrasena() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        this.campoContrasena = true;
        this.contrasena = "";
        requestContext.update("UsuarioEditForm");
    }

    public void actualizarContrasena() {

        RequestContext requestContext = RequestContext.getCurrentInstance();

        this.campoContrasena = true;
        this.usuario.setUsucontrasena(Cifrar.sha256(this.contrasena));
        this.ejbUsuario.edit(this.usuario);

        requestContext.update("UsuarioEditForm");
    }

    public String getFecha() {
        String fechaNacimiento = "";
        if (usuario.getUsufechanacimiento() != null) {
            fechaNacimiento = formatoFecha.format(usuario.getUsufechanacimiento());
        }

        return fechaNacimiento;
    }

    public void buscarUsuario() {
        this.items = ejbUsuario.buscarUsuarioEjb(this.datoBusqueda.toLowerCase());
    }

    private byte[] inputStreamToByteArray(UploadedFile file) {
        byte[] imagen = null;
        if (file != null) {
            try {
                try (InputStream input = file.getInputstream()) {
                    imagen = RedimensionadorImagenes.redimensionar(input, 150);
                }

            } catch (Exception ex) {
            }
        }

        return imagen;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleAdmin").getString("UsuarioCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleAdmin").getString("UsuarioUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleAdmin").getString("UsuarioDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            usuario = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Usuario> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void cargarFoto(FileUploadEvent event) {
        file = event.getFile();
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (usuario != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(usuario);
                } else {
                    getFacade().remove(usuario);
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

    public Usuario getUsuario(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Usuario> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Usuario> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public StreamedContent getImagenFlujo() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            Usuario usu = ejbUsuario.buscarPorIdUsuario(Long.valueOf(id)).get(0);
            if (usu.getUsufoto() == null) {
                return Utilidades.getImagenPorDefecto("foto");
            } else {
                return new DefaultStreamedContent(new ByteArrayInputStream(usu.getUsufoto()));
            }
        }
    }

    public StreamedContent getImagenFlujoEditar() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext context = FacesContext.getCurrentInstance();

        String id = context.getExternalContext().getRequestParameterMap().get("idUsu");
        if (usuario.getUsufoto() == null) {
            return Utilidades.getImagenPorDefecto("foto");
        } else {
            return new DefaultStreamedContent(new ByteArrayInputStream(usuario.getUsufoto()));
        }

    }

    public void mostraSubirFoto() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        this.campoFoto = false;
        requestContext.update("formEditarfoto");

    }

    public void actualizarFoto(FileUploadEvent event) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        UploadedFile file = event.getFile();
        usuario.setUsufoto(inputStreamToByteArray(file));
        this.ejbUsuario.edit(usuario);
        this.campoFoto = true;
        file = null;
        requestContext.update(":formEditarfoto:panel");
        requestContext.update(":UsuarioListForm:datalist");

    }

    public void cancelarRegistroUsuario() {
        this.usuario = new Usuario();
        usuario.setUsugenero('M');
        this.cargo = new Cargo();
        this.grupo = new Grupo();
        this.campoFoto = true;
        this.campoContrasena = true;
        file = null;
    }

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.getUsuario(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return getStringKey(o.getUsuid());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Usuario.class.getName()});
                return null;
            }
        }

    }

}
