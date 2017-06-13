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

    /**
     * Atributos
     */
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

    /**
     * Constructor encargado de inicializar algunas de los atributos asignados a
     * la clase
     */
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

    /**
     * Metodo encargado de retornar a la vista PlanEstudio un objeto de tipo
     * streamedContet para poder visualizar un plan de estudio.
     *
     * @return retorna un objeto de tipo streamedContent.
     */
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

    /**
     * Metodo encargado de restaurar los metadatos del plan de estudio y
     * actualizar la vista de registro de un plan.
     */
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
     * se obtiene el archivo y nombre, acontinuacion se envia un mensaje de
     * exito a la vista de registro y se actualiza el campo del archivo en la
     * vista.
     *
     * @param event archivo cargado desde el computador.
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

    /**
     * Recibe como parametro el archivo cargado desde el computador, obtiene el
     * nombre, y el archivo. A continuacion se envia un mensaje de exito a la
     * vista de actualizacion y se actualiza el formulario.
     *
     * @param event archivo cargado desde el computador.
     */
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

    /**
     * Realiza un llamado al repositorio de openkm para verificar si existe el
     * directorio donde se van a guardar los planes de estudio, una ves
     * verificado esto, verifica si el plan de estudio a guardar existe,
     * realizadas estas verificaciones, si la carpeta no existe la crea, si el
     * plan de estudio existe enviar un mensaje de error a la vista, de lo
     * contrario realiza un llamado a openkm para guardar el plan de estudio,
     * guardado el plan le asigna los metadatos al plan de estudios creado.
     * Terminados estos pasos envia un mensaje de exito a la vista de
     * PlanEstudio, actualiza el formulario de registro y la vista que contiene
     * los planes de estudio(planEstudio).
     */
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
                okm.createFolderSimple("/okm:root/Coordinacion");
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

    /**
     * Recibe como parametro un plan de estudio, luego de esto obtiene los
     * metadatos del plan, los guarda en un objeto de metadatosPlanEstudio y una
     * variables auxiliares para poder saber si al momento de actualizar un plan
     * de estudio se han realizado cambios en los metadatos. Obtenidos los
     * metadatos del documento se actualiza la vista de ActualizaPlanEstudio
     * para mostrar los metadatos asociados al plan de estudio.
     *
     * @param document plan de estudio
     */
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

    /**
     * Verifica si se han realizado cambios en el documento de plan de estudio y
     * sus metadatos, si han habido cambios realiza un llamado a openkm para
     * guardar los cambios. Realizados estos cambios, reinicia los metadatos a
     * sus valores por defecto, actualiza y envia un mensaje de exito a la vista
     * PlanEstudio
     */
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
     * Recupera los planes de estudio contenidos en openkm
     */
    public void listaDocs() {
        try {

            if (existeCarpeta()) {
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

    /**
     * Limita la fecha de la vigencia del plan de estudio
     *
     * @return
     */
    public Date fechaActual() {
        return new Date();
    }

    /**
     * Reinicia los atributos del objeto metadatosPlanEstudio, y tambien elimina
     * el archivo subido desde el computador.
     */
    public void limpiarVariables() {
        metadatosPlandeEstudio = new MetadatosPlanEstudio();
        nombreArchivo = "";
        archivoPlan = null;
        exitoSubirArchivo = false;
    }

    /**
     * Verifica en openkm si existe o no el directorio donde se van a guardar
     * los planes de estudio. Si existe retorna true, de lo contrario retorna
     * false.
     *
     * @return retorna un valor true o false. existe en openkm
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @throws DatabaseException
     * @throws UnknowException
     * @throws WebserviceException
     */
    private boolean existeCarpeta() throws PathNotFoundException, RepositoryException, DatabaseException, UnknowException, WebserviceException {

        for (Folder fld : okm.getFolderChildren("/okm:root")) {

            if (fld.getPath().equalsIgnoreCase("/okm:root/Coordinacion")) {
                for (Folder f : okm.getFolderChildren("/okm:root/Coordinacion")) {
                    if (f.getPath().equalsIgnoreCase(rutaPlanesDeEstudio)) {//Buscar en openkm si existe la carpeta Planes de Estudio     

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Recibe como parametro el plan de estudios a descargar, obtiene la ruta de
     * este plan de estudios en openkm y recupera el archivo completo. Una ves
     * recuperado lo retorna a la vista PlanEstudio para poder ser descargado.
     *
     * @param document plan de estudios a descargar
     * @return Retorna un objeto de tipo StreamedContent
     */
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

    /**
     * Recibe como parametro la ruta del plan de estudio en openkm, luego
     * obtiene el nombre del plan de estudio de la ruta y retorna este nombre.
     *
     * @param path Ruta completa del plan de estudio en openkm
     * @return nombre del plan de estudio
     */
    public String nombreDelArchivo(String path) {
        String partesPath[] = path.split("/");
        return partesPath[partesPath.length - 1];
    }

    /**
     * Recibe como parametro la fecha de creacion de un plan de estudio, luego
     * de esto le da el formato establecido y la retorna a la vista para ser
     * mostrada.
     *
     * @param fecha fecha creacion plan de estudio
     * @return fecha con nuevo formato
     */
    public String fecha(Date fecha) {
        return formatoFecha.format(fecha.getTime());
    }

    /**
     * Recibe como parametro un plan de estudio. Una ves recibido realiza un
     * llamado a openkm y le pasa la ruta completa del plan para obtener el
     * docuemtno completo, recuperado el documeto lo envia al visor de
     * documentos para ser mostrado.
     *
     * @param doc plan de estudio
     */
    public void visualizardePlanEstudio(Document doc) {
        InputStream in = null;
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

    /**
     * Cambia de estado a la variable exitoSubirArchivo para poder mostrar en la
     * vista de registro de plan de estudio que no hay un archivo seleccionado,
     * luego de esto actualiza la vista de registro.
     */
    public void cambiarArchivo() {
        exitoSubirArchivo = false;
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("formSeleccionarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoPlanEstudio");
        rc.update("formMetadatosPlanEstudio");

    }

    /**
     * Cambia de estado a la variable exitoSubirArchivo para poder mostrar en la
     * vista de actualizacion de plan de estudio que no hay un archivo
     * seleccionado, luego de esto actualiza la vista de actualizacion.
     */
    public void cambiarArchivoActualizacion() {
        exitoSubirArchivo = false;
        RequestContext rc = RequestContext.getCurrentInstance();
        rc.update("formActualizarArchivoPlanEstudio");
        rc.update("formArchivoSelecionadoActualizarPlanEstudio");
        rc.update("formActualizarMetadatosPlanEstudio");

    }

    /**
     * Realiza un llamado a openkm para verificar si hay una comunicacion con el
     * repositorio, en caso de que haya comunicacion retorna true, de lo
     * contrario retorna false.
     *
     * @return valor booleano
     */
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

    /**
     * Recibe como parametro un plan de estudio, realiza un llamado a openkm
     * para eliminar el plan de estudio, al realizar este llamado se pasa la
     * ruta completa del plan. Una ves eliminado el plan de estudio se realiza
     * el llamado al metodo purgeTrash de openkm para eliminar el documento de
     * la papelera.
     *
     * @param doc plan de estudio
     */
    public void deleteDocument(Document doc) {
        try {
            okm.deleteDocument(doc.getPath());
            okm.purgeTrash();
            RequestContext requestContext = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "El archivo se elimino con exito!"));
            requestContext.update("formPlanesdeEstudio:mensajeEliminar");
            listaDocs();
            requestContext.update("lstPlanesEstudio");
        } catch (Exception e) {

        }
    }

}
