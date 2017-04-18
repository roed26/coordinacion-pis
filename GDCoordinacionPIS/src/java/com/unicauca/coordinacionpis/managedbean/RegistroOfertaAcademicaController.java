package com.unicauca.coordinacionpis.managedbean;

import com.itextpdf.text.BaseColor;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.openkm.sdk4j.OKMWebservices;
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
import com.unicauca.coordinacionpis.entidades.Materia;
import com.unicauca.coordinacionpis.entidades.Departamento;
import com.unicauca.coordinacionpis.sessionbean.DepartamentoFacade;
import com.unicauca.coordinacionpis.utilidades.ConexionOpenKM;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
    private List<QueryResult> documentosOfertasAcademicas;
    private List<com.openkm.sdk4j.bean.Document> listadoDocsOfertasAcademicas;
    private ConexionOpenKM conexionOpenKM;
    private OKMWebservices okm;
    private SimpleDateFormat formatoFecha;
    private SimpleDateFormat formatoFechaDocumento;
    private StreamedContent streamedContent;
    private List<Departamento> listaDepartamentos;
    private com.openkm.sdk4j.bean.Document documento;
    private InputStream stream;
    private boolean registroInicialOferta;

    public RegistroOfertaAcademicaController() {
        this.formatoFecha = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.formatoFechaDocumento = new SimpleDateFormat("dd-MM-yyyy");
        datos = "";
        this.listaDepartamentos = new ArrayList<>();
        listadoDocsOfertasAcademicas = new ArrayList<>();
        this.conexionOpenKM = new ConexionOpenKM();
        this.okm = conexionOpenKM.getOkm();
        registroInicialOferta = true;

    }

    @PostConstruct
    public void init() {
        listaDepartamentos = ejbDepartamento.findAll();
        periodoOfertaAcademica = asignarPeriodo();

        try {

            InputStream in = okm.getContent(documento.getPath());
            streamedContent = new DefaultStreamedContent(in, "application/pdf");
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            byte[] b = (byte[]) session.get("reportBytes");
            if (b != null) {
                streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
            }
        } catch (Exception e) {
        }
    }

    public boolean isRegistroInicialOferta() {
        return registroInicialOferta;
    }

    public void setRegistroInicialOferta(boolean registroInicialOferta) {
        this.registroInicialOferta = registroInicialOferta;
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
        if (FacesContext.getCurrentInstance().getRenderResponse()) {
            return new DefaultStreamedContent();
        } else {
            return streamedContent;
        }
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public List<Departamento> getListaDepartamentos() {
        listaDepartamentos = ejbDepartamento.findAll();
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

    public com.openkm.sdk4j.bean.Document getDocumento() {
        return documento;
    }

    public void setDocumento(com.openkm.sdk4j.bean.Document documento) {
        this.documento = documento;
    }

    public List<com.openkm.sdk4j.bean.Document> getListadoOfertasAcademicas() {
        listadoDocsOfertasAcademicas.clear();
        try {
            List<QueryResult> lista = okm.findByName(datos);
            for (int i = 0; i < lista.size(); i++) {
                String[] pathDividido = lista.get(i).getDocument().getPath().split("/");
                String path = "/" + pathDividido[1] + "/" + pathDividido[2];
                if (path.equalsIgnoreCase("/okm:root/Oferta academica")) {
                    listadoDocsOfertasAcademicas.add(lista.get(i).getDocument());
                }
            }
            /*listadoDocsOfertasAcademicas
                    = okm.getDocumentChildren("/okm:root/Oferta academica");*/

        } catch (RepositoryException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DatabaseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknowException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebserviceException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(RegistroOfertaAcademicaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listadoDocsOfertasAcademicas;
    }

    public List<QueryResult> getListaDocs() {
        try {
            documentosOfertasAcademicas = okm.findByName(datos);

            /* if(datos.equalsIgnoreCase("")){
                documentosOfertasAcademicas = okm.;
            }else{
            documentosOfertasAcademicas = okm.findByName(datos);
            }*/
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
            if (registroInicialOferta) {
                generarPDFPre();
                File initialFile = new File("D:\\" + periodoOfertaAcademica + "-pre.pdf");
                InputStream targetStream = new FileInputStream(initialFile);
                if (!existeFolder) {
                    okm.createFolderSimple("/okm:root/Oferta academica");
                    okm.createDocumentSimple("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pre.pdf", targetStream);
                } else {
                    okm.createDocumentSimple("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pre.pdf", targetStream);
                }
                if (!existeCategoria) {
                    okm.createFolderSimple("/okm:categories/Oferta academica");
                    okm.addCategory("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pre.pdf", "/okm:categories/Oferta academica");
                } else {
                    okm.addCategory("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pre.pdf", "/okm:categories/Oferta academica");
                }
            } else {
                generarPDFPos();
                File initialFile = new File("D:\\" + periodoOfertaAcademica + "-pos.pdf");
                InputStream targetStream = new FileInputStream(initialFile);
                if (!existeFolder) {
                    okm.createFolderSimple("/okm:root/Oferta academica");
                    okm.createDocumentSimple("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pos.pdf", targetStream);
                } else {
                    okm.createDocumentSimple("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pos.pdf", targetStream);
                }
                if (!existeCategoria) {
                    okm.createFolderSimple("/okm:categories/Oferta academica");
                    okm.addCategory("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pos.pdf", "/okm:categories/Oferta academica");
                } else {
                    okm.addCategory("/okm:root/Oferta academica/" + periodoOfertaAcademica + "-pos.pdf", "/okm:categories/Oferta academica");
                }
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

    public void registroInicial(ValueChangeEvent e) {
        if (e.getNewValue().equals("Si")) {
            registroInicialOferta = true;
        } else {
            registroInicialOferta = false;
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

    private Document generarPDFPre() {

        Document document = new Document(PageSize.A4);
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\" + periodoOfertaAcademica + "-pre.pdf"));
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
                document.add(new Paragraph(listaDepartamentos.get(i).getNombre()));
                document.add(new Paragraph("\n"));
                document.add(crearTablaCursoPorDepartamento(listaDepartamentos.get(i)));

                document.add(new Paragraph("\n"));
            }
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
    
    private Document generarPDFPos() {

        Document document = new Document(PageSize.A4);
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\" + periodoOfertaAcademica + "-pos.pdf"));
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
                document.add(new Paragraph(listaDepartamentos.get(i).getNombre()));
                document.add(new Paragraph("\n"));
                document.add(crearTablaCursoPorDepartamento(listaDepartamentos.get(i)));

                document.add(new Paragraph("\n"));
            }
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

        PdfPTable table;

        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        //cell = new PdfPCell(new Phrase(departamento.getNombre()));
        //cell.setColspan(3);
        //cell.setBackgroundColor(BaseColor.GRAY);
        //table.addCell(cell);
        // now we add a cell with rowspan 2
        if (listadoCursos.get(0).getSemestre() != null) {
            float[] columnWidths = {2, 5, 7, 5, 5};
            table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);
            cell = new PdfPCell(new Phrase("Sem"));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        } else {
            table = new PdfPTable(4);
        }
        cell = new PdfPCell(new Phrase("Código materia"));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Nombre materia"));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Numero aprox. estudiantes"));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Grupos solicitados"));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
        /*table.addCell("Nombre materia");
        table.addCell("Numero aprox. estudiantes");
        table.addCell("Cursos solicitados");*/
        for (int i = 0; i < listadoCursos.size(); i++) {
            if (listadoCursos.get(i).getSemestre() != null) {
                table.addCell(listadoCursos.get(i).getSemestre());
            }
            table.addCell(listadoCursos.get(i).getCodigoMateria());
            table.addCell(listadoCursos.get(i).getNombreMateria());
            table.addCell("" + listadoCursos.get(i).getNumeroEstudiantes());
            table.addCell("" + listadoCursos.get(i).getGruposSolicitados());
        }

        return table;
    }

    /*public StreamedContent descargarDocumento(QueryResult queryResult) {
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
    }*/
    public StreamedContent descargarDocumento(com.openkm.sdk4j.bean.Document queryResult) {
        StreamedContent file = null;
        com.openkm.sdk4j.bean.Document doc = queryResult;
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

    public void visualizarDocumento(com.openkm.sdk4j.bean.Document documento) {

        try {
            this.documento = documento;
            InputStream in = okm.getContent(documento.getPath());
            streamedContent = new DefaultStreamedContent(in, "application/pdf");
            //-------
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            byte[] b = (byte[]) session.get("reportBytes");
            if (b != null) {
                streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
            }

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.update("form:visualizacionPdf");
            requestContext.execute("PF('visualizarPDF').show()");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public StreamedContent getVerPdf() {

        try {

            /*InputStream in = okm.getContent(this.documento.getPath());

            //streamedContent = new DefaultStreamedContent(in, "application/pdf");
            streamedContent = new DefaultStreamedContent(in, "application/pdf", nombreDelArchivo(documento.getPath()));*/
            InputStream in = okm.getContent(documento.getPath());
            streamedContent = new DefaultStreamedContent(in, "application/pdf");
            //-------
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            byte[] b = (byte[]) session.get("reportBytes");
            if (b != null) {
                streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(b), "application/pdf");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return streamedContent;
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
