/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.managedbean;

import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Document;
import com.openkm.sdk4j.bean.Folder;
import com.openkm.sdk4j.bean.QueryResult;
import com.openkm.sdk4j.exception.AccessDeniedException;
import com.openkm.sdk4j.exception.AutomationException;
import com.openkm.sdk4j.exception.DatabaseException;
import com.openkm.sdk4j.exception.ExtensionException;
import com.openkm.sdk4j.exception.FileSizeExceededException;
import com.openkm.sdk4j.exception.ItemExistsException;
import com.openkm.sdk4j.exception.ParseException;
import com.openkm.sdk4j.exception.PathNotFoundException;
import com.openkm.sdk4j.exception.RepositoryException;
import com.openkm.sdk4j.exception.UnknowException;
import com.openkm.sdk4j.exception.UnsupportedMimeTypeException;
import com.openkm.sdk4j.exception.UserQuotaExceededException;
import com.openkm.sdk4j.exception.VirusDetectedException;
import com.openkm.sdk4j.exception.WebserviceException;
import com.unicauca.coordinacionpis.classMetadatos.MetadatosPlanEstudio;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
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
    private UploadedFile archivoPlan;
    private String datos;
    private List<Document> documentosPlanEstudio;
    String url = "http://localhost:8080/OpenKM";
    String user = "okmAdmin";
    String pass = "admin";

    OKMWebservices okm = OKMWebservicesFactory.newInstance(url, user, pass);

    public RegistroPlandeEstudioController() {
        metadatosPlandeEstudio = new MetadatosPlanEstudio();
        documentosPlanEstudio = new ArrayList<>();
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
//Al presionar el boton cancelar, se borran los datos ingresados en el formulario y se actualiza el formulario.

    public void cancelarRegistroPlanEstudio() {
        metadatosPlandeEstudio = new MetadatosPlanEstudio();
        limpiarVariables();
        RequestContext rc = RequestContext.getCurrentInstance();
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
        archivoPlan = event.getFile();
        FacesMessage msg = new FacesMessage("El archivo", nombreArchivo + " se seleccionó con exito");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("msg");//Actualiza la etiqueta growl para que el mensaje pueda ser mostrado

    }

    public void aceptarRegistroPlanEstudio() {
        RequestContext rc = RequestContext.getCurrentInstance();
        FacesMessage message = null;

        try {
            boolean existeFolder = false;
            boolean existeDocumento = false;

            existeFolder = existeCarpeta();

            if (existeFolder) {//Si existe la carpeta, busca el documento
                for (com.openkm.sdk4j.bean.Document doc : okm.getDocumentChildren("/okm:root/Planes de Estudio")) {
                    if (doc.getPath().equalsIgnoreCase("/okm:root/Planes de Estudio/" + nombreArchivo)) {//Buscar en openkm si existe el archivo a guardar
                        existeDocumento = true;
                    }
                }
            }
            if (existeFolder) {//Si existe la carpeta Planes de Estudio, crea dentro de ella el documento
                if (!existeDocumento) {
                    okm.createDocumentSimple("/okm:root/Planes de Estudio/" + nombreArchivo, archivoPlan.getInputstream());//Crear el documento en openkm
                    message = new FacesMessage("El archivo", nombreArchivo + " se registro con exito");
                } else {
                    message = new FacesMessage("Error al registrar el archivo", nombreArchivo);
                }
            } else {//Si la carpeta no existe, la crea y dentro de ella crea el documento.
                okm.createFolderSimple("/okm:root/Planes de Estudio");//Crear carpeta Planes de Estudio en openkm
                okm.createDocumentSimple("/okm:root/Planes de Estudio/" + nombreArchivo, archivoPlan.getInputstream());//Crear el documento dentro de la carpeta Planes de Estudio en openkm
                message = new FacesMessage("El archivo", nombreArchivo + " se registro con exito");
            }
            FacesContext.getCurrentInstance().addMessage(null, message);
            rc.update("formMetadatosPlanEstudio");//Actualizar el formulario de registro
            rc.execute("PF('dlgRegistroPlandeEstudio').hide()");//Cerrar el dialog que contiene el formulario
            limpiarVariables();
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
        }
    }

    /**
     * Obtener los planes de estudio contenidos en openkm
     */
    public void listaDocs() {
        try {
            if (existeCarpeta()) {
                documentosPlanEstudio.clear();
                documentosPlanEstudio = okm.getDocumentChildren("/okm:root/Planes de Estudio");//Obtener los Planes de Estudio contenidos en openkm          
            }
        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PathNotFoundException ex) {
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
    }

    private boolean existeCarpeta() throws PathNotFoundException, RepositoryException, DatabaseException, UnknowException, WebserviceException {

        for (Folder fld : okm.getFolderChildren("/okm:root")) {
            if (fld.getPath().equalsIgnoreCase("/okm:root/Planes de Estudio")) {//Buscar en openkm si existe la carpeta Planes de Estudio
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

    public StreamedContent stream(Document doc) {
        InputStream in = null;
        StreamedContent str = null;
        try {
            
            in = okm.getContent(doc.getPath());
            str = new DefaultStreamedContent(in, "application/pdf");
            //-------
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            byte[] b = (byte[]) session.get("reportBytes");
            if (b != null) {
                str = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
            }   return str;
        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PathNotFoundException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AccessDeniedException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(RegistroPlandeEstudioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return str;
    }
}
