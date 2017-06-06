/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.managedbean;

import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.exception.AccessDeniedException;
import com.openkm.sdk4j.exception.AutomationException;
import com.openkm.sdk4j.exception.DatabaseException;
import com.openkm.sdk4j.exception.ExtensionException;
import com.openkm.sdk4j.exception.FileSizeExceededException;
import com.openkm.sdk4j.exception.ItemExistsException;
import com.openkm.sdk4j.exception.LockException;
import com.openkm.sdk4j.exception.ParseException;
import com.openkm.sdk4j.exception.PathNotFoundException;
import com.openkm.sdk4j.exception.RepositoryException;
import com.openkm.sdk4j.exception.UnknowException;
import com.openkm.sdk4j.exception.UnsupportedMimeTypeException;
import com.openkm.sdk4j.exception.UserQuotaExceededException;
import com.openkm.sdk4j.exception.VersionException;
import com.openkm.sdk4j.exception.VirusDetectedException;
import com.openkm.sdk4j.exception.WebserviceException;
import com.unicauca.coordinacionpis.classMetadatos.MetadatosPlanEstudio;
import com.unicauca.coordinacionpis.utilidades.ConexionOpenKM;
import com.unicauca.coordinacionpis.utilidades.Validador;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author alexa
 */
@Named(value = "registroPlandeEstudioController")
@SessionScoped
public class RegistroPlandeEstudioController implements Serializable {

    private MetadatosPlanEstudio metadatosPlandeEstudio;
    private String nombreArchivo;
    private boolean exitoSubirArchivo;
    private UploadedFile archivoPlan;
    private String datos;
    private List<Document> documentosPlanEstudio;
    private ConexionOpenKM conexionOpenKM;
    private StreamedContent streamedContent;
    private com.openkm.sdk4j.bean.Document documento;
    private BufferedOutputStream output;
    private BufferedInputStream input;
    private SimpleDateFormat formatoFecha;
    private String documentoAnterior;
    private String rutaPlanesDeEstudio;
    private int auxNumeroPlan;
    private String auxAcuerdoPlan;
    private Date auxFechaPlan;

    private OKMWebservices okm;

