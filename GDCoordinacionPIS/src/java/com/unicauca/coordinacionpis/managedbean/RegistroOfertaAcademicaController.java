package com.unicauca.coordinacionpis.managedbean;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
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
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.unicauca.coodinacionpis.appScan.scanner;
import com.unicauca.coodinacionpis.entidades.Materia;
import com.unicauca.coodinacionpis.entidades.Departamento;
import com.unicauca.coordinacionpis.sessionbean.DepartamentoFacade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.apache.commons.io.IOUtils;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ROED26
 */
@Named(value = "registroOfertaAcademicaController")
@SessionScoped
public class RegistroOfertaAcademicaController implements Serializable {

    @EJB
    private DepartamentoFacade ejbDepartamento;

    private boolean exitoSubirArchivo = true;
    private String nombreArchivo;
    private String datos;
    private String periodoOfertaAcademica;
    private UploadedFile archivOferta;
    private List<QueryResult> documentosOfertasAcademicas;
    String url = "http://localhost:8080/OpenKM";
    String user = "okmAdmin";
    String pass = "admin";
    OKMWebservices okm = OKMWebservicesFactory.newInstance(url, user, pass);
    private SimpleDateFormat formatoFecha;
    private SimpleDateFormat formatoFechaDocumento;
    private StreamedContent streamedContent;
    private List<Departamento> listaDepartamentos;

    public RegistroOfertaAcademicaController() {
        this.formatoFecha = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.formatoFechaDocumento = new SimpleDateFormat("dd-MM-yyyy");
        datos = "";
        this.listaDepartamentos = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        listaDepartamentos = ejbDepartamento.findAll();
        periodoOfertaAcademica = asignarPeriodo();
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

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public List<Departamento> getListaDepartamentos() {
        return listaDepartamentos;
    }

    public void setListaDepartamentos(List<Departamento> listaDepartamentos) {
        this.listaDepartamentos = listaDepartamentos;
    }

    public String getPeriodoOfertaAcademica() {
        return periodoOfertaAcademica;
    }

    public void setPeriodoOfertaAcademica(String periodoOfertaAcademica) {
        this.periodoOfertaAcademica = periodoOfertaAcademica;
    }

    public List<QueryResult> getListaDocs() {
        try {
            documentosOfertasAcademicas = okm.findByName(datos);
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error de conexión."));
        }

        return documentosOfertasAcademicas;
    }

    public void seleccionarArchivo(FileUploadEvent event) {
        nombreArchivo = event.getFile().getFileName();
        archivOferta = event.getFile();
        FacesMessage message = new FacesMessage("El archivo", event.getFile().getFileName() + " se selecciono con exito");
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("messages");
        exitoSubirArchivo = true;
        requestContext.update("formSeleccionarArchivo");
        requestContext.update("formMetadatosOfertaAcademica");
        requestContext.update("formArchivoSelecionado");

    }

    public void cambiarArchivo() {
        exitoSubirArchivo = true;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("formSeleccionarArchivo");
        requestContext.update("formMetadatosOfertaAcademica");
        requestContext.update("formArchivoSelecionado");
    }

    public void cancelarRegistroDeOferta() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update("formMetadatosOfertaAcademica");
        requestContext.execute("PF('dlgRegistroOfertaAcedemica').hide()");
    }

