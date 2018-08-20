package mx.com.amx.unotv.noticias.upload.images.bo;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.amx.unotv.noticias.upload.images.dto.ParametrosDTO;
import mx.com.amx.unotv.noticias.upload.images.util.CargaProperties;

public class UploadBannerBO {

	//LOG
	private static Logger LOG = Logger.getLogger(UploadBannerBO.class);
	@Autowired
	private CallWSBO callWSBO;
	
	
	/**
	 * 
	 * 
	 * */
	public String procesaFicheros(HttpServletRequest req) 
	{
        try {
        	LOG.debug("Inicia procesaFicheros");
        	//Cargamos archivos de propiedades			
        	CargaProperties cargaProperties=new CargaProperties();
	        ParametrosDTO parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
        	
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
                actual = i.next();                
                fileName = actual.getName();
                int uu = 0;               
                if( fileName != null && !fileName.equals("") && actual.getFieldName().equals("nameFile") ) {                	
                	File fichero = new File(fileName);
	                LOG.info("El nombre del fichero es :" + fichero.getName());
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
								secuencia = callWSBO.getSecuencia( parametrosDTO);
								//directorio += secuencia  + "/";
								continuar = createFolders(directorio);
	                		} catch (Exception e) {
	                			continuar = false;
	                			secuencia = 0;
	                			LOG.error("Error al crear las carpetas: " + e.getMessage());
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
	                	LOG.info("Se intento subir otro tipo de archivo");
	                	fileName = "TIPOAR";
	                }	                
                }
            }
            return fileName;
        }catch (SizeLimitExceededException le) {
        	LOG.info("Se intento subir un archivo de tamano mayor al permitido ");
        	return "MAXTAM";
        }catch(Exception e) {
        	LOG.error("******************************Error de Aplicacion " + e.getMessage(), e);
            return null;
        }
    }	
	

	/***/
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
			LOG.error("Ocurrio error al crear las carpetas: ", e);
		} 
		return success;
	}
	
}//