    public RegistroPlandeEstudioController() {
        this.formatoFecha = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        this.metadatosPlandeEstudio = new MetadatosPlanEstudio();
        this.documentosPlanEstudio = new ArrayList<>();
        this.exitoSubirArchivo = false;
        this.conexionOpenKM = new ConexionOpenKM();
        this.okm = conexionOpenKM.getOkm();
        this.documentoAnterior = "";
        this.rutaPlanesDeEstudio = "/okm:root/Coordinacion/Planes de Estudio";
        auxNumeroPlan = 1;
        auxAcuerdoPlan = "";
        auxFechaPlan = null;

    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public boolean isExitoSubirArchivo() {
        return exitoSubirArchivo;
    }

    public void setExitoSubirArchivo(boolean exitoSubirArchivo) {
        this.exitoSubirArchivo = exitoSubirArchivo;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public List<Document> getDocumentosPlanEstudio() {
       
        return documentosPlanEstudio;
    }

    public void setDocumentosPlanEstudio(List<Document> documentosPlanEstudio) {
        this.documentosPlanEstudio = documentosPlanEstudio;
    }

    public MetadatosPlanEstudio getMetadatosPlandeEstudio() {
        return metadatosPlandeEstudio;
    }

    public void setMetadatosPlandeEstudio(MetadatosPlanEstudio metadatosPlandeEstudio) {
        this.metadatosPlandeEstudio = metadatosPlandeEstudio;
    }

    public StreamedContent getStreamedContent() {
        if (FacesContext.getCurrentInstance().getRenderResponse()) {
            return new DefaultStreamedContent();
        } else {
            return streamedContent;
        }
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

//Al presionar el boton cancelar, se borran los datos ingresados en el formulario y se actualiza el formulario.
    public void cancelarRegistroPlanEstudio() {
        metadatosPlandeEstudio = new MetadatosPlanEstudio();
        limpiarVariables();
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("formSeleccionarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoPlanEstudio");
        rc.update("formMetadatosPlanEstudio");

    }

    /**
     * Recibe como parametro el archivo(Plan de estudio) que se va a guardar y
     * se obtiene el archivo y nombre.
     *
     * @param event
     */
    public void seleccionarArchivo(FileUploadEvent event) {
        nombreArchivo = event.getFile().getFileName();
        System.out.println("nombre archivo: " + nombreArchivo);
        archivoPlan = event.getFile();
        FacesMessage msg = new FacesMessage("El archivo", nombreArchivo + " se seleccionó con exito");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("msg");//Actualiza la etiqueta growl para que el mensaje pueda ser mostrado
        exitoSubirArchivo = true;
        rc.update("formSeleccionarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoPlanEstudio");
        rc.update("formMetadatosPlanEstudio");

    }

    public void seleccionarArchivoActualizacion(FileUploadEvent event) {
        nombreArchivo = event.getFile().getFileName();
        System.out.println("nombre archivo: " + nombreArchivo);
        archivoPlan = event.getFile();
        FacesMessage mensaje = new FacesMessage("El archivo", nombreArchivo + " se seleccionó con exito");
        FacesContext.getCurrentInstance().addMessage(null, mensaje);
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("msg");//Actualiza la etiqueta growl para que el mensaje pueda ser mostrado
        exitoSubirArchivo = true;
        rc.update("formActualizarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoActualizarPlanEstudio");
        rc.update("formActualizarMetadatosPlanEstudio");

    }

    public void aceptarRegistroPlanEstudio() {
        RequestContext rc = RequestContext.getCurrentInstance();
        FacesMessage message = null;

        try {

            boolean existeFolder = false;
            boolean existeDocumento = false;

            existeFolder = existeCarpeta();

            if (existeFolder) {//Si existe la carpeta, busca el documento
                for (com.openkm.sdk4j.bean.Document doc : okm.getDocumentChildren("/okm:root/Coordinacion/Planes de Estudio")) {
                    if (doc.getPath().equalsIgnoreCase("/okm:root/Coordinacion/Planes de Estudio/" + nombreArchivo)) {//Buscar en openkm si existe el archivo a guardar
                        existeDocumento = true;
                    }
                }
            }
            if (existeFolder) {//Si existe la carpeta Planes de Estudio, crea dentro de ella el documento
                if (!existeDocumento) {
                    okm.createDocumentSimple(rutaPlanesDeEstudio + "/" + nombreArchivo, archivoPlan.getInputstream());//Crear el documento en openkm                    
                    okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + metadatosPlandeEstudio.getNumero());
                    okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + metadatosPlandeEstudio.getAcuerdo());
                    okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + formatoFecha.format(metadatosPlandeEstudio.getVigencia()));

                    message = new FacesMessage("El archivo", nombreArchivo + " se registro con exito");
                } else {
                    message = new FacesMessage("Error al registrar el archivo", nombreArchivo);
                }
            } else {//Si la carpeta no existe, la crea y dentro de ella crea el documento.
                System.out.println("creando carpeta");
                okm.createFolderSimple(rutaPlanesDeEstudio);//Crear carpeta Planes de Estudio en openkm
                okm.createDocumentSimple(rutaPlanesDeEstudio + "/" + nombreArchivo, archivoPlan.getInputstream());//Crear el documento dentro de la carpeta Planes de Estudio en openkm
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "El archivo " + nombreArchivo + " se registro con exito");
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
            rc.update("formMetadatosPlanEstudio");//Actualizar el formulario de registro
            rc.execute("PF('dlgRegistroPlandeEstudio').hide()");//Cerrar el dialog que contiene el formulario

            limpiarVariables();

            rc.update("formSeleccionarArchivoPlanEstudio");
            rc.update("formArchivoSelecionadoPlanEstudio");
            rc.update("formMetadatosPlanEstudio");//Actualizar el formulario de registro

            listaDocs();
            rc.update("lstPlanesEstudio");
            rc.execute("PF('dlgRegistroPlandeEstudio').hide()");//Cerrar el dialog que contiene el formulario

        } catch (PathNotFoundException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessDeniedException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ItemExistsException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExtensionException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutomationException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedMimeTypeException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileSizeExceededException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserQuotaExceededException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VirusDetectedException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VersionException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LockException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarPlanEstudio(Document document) {
        try {
            RequestContext rc = RequestContext.getCurrentInstance();

            Set<String> palabras = document.getKeywords();
            for (int i = 0; i < palabras.size(); i++) {
                if (Validador.esFecha((String) palabras.toArray()[i])) {
                    String fecha = (String) palabras.toArray()[i];
                    metadatosPlandeEstudio.setVigencia(formatoFecha.parse(fecha));
                    auxFechaPlan = metadatosPlandeEstudio.getVigencia();
                } else if (Validador.esNumero((String) palabras.toArray()[i])) {
                    metadatosPlandeEstudio.setNumero(Integer.parseInt((String) palabras.toArray()[i]));
                    auxNumeroPlan = metadatosPlandeEstudio.getNumero();
                } else {
                    metadatosPlandeEstudio.setAcuerdo((String) palabras.toArray()[i]);
                    auxAcuerdoPlan = metadatosPlandeEstudio.getAcuerdo();
                }
            }

            nombreArchivo = nombreDelArchivo(document.getPath());
            documentoAnterior = nombreArchivo;
            this.exitoSubirArchivo = true;

            rc.update("formActualizarArchivoPlanEstudio");
            rc.update("formArchivoSelecionadoActualizarPlanEstudio");
            rc.update("formActualizarMetadatosPlanEstudio");
        } catch (java.text.ParseException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editarPlanEstudio() {

        RequestContext rc = RequestContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No registraste ningún cambio");

        try {
            if (!nombreArchivo.equals(documentoAnterior)) {
                okm.deleteDocument(rutaPlanesDeEstudio + "/" + documentoAnterior);
                okm.createDocumentSimple(rutaPlanesDeEstudio + "/" + nombreArchivo, archivoPlan.getInputstream());//Crear el documento en openkm   
                okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + metadatosPlandeEstudio.getNumero());
                okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + metadatosPlandeEstudio.getAcuerdo());
                okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + formatoFecha.format(metadatosPlandeEstudio.getVigencia()));
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmación", "Plan de estudio actualizado correctamente");
            } else {
                if (metadatosPlandeEstudio.getNumero() != auxNumeroPlan) {
                    okm.removeKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + auxNumeroPlan);
                    okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + metadatosPlandeEstudio.getNumero());
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmación", "Plan de estudio actualizado correctamente");
                }
                if (!metadatosPlandeEstudio.getAcuerdo().equalsIgnoreCase(auxAcuerdoPlan)) {
                    okm.removeKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + auxAcuerdoPlan);
                    okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + metadatosPlandeEstudio.getAcuerdo());
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmación", "Plan de estudio actualizado correctamente");
                }
                if (metadatosPlandeEstudio.getVigencia().compareTo(auxFechaPlan) != 0) {
                    okm.removeKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + formatoFecha.format(auxFechaPlan));
                    okm.addKeyword(rutaPlanesDeEstudio + "/" + nombreArchivo, "" + formatoFecha.format(metadatosPlandeEstudio.getVigencia()));
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmación", "Plan de estudio actualizado correctamente");
                }
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
            rc.update("formMetadatosPlanEstudio");//Actualizar el formulario de registro
            rc.execute("PF('dlgEditarPlanEstudio').hide()");//Cerrar el dialog que contiene el formulario

            limpiarVariables();

            rc.update("formActualizarArchivoPlanEstudio");
            rc.update("formArchivoSelecionadoActualizarPlanEstudio");
            rc.update("formActualizarMetadatosPlanEstudio");//Actualizar el formulario de registro

            listaDocs();
            rc.update("lstPlanesEstudio");
            rc.execute("PF('dlgEditarPlanEstudio').hide()");//Cerrar el dialog que contiene el formulario

        } catch (AccessDeniedException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PathNotFoundException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LockException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExtensionException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedMimeTypeException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileSizeExceededException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserQuotaExceededException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VirusDetectedException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ItemExistsException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AutomationException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VersionException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtener los planes de estudio contenidos en openkm
     */
    public void listaDocs() {
        try {

            if (existeCarpeta()) {
                System.out.println("recuperando archivos");
                documentosPlanEstudio.clear();
                documentosPlanEstudio = okm.getDocumentChildren(rutaPlanesDeEstudio);//Obtener los Planes de Estudio contenidos en openkm          
            }

        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PathNotFoundException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Método utilizado para limitar la fecha de la vigencia del plan de estudio
    public Date fechaActual() {
        return new Date();
    }

    public void limpiarVariables() {//Limpiar valores ingresados en el formulario
        metadatosPlandeEstudio = new MetadatosPlanEstudio();
        nombreArchivo = "";
        archivoPlan = null;
        exitoSubirArchivo = false;
    }

    private boolean existeCarpeta() throws PathNotFoundException, RepositoryException, DatabaseException, UnknowException, WebserviceException {

        for (Folder fld : okm.getFolderChildren("/okm:root/Coordinacion")) {
           
            if (fld.getPath().equalsIgnoreCase(rutaPlanesDeEstudio)) {//Buscar en openkm si existe la carpeta Planes de Estudio                 
                return true;
            }
        }
        return false;
    }

    public StreamedContent descargarDocumento(Document document) {
        StreamedContent file = null;
        com.openkm.sdk4j.bean.Document doc = document;
        try {
            InputStream is = okm.getContent(doc.getPath());
            file = new DefaultStreamedContent(is, "application/pdf", nombreDelArchivo(doc.getPath()));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PathNotFoundException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessDeniedException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return file;
    }

    public String nombreDelArchivo(String path) {
        String partesPath[] = path.split("/");       
        return partesPath[partesPath.length - 1];
    }

    public String fecha(Date fecha) {
        return formatoFecha.format(fecha.getTime());
    }

    public void visualizardePlanEstudio(Document doc) {
        InputStream in = null;
//        StreamedContent str = null;
        try {
            this.documento = doc;
            in = okm.getContent(doc.getPath());
            streamedContent = new DefaultStreamedContent(in, "application/pdf");
            //-------
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            byte[] b = (byte[]) session.get("reportBytes");
            if (b != null) {
                streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
            }
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.update(":visualizacionPlanPdf");
            requestContext.execute("PF('visualizarPlanPDF').show()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cambiarArchivo() {
        exitoSubirArchivo = false;
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("formSeleccionarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoPlanEstudio");
        rc.update("formMetadatosPlanEstudio");

    }

    public void cambiarArchivoActualizacion() {
        exitoSubirArchivo = false;
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("formActualizarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoActualizarPlanEstudio");
        rc.update("formActualizarMetadatosPlanEstudio");

    }

    public boolean getComprobarConexionOpenKM() {
        boolean conexion = true;
        try {
            okm.getAppVersion();

        } catch (RepositoryException ex) {
            conexion = false;
        } catch (DatabaseException ex) {
            conexion = false;
        } catch (UnknowException ex) {
            conexion = false;
        } catch (WebserviceException ex) {
            conexion = false;
        }
        return conexion;
    }

}