    public void aceptarRegistroDeOferta() {

        Document okmDocument = new Document();
        generarPDF();

        try {
            boolean existeFolder = false;
            boolean existeCategoria = false;
            for (Folder fld : okm.getFolderChildren("/okm:root")) {
                if (fld.getPath().equalsIgnoreCase("/okm:root/Oferta academica")) {
                    existeFolder = true;
                }
            }
            for (Folder folder : okm.getFolderChildren("/okm:categories")) {
                if (folder.getPath().equalsIgnoreCase("/okm:categories/Oferta academica")) {
                    existeCategoria = true;
                }
            }
            File initialFile = new File("D:\\" + periodoOfertaAcademica + ".pdf");
            InputStream targetStream = new FileInputStream(initialFile);
            if (!existeFolder) {
                okm.createFolderSimple("/okm:root/Oferta academica");
                okm.createDocumentSimple("/okm:root/Oferta academica/" + periodoOfertaAcademica + ".pdf", targetStream);
            } else {
                okm.createDocumentSimple("/okm:root/Oferta academica/" + periodoOfertaAcademica + ".pdf", targetStream);
            }
            if (!existeCategoria) {
                okm.createFolderSimple("/okm:categories/Oferta academica");
                okm.addCategory("/okm:root/Oferta academica/" + periodoOfertaAcademica + ".pdf", "/okm:categories/Oferta academica");
            } else {
                okm.addCategory("/okm:root/Oferta academica/" + periodoOfertaAcademica + ".pdf", "/okm:categories/Oferta academica");
            }

            exitoSubirArchivo = true;
            nombreArchivo = "";
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.update("formSeleccionarArchivo");
            requestContext.update("formMetadatosOfertaAcademica");
            requestContext.update("formArchivoSelecionado");

            documentosOfertasAcademicas = okm.findByName("");
            requestContext.update("datalist");
            requestContext.execute("PF('dlgRegistroOfertaAcedemica').hide()");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "La información se registro con exito."));
            requestContext.execute("PF('mensajeRegistroExitoso').show()");
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
        } catch (ParseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VersionException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LockException ex) {
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

    /*
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
            document.addKeywords(metadatosOfertaAcademica.getPeriodoAcademico() + ","
                    + "," + metadatosOfertaAcademica.getNumCursosCongelados());
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
     */
    public String nombreDelArchivo(String path) {
        String partesPath[] = path.split("/");
        return partesPath[partesPath.length - 1];
    }

    public String fecha(Calendar fecha) {
        return formatoFecha.format(fecha.getTime());
    }

    private Document generarPDF() {
        Document document = new Document(PageSize.A4);
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\" + periodoOfertaAcademica + ".pdf"));
            // add meta-data to pdf
            document.addAuthor("Memorynotfound");
            document.addCreationDate();
            document.addCreator("Memorynotfound.com");
            document.addTitle("Add meta data to PDF");
            document.addSubject("how to add meta data to pdf using itext");
            document.addKeywords(periodoOfertaAcademica);
            document.addHeader("type", "tutorial, example");

            // add xmp meta data
            writer.createXmpMetadata();

            document.open();
            Image img = Image.getInstance("D:\\unicauca.png");
            document.add(img);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Popayán, " + formatoFechaDocumento.format(asignarFecha())));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Decano FIET"));
            document.add(new Paragraph("Universidad del cauca"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            for (int i = 0; i < listaDepartamentos.size(); i++) {
                document.add(crearTablaCursoPorDepartamento(listaDepartamentos.get(i)));
                document.add(new Paragraph("\n"));
            }
            /*document.add(createFirstTable());
            document.add(new Paragraph("\n"));
            document.add(createFirstTableFisica());
            document.add(new Paragraph("\n"));
            document.add(createFirstTableHumanidades());
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));
            document.add(createFirstTableContables());
            document.add(new Paragraph("\n"));
            document.add(createFirstTableDerecho());*/

            document.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return document;
    }

    public PdfPTable crearTablaCursoPorDepartamento(Departamento departamento) {
        List<Materia> listadoCursos = departamento.getMateriaList();
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase(departamento.getNombre()));
        cell.setColspan(3);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);
        // now we add a cell with rowspan 2

        table.addCell("Asignatura");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");
        for (int i = 0; i < listadoCursos.size(); i++) {
            table.addCell(listadoCursos.get(i).getNombreMateria());
            table.addCell("" + listadoCursos.get(i).getNumeroEstudiantes());
            table.addCell("" + listadoCursos.get(i).getGruposSolicitados());
        }

        return table;
    }

    /*
    public PdfPTable createFirstTable() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Depatemaneto de Matemáticas"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2

        table.addCell("Asignatura");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");
        table.addCell("Calculo I ");
        table.addCell(metadatosOfertaAcademica.getEstudiantesCalculoI());
        table.addCell(metadatosOfertaAcademica.getSolicitadosCalculoI());
        table.addCell("Calculo II");
        table.addCell(metadatosOfertaAcademica.getEstudiantesCalculoII());
        table.addCell(metadatosOfertaAcademica.getSolicitadosCalculoII());
        table.addCell("Calculo III");
        table.addCell(metadatosOfertaAcademica.getEstudiantesCalculoIII());
        table.addCell(metadatosOfertaAcademica.getSolicitadosCalculoIII());
        table.addCell("Álgebra Lineal");
        table.addCell(metadatosOfertaAcademica.getEstudiantesLineal());
        table.addCell(metadatosOfertaAcademica.getSolicitadosLineal());
        table.addCell("Ecuaciones diferenciales");
        table.addCell(metadatosOfertaAcademica.getEstudiantesEcuaciones());
        table.addCell(metadatosOfertaAcademica.getSolicitadosEcuaciones());
        table.addCell("Estadistica");
        table.addCell(metadatosOfertaAcademica.getEstudiantesEstadistica());
        table.addCell(metadatosOfertaAcademica.getSolicitadosEstadistica());
        return table;
    }

    public PdfPTable createFirstTableFisica() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Depatemaneto de Física"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2

        table.addCell("Asignatura");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");
        table.addCell("Mecánica");
        table.addCell(metadatosOfertaAcademica.getEstudiantesMecanica());
        table.addCell(metadatosOfertaAcademica.getSolicitadosMecanica());
        table.addCell("Electromagnetismo");
        table.addCell(metadatosOfertaAcademica.getEstudiantesElectro());
        table.addCell(metadatosOfertaAcademica.getSolicitadosElectro());
        table.addCell("Laboratorio de mecánica");
        table.addCell(metadatosOfertaAcademica.getEstudiantesLabMecanica());
        table.addCell(metadatosOfertaAcademica.getSolicitadosLabMecanica());
        table.addCell("Vibraciones y ondas");
        table.addCell(metadatosOfertaAcademica.getEstudiantesOndas());
        table.addCell(metadatosOfertaAcademica.getSolicitadosOndas());
        table.addCell("Laboratorio de electromagnetismo");
        table.addCell(metadatosOfertaAcademica.getEstudiantesLabElectro());
        table.addCell(metadatosOfertaAcademica.getSolicitadosLabElectro());

        return table;
    }

    public PdfPTable createFirstTableHumanidades() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Humanidades"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2

        table.addCell("Asignatura");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");
        table.addCell("Lectura y escritura");
        table.addCell(metadatosOfertaAcademica.getEstudiantesLecto());
        table.addCell(metadatosOfertaAcademica.getSolicitadosLecto());
        table.addCell("Electiva FISH I");
        table.addCell(metadatosOfertaAcademica.getEstudiantesFishI());
        table.addCell(metadatosOfertaAcademica.getSolicitadosFishI());
        table.addCell("Electiva FISH II");
        table.addCell(metadatosOfertaAcademica.getEstudiantesFishII());
        table.addCell(metadatosOfertaAcademica.getSolicitadosFishII());
        table.addCell("Electiva FISH III");
        table.addCell(metadatosOfertaAcademica.getEstudiantesFishIII());
        table.addCell(metadatosOfertaAcademica.getSolicitadosFishIII());
        table.addCell("Ética");
        table.addCell(metadatosOfertaAcademica.getEstudiantesEtica());
        table.addCell(metadatosOfertaAcademica.getSolicitadosEtica());

        return table;
    }

    public PdfPTable createFirstTableContables() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Facultad de ciencias contables"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2

        table.addCell("Asignatura");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");
        table.addCell("Investigación de operaciones");
        table.addCell(metadatosOfertaAcademica.getEstudiantesInvoper());
        table.addCell(metadatosOfertaAcademica.getSolicitadosInvoper());
        table.addCell("Fundamentos de economía");
        table.addCell(metadatosOfertaAcademica.getEstudiantesFundamentos());
        table.addCell(metadatosOfertaAcademica.getSolicitadosFundamentos());
        table.addCell("Gestión Empresarial");
        table.addCell(metadatosOfertaAcademica.getEstudiantesGestion());
        table.addCell(metadatosOfertaAcademica.getSolicitadosGestion());

        return table;
    }

    public PdfPTable createFirstTableDerecho() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Facultad de Derecho"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2

        table.addCell("Asignatura");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");
        table.addCell("Legislación laboral");
        table.addCell(metadatosOfertaAcademica.getEstudiantesLegislacion());
        table.addCell(metadatosOfertaAcademica.getSolicitadosLegislacion());

        return table;
    }*/
    public StreamedContent descargarDocumento(QueryResult queryResult) {
        StreamedContent file = null;
        com.openkm.sdk4j.bean.Document doc = queryResult.getDocument();
        try {
            InputStream is = okm.getContent(doc.getPath());
            file = new DefaultStreamedContent(is,"application/pdf",nombreDelArchivo(doc.getPath()));
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

    public void visualizarDocumento() {

        try {

            /* com.openkm.sdk4j.bean.Document documento = queryResult.getDocument();
            OutputStream out = new ByteArrayOutputStream();
            //PdfWriter writer = PdfWriter.getInstance(documento, out);
            out.close();

            InputStream in = new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());

            streamedContent = new DefaultStreamedContent(in, "application/pdf");

            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            byte[] b = (byte[]) session.get("reportBytes");
            if (b != null) {
                streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
            }*/
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            OutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            PdfPTable table = new PdfPTable(1);
            PdfPCell cell = new PdfPCell(new Phrase("First PDF"));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
            table.addCell(cell);
            doc.add(table);
            doc.close();
            out.close();
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute("PF('visualizarPDF').show()");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Date asignarFecha() {
        GregorianCalendar c = new GregorianCalendar();
        Date fechaActual = c.getTime();
        return fechaActual;

    }

    private String asignarPeriodo() {
        GregorianCalendar c = new GregorianCalendar();
        String anio = "" + c.get(Calendar.YEAR);
        String periodo = "";

        switch ("" + c.get(Calendar.MONTH)) {
            case "1":
                periodo = "I";
                break;
            case "2":
                periodo = "I";
                break;
            case "3":
                periodo = "I";
                break;
            case "4":
                periodo = "I";
                break;
            case "5":
                periodo = "I";
                break;
            case "6":
                periodo = "I";
                break;
            case "7":
                periodo = "II";
                break;
            case "8":
                periodo = "II";
                break;
            case "9":
                periodo = "II";
                break;
            case "10":
                periodo = "II";
                break;
            case "11":
                periodo = "II";
                break;
            case "12":
                periodo = "II";
                break;

        }
        return anio + "-" + periodo;
    }

}
