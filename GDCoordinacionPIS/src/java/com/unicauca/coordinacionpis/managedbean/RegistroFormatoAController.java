/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unicauca.coordinacionpis.managedbean;

import com.unicauca.coordinacionpis.classMetadatos.MetadatosOfertaAcademica;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
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

import com.unicauca.coordinacionpis.classMetadatos.MetadatosAntepoyecto;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ROED26
 */
@Named(value = "registroFormatoAController")
@SessionScoped
public class RegistroFormatoAController implements Serializable {

    private MetadatosAntepoyecto metadatosAnteproyectos;
    private boolean exitoSubirArchivo;
    private String nombreArchivo;
    private UploadedFile archivOferta;
    private List<QueryResult> documentosFormatoA;
    String url = "http://localhost:8080/OpenKM";
    String user = "okmAdmin";
    String pass = "admin";
    OKMWebservices okm = OKMWebservicesFactory.newInstance(url, user, pass);
    private SimpleDateFormat formatoFecha;

    public RegistroFormatoAController() {
        this.formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        metadatosAnteproyectos = new MetadatosAntepoyecto();
        metadatosAnteproyectos.setViabilidad("Si");
    }

    @PostConstruct
    public void init() {

    }

    public MetadatosAntepoyecto getMetadatosAnteproyectos() {
        return metadatosAnteproyectos;
    }

    public void setMetadatosAnteproyectos(MetadatosAntepoyecto metadatosAnteproyectos) {
        this.metadatosAnteproyectos = metadatosAnteproyectos;
    }

    
  

    public boolean isExitoSubirArchivo() {
        return exitoSubirArchivo;
    }

    public void setExitoSubirArchivo(boolean exitoSubirArchivo) {
        this.exitoSubirArchivo = exitoSubirArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public SimpleDateFormat getFormatoFecha() {
        return formatoFecha;
    }

    public void setFormatoFecha(SimpleDateFormat formatoFecha) {
        this.formatoFecha = formatoFecha;
    }

    public List getListaDocs() {
        try {
            documentosFormatoA = okm.findByName("");
        } catch (IOException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentosFormatoA;
    }

    public void seleccionarArchivo(FileUploadEvent event) {
        nombreArchivo = event.getFile().getFileName();
        archivOferta = event.getFile();
        FacesMessage message = new FacesMessage("El archivo", event.getFile().getFileName() + " se selecciono con éxito");
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("messages");
        exitoSubirArchivo = true;
        requestContext.update("formSeleccionarArchivoFormatoA");
        requestContext.update("formMetadatosFormatoA");
        requestContext.update("formArchivoSelecionadoFormatoA");
    }

    public void cambiarArchivo() {
        exitoSubirArchivo = false;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("formSeleccionarArchivoFormatoA");
        requestContext.update("formMetadatosFormatoA");
        requestContext.update("formArchivoSelecionadoFormatoA");
    }

    public void cancelarFormatoA() {
        exitoSubirArchivo = false;
        nombreArchivo = "";
        metadatosAnteproyectos = new MetadatosAntepoyecto();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("formSeleccionarArchivoFormatoA");
        requestContext.update("formMetadatosFormatoA");
        requestContext.execute("PF('dlgRegistroFormatoA').hide()");
        requestContext.update("formArchivoSelecionadoFormatoA");
    }

    public void aceptarFormatoA() {

        boolean existe = false;
        Document okmDocument = new Document();
        try {
            for (Folder fld : okm.getFolderChildren("/okm:root")) {
                System.out.println("Fodler -> " + fld.getPath());
                if(fld.getPath().equalsIgnoreCase("okm:root/FormatoA")){
                
                    existe = true;
                }
            }
            //okm.createFolderSimple("/okm:root/ofertaAcademica");

//            okm.createDocumentSimple("/okm:root/ofertaAcademica/"+archivOferta.getFileName(),archivOferta.getInputstream());
            //if (!okm.isValidFolder("/okm:root/Oferta academica")) {
            //okm.createFolderSimple("/okm:personal/okmAdmin/oferta acedemica");
            if (!existe) {
                okm.createFolderSimple("/okm:root/FormatoA");
                okm.createDocumentSimple("/okm:root/FormatoA/" + archivOferta.getFileName(), archivOferta.getInputstream());
            } else {
                okm.createDocumentSimple("/okm:root/FormatoA/" + archivOferta.getFileName(), archivOferta.getInputstream());
            }
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
        agregarMetadatos();
    }

    public void agregarMetadatos() {
        // create document and writer
        Document document = new Document(PageSize.A4);
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\aguaabril2016.pdf"));
            // add meta-data to pdf
            document.addAuthor("Memorynotfound");
            document.addCreationDate();
            document.addCreator("Memorynotfound.com");
            document.addTitle("Add meta data to PDF");
            document.addSubject("how to add meta data to pdf using itext");
            document.addKeywords(metadatosAnteproyectos.getTitulo()+ "," + metadatosAnteproyectos.getProfesor());
            document.addLanguage(Locale.ENGLISH.getLanguage());
            document.addHeader("type", "tutorial, example");

            // add xmp meta data
            writer.createXmpMetadata();

            document.open();
            document.add(new Paragraph("Add meta-data to PDF using iText"));
            document.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String nombreDelArchivo(String path) {

        String partesPath[] = path.split("/");
        return partesPath[partesPath.length - 1];
    }

    public String fecha(Calendar fecha) {
        return formatoFecha.format(fecha.getTime());
    }
}
