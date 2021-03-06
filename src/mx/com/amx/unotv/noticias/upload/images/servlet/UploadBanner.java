package mx.com.amx.unotv.noticias.upload.images.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.noticias.upload.images.bo.ProcesoBO;
import mx.com.amx.unotv.noticias.upload.images.dto.ParametrosDTO;
import mx.com.amx.unotv.noticias.upload.images.util.CargaProperties;
import mx.com.amx.unotv.noticias.upload.images.util.Scalr;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;


public class UploadBanner extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       	
	private final Logger logger = Logger.getLogger(UploadBanner.class);
	
    public UploadBanner() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Intento de peticion Get");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();	      
		try {			
			logger.info("UploadBanner doPost");
			CargaProperties cargaProperties=new CargaProperties();
	        ParametrosDTO parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
	        String result = "";
	        boolean success = parametrosDTO.getPathLocalImagenes() != null && parametrosDTO.getPathLocalImagenes().length()>0?true:false;
        	if(success)
        		result = procesaFicheros(request, parametrosDTO);
        	else 
        		result = null;
        	if( result != null ) {	        		        		
        		out.print( result );
        		return;
        	}	        	
	        out.print( "ERROR" );
		}catch(Exception ex){
			logger.error("Ocurrio un error subiendo el archivo: " + ex.getMessage() , ex);
			 out.print( "ERROR" );
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public String procesaFicheros(HttpServletRequest req, ParametrosDTO parametrosDTO) {
        try {
        	        	        	
        	//long maxBites = 1024*parametrosDTO.getMaxKBImagenes();
            DiskFileUpload fu = new DiskFileUpload();
            fu.setSizeMax(1024*1024*parametrosDTO.getMaxMegas()); //50 es el numero de megas
            fu.setSizeThreshold(1024*1024*parametrosDTO.getMaxMegas());
            fu.setRepositoryPath("/tmp");
            List<FileItem> fileItems = fu.parseRequest(req);
            if(fileItems == null) {
                return null;
            }
            Iterator<FileItem> i = fileItems.iterator();
            FileItem actual = null;
            String fileName = "";            
            while (i.hasNext()) {
                actual = (FileItem)i.next();                
                fileName = actual.getName();
                int uu = 0;               
                if( fileName != null && !fileName.equals("") && actual.getFieldName().equals("nameFile") ) {                	
                	File fichero = new File(fileName);
	                logger.info("El nombre del fichero es :" + fichero.getName());
	                long sizeFile = actual.getSize();
	                if(parametrosDTO.getExtFiles() != null && parametrosDTO.getExtFiles().length >1) {
	                	String [] ext = fichero.getName().split("\\.");
	                	for(int hh = 0;  hh<parametrosDTO.getExtFiles().length; hh++) {
	                		if(parametrosDTO.getExtFiles()[hh].trim().equals(ext[ext.length-1]))
	                			uu++;
	                		}	                	                
	                } else { 
	                	uu=1;
	                }
	                
	                if(uu>0) {
	                	
	                		String directorio = parametrosDTO.getPathLocalBanners();
	                		boolean continuar = false;
	                		Integer secuencia = 0;
	                		try { 
	                			
								ProcesoBO procesoBo = new ProcesoBO();
								secuencia = procesoBo.getSecuencia("",parametrosDTO.getURL_WS_BASE());
								//directorio += secuencia  + "/";
								continuar = createFolders(directorio);
	                		} catch (Exception e) {
	                			continuar = false;
	                			secuencia = 0;
	                			logger.error("Error al crear las carpetas: " + e.getMessage());
	                		}
	                		if(continuar) {	                		
		                		String extFile = "";
	                			try { 
	                				String nam[] = actual.getName().split("\\.");
	                				extFile = nam[nam.length-1];		                				
	                			} catch (Exception e) {
	                				extFile = "jpg";
	                			}
	                			
		                		fichero = new  File(directorio + secuencia + "-Banner." + extFile);
		                		actual.write(fichero);				                	
		                		fileName = secuencia + "-Banner." + extFile;		                				                	
	                		} else {
	                			fileName = "ERROR";
	                		}
	                		
	                	
	                } else { 
	                	logger.info("Se intento subir otro tipo de archivo");
	                	fileName = "TIPOAR";
	                }	                
                }
            }
            return fileName;
        }catch (SizeLimitExceededException le) {
        	logger.info("Se intento subir un archivo de tamano mayor al permitido " + parametrosDTO.getMaxMegas() + " MB");
        	return "MAXTAM";
        }catch(Exception e) {
            logger.error("******************************Error de Aplicacion " + e.getMessage(), e);
            return null;
        }
    }	
	
	/*private boolean obtenerPropiedades() {
		boolean success = false;		 
		try {	    		
			Properties propsTmp = new Properties();
		    propsTmp.load(this.getClass().getResourceAsStream( "/ApplicationResources.properties" ));
			String rutaProperties = propsTmp.getProperty("rutaProperties");			
			Properties props = new Properties();
			props.load(new FileInputStream(new File(rutaProperties)));		
			pathLocal = props.getProperty("pathLocal");
			pathRemotoNBA = props.getProperty("pathRemotoNBA");
			pathShell = props.getProperty("pathShell");
			pathShellElimina = props.getProperty("pathShellElimina");
			extFiles = props.getProperty("extFiles") == null? null: props.getProperty("extFiles").split(",");
			URL_WS_BASE = props.getProperty("URL_WS_BASE");
			try {
				parametrosDTO.getMaxMegas() = Integer.parseInt(props.getProperty("parametrosDTO.getMaxMegas()Programas"));
			} catch (Exception e) {
				parametrosDTO.getMaxMegas() = 1;
			}
			success = true;					
		} catch (Exception ex) {
			success = false;
			logger.error("No se encontro el Archivo de propiedades: ", ex);			
		}
		return success;
    }*/
	
			
	public boolean createFolders(String directorio) {
		boolean success = false;
		try {						
			File carpetas = new File(directorio) ;
			if(!carpetas.exists()) {   
				success = carpetas.mkdirs();					
			} else 
				success = true;						
		} catch (Exception e) {
			success = false;
			logger.error("Ocurrio error al crear las carpetas: ", e);
		} 
		return success;
	}


}
