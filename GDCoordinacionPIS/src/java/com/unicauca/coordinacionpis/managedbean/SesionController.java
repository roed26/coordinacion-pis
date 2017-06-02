package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coordinacionpis.entidades.Usuario;
import com.unicauca.coordinacionpis.entidades.Usuariogrupo;
import com.unicauca.coordinacionpis.sessionbean.UsuarioFacade;
import com.unicauca.coordinacionpis.sessionbean.UsuariogrupoFacade;
import com.unicauca.coordinacionpis.utilidades.Utilidades;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class SesionController implements Serializable {

    @EJB
    private UsuariogrupoFacade ejbUsuarioGrupo;
    @EJB
    private UsuarioFacade ejbUsuario;

    private String nombreDeUsuario;
    private String contrasenia;
    private String identificacion;
    private String grupo;

    //bools
    private boolean haySesion;
    private boolean errorSesion;
    private boolean opcionesCoordinador;
    private boolean opcionesAdministrador;

    public SesionController() {
        opcionesAdministrador = false;
        opcionesCoordinador = true;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public boolean isHaySesion() {
        return haySesion;
    }

    public void setHaySesion(boolean haySesion) {
        this.haySesion = haySesion;
    }

    public boolean isErrorSesion() {
        return errorSesion;
    }

    public void setErrorSesion(boolean errorSesion) {
        this.errorSesion = errorSesion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public boolean isOpcionesCoordinador() {
        return opcionesCoordinador;
    }

    public void setOpcionesCoordinador(boolean opcionesCoordinador) {
        this.opcionesCoordinador = opcionesCoordinador;
    }

    public boolean isOpcionesAdministrador() {
        return opcionesAdministrador;
    }

    public void setOpcionesAdministrador(boolean opcionesAdministrador) {
        this.opcionesAdministrador = opcionesAdministrador;
    }

    public void login() throws IOException, ServletException {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext fc = FacesContext.getCurrentInstance();

        HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();

        if (req.getUserPrincipal() == null) {
            try {
                req.login(this.nombreDeUsuario, this.contrasenia);
                req.getServletContext().log("Autenticacion exitosa");
                this.haySesion = true;
                this.errorSesion = false;
                Usuariogrupo usuariogrupo = this.ejbUsuarioGrupo.buscarPorNombreUsuarioObj(req.getUserPrincipal().getName());
                this.grupo = usuariogrupo.getUsuariogrupoPK().getGruid();
                if (grupo.equalsIgnoreCase("1")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/GDCoordinacionPIS/GDCP/sesionAdmin/Principal.xhtml");
                    identificacion = "" + this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuario().getUsuid();

                } else if (grupo.equalsIgnoreCase("2")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/GDCoordinacionPIS/GDCP/sesionCoordinador/Principal.xhtml");
                    identificacion = "" + this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuario().getUsuid();

                } else if (grupo.equalsIgnoreCase("3")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/GDCoordinacionPIS/GDCP/sesionJefe/Principal.xhtml");
                    identificacion = "" + this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuario().getUsuid();

                }
            } catch (ServletException e) {

                this.errorSesion = true;

            }
        } else {
            logout();
            try {
                req.login(this.nombreDeUsuario, this.contrasenia);
                req.getServletContext().log("Autenticacion exitosa");
                this.haySesion = true;
                this.errorSesion = false;

                if (this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuariogrupoPK().getGruid().equalsIgnoreCase("2")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/GDCoordinacionPIS/GDCP/sesionCoordinador/Principal.xhtml");
                    identificacion = "" + this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuario().getUsuid();

                } else if (this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuariogrupoPK().getGruid().equalsIgnoreCase("2")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("/GDCoordinacionPIS/GDCP/sesionCoordinador/Principal.xhtml");
                    identificacion = "" + this.ejbUsuarioGrupo.buscarPorNombreUsuario(req.getUserPrincipal().getName()).get(0).getUsuario().getUsuid();

                }
            } catch (ServletException e) {

                this.errorSesion = true;

            }
        }

    }

    public void logout() throws IOException, ServletException {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();
        try {
            req.logout();
            req.getSession().invalidate();
            fc.getExternalContext().invalidateSession();
            FacesContext.getCurrentInstance().getExternalContext().redirect("/GDCoordinacionPIS/");

        } catch (ServletException e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "FAILED", "Logout failed on backend"));
        }

    }

    public String paraMostrar() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();
        if (req.getUserPrincipal() == null) {
            return "";
        } else {
            Usuario usuario = ejbUsuario.buscarUsuarioPorNombreDeUsuario(req.getUserPrincipal().getName());
            if (usuario == null) {
                return "";
            } else {
                String[] nombres = usuario.getUsunombres().split(" ");
                String[] apellidos = usuario.getUsuapellidos().split(" ");

                String nombreAMostrar = "";
                if (nombres.length > 0) {
                    nombreAMostrar = nombreAMostrar + nombres[0];
                }
                if (apellidos.length > 0) {
                    nombreAMostrar = nombreAMostrar + " " + apellidos[0];
                }

                return nombreAMostrar;
            }

        }
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

    public void modficarOpciones(String opcion, CargarFormularioController cargarFormularioController, AddNavegacionController addNavegacionController) {
        if (opcion.equalsIgnoreCase("coordinador")) {
            opcionesAdministrador = false;
            opcionesCoordinador = true;
        } else {
            opcionesAdministrador = true;
            opcionesCoordinador = false;
        }
        cargarFormularioController.setRuta("");
        addNavegacionController.cambioDeOpciones();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("formMenu");
        requestContext.update("formMenuUsuario");
        requestContext.update("panelContent");
        requestContext.update("navegacion");
    }

}
