package mx.com.amx.unotv.noticias.upload.images.bo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.amx.unotv.noticias.upload.images.bo.exception.UploadImageBOException;
import mx.com.amx.unotv.noticias.upload.images.dto.ParametrosDTO;
import mx.com.amx.unotv.noticias.upload.images.dto.ProcesaImagenDTO;
import mx.com.amx.unotv.noticias.upload.images.util.CargaProperties;
import mx.com.amx.unotv.noticias.upload.images.util.Scalr;

public class UploadImageBO {

	//LOG
	private static Logger LOG = Logger.getLogger(UploadImageBO.class);
	@Autowired
	private CallWSBO callWSBO;
	
	
	/**
	 * Metodo que carga y trata las imagenes de UnoTV
	 * 
	 * */
	public String uploadImage(HttpServletRequest request)
	{
		LOG.debug("Inicia uploadImage en UploadImageBO");
		try {
			
			//Cargamos archivos de propiedades						
			CargaProperties cargaProperties=new CargaProperties();
	        ParametrosDTO parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
	        	 	        
        	int maxBites = 1024*parametrosDTO.getMaxKBImagenes();
        	LOG.debug("maxBites: "+maxBites);
        	//Cargamos la imagen que viene en el request
	        DiskFileUpload fu = new DiskFileUpload();
            fu.setSizeMax(maxBites); //80 KB
            fu.setSizeThreshold(maxBites);
            fu.setRepositoryPath("/tmp");
            List<FileItem> fileItems = fu.parseRequest(request);
            
//            if(fileItems == null) {
//                return null;
//            }
            
            Iterator<FileItem> i = fileItems.iterator();
            FileItem fileItemActual = null;
            String fileItemName = "";            
            String nameImgPrincipal = "";
            
            while (i.hasNext()) {
            	
            	 fileItemActual = i.next();                
            	 fileItemName = fileItemActual.getName();
                 if( fileItemName != null && !fileItemName.equals("") && fileItemActual.getFieldName().equals("nameFile") ) {
                	
                	LOG.debug("<<<<<<<< fileItemName: "+fileItemName);
                	File fichero = new File(fileItemName);
                	 
                 	LOG.info("Imagen seleccionada:" + fichero.getName());
	                long sizeFile = fileItemActual.getSize();


	                //Validamos la extesion del fichero	                	              
	                String [] arrayNombreFile = fichero.getName().split("\\.");
        			String extensionFile = arrayNombreFile[arrayNombreFile.length-1];		                				

	                
	                boolean isValidExtension=false;
	                
	                //Recorremos las extensiones validas
	                for (String strExtensionesValidas : parametrosDTO.getExtFiles()) {
	                	if(strExtensionesValidas.equals(extensionFile))
	                		isValidExtension=true;                	
					}
	                
	                
	                //Tiene una extension valida
	                if(isValidExtension)
	                {
	                	//No supera el tamaño permitido
	                	if(sizeFile<=maxBites) 
	                	{

	                		Integer secuencia = 0;
	                		
	                		//Obtenemos la secuencia	                		
							secuencia = callWSBO.getSecuencia( parametrosDTO);
							LOG.debug("secuencia: "+secuencia);
	                		                			
							//Imagen principal							
							InputStream insInputStreamImg = new ByteArrayInputStream( fileItemActual.get() );							
							BufferedImage bImageFromConvert = ImageIO.read(insInputStreamImg);
							
							
							LOG.debug(">>>>>>>> Original  Height: " +bImageFromConvert.getHeight());
							LOG.debug(">>>>>>>> Original  Width: " +bImageFromConvert.getWidth());
							
							
							//Imagen principal
							//Configuracion del archivo de propiedades
							String[] arrayConfImgPrincipal = parametrosDTO.getConfiguracionImgPrincipal().split("\\|");
											
							//Nombre de la imagen principal
							nameImgPrincipal = secuencia+arrayConfImgPrincipal[2]+extensionFile;
							
							
							//Llenamos DTO
							ProcesaImagenDTO procesaImagenDTO =  new ProcesaImagenDTO();
							procesaImagenDTO.setDirectorio(parametrosDTO.getPathLocalImagenes());
							procesaImagenDTO.setExtensionFile(extensionFile);
							procesaImagenDTO.setNombre(arrayConfImgPrincipal[2]);
							procesaImagenDTO.setSecuencia(secuencia);
							procesaImagenDTO.setTargetWidthm(Integer.parseInt(arrayConfImgPrincipal[0]));
							procesaImagenDTO.setTargetHeight(Integer.parseInt(arrayConfImgPrincipal[1]));							
							procesaImagen(bImageFromConvert, procesaImagenDTO);
							//procesaImagenOriginal(bImageFromConvert, procesaImagenDTO);
							
							LOG.info("Genera imagen principal: OK");							

					
	                	}
	                	else								                		
	                	{	                	
	                		LOG.info("Se intento subir un archivo de mayor tamaño " + sizeFile +" MB");
	                		nameImgPrincipal = "MAXTAM";
	                	}
                	}
	                else
	                {
	                	LOG.info("Extencion incorrecta");
	                	nameImgPrincipal = "TIPOAR";
	                }	             	                
                 }//FIN IF fileName                  
            }//FIN WHILE
            LOG.info("Nombre imagen generada: "+nameImgPrincipal);
            return nameImgPrincipal;            
		}catch (SizeLimitExceededException le) {
        	LOG.error("SizeLimitExceededException",le);
        	return "MAXTAM";
		} catch (Exception e) {
			LOG.error("Exception en uploadImage ",e);
            return null;
		}		
	}

	
	/**
	 *Metodo que hace el resize y renombra la imagen. 
	 *@param BufferedImage
	 *@param ProcesaImagenDTO
	 *@return 
	 *@throws
	 *
	 * */
	private void procesaImagen(BufferedImage bufferedImage, ProcesaImagenDTO procesaImagenDTO) throws UploadImageBOException
	{
		LOG.debug("Inicia procesaImagen en UploadImageBO");
		try {								
			BufferedImage img = Scalr.resize(bufferedImage, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, procesaImagenDTO.getTargetWidthm(), procesaImagenDTO.getTargetHeight(), Scalr.OP_ANTIALIAS);								
			ByteArrayOutputStream baosM = new ByteArrayOutputStream();
			ImageIO.write( img, procesaImagenDTO.getExtensionFile(), baosM );
			baosM.flush();
			byte[] imageMInByte = baosM.toByteArray();
			baosM.close();
			FileOutputStream fosM = new FileOutputStream(procesaImagenDTO.getDirectorio() + procesaImagenDTO.getSecuencia() + procesaImagenDTO.getNombre() + procesaImagenDTO.getExtensionFile());
			fosM.write(imageMInByte);
			fosM.close();			
		} catch (Exception e) {
			LOG.error("Exception en procesaImagen ",e);
			throw new UploadImageBOException(e.getMessage());
		} 	 
	}
		
	/**
	 *Metodo que hace el resize y renombra la imagen. 
	 *@param BufferedImage
	 *@param ProcesaImagenDTO
	 *@return 
	 *@throws
	 *
	 * */
	public void procesaImagenOriginal(BufferedImage bufferedImage, ProcesaImagenDTO procesaImagenDTO) throws UploadImageBOException
	{
		LOG.debug("Inicia procesaImagen en UploadImageBO");
		try {								
			
			
            
			 //Scalr.crop(src, x, y, width, height, ops)
			LOG.debug(" --- procesaImagenDTO  : "+procesaImagenDTO.toString()+"---  ");
			BufferedImage img = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, procesaImagenDTO.getTargetWidthm(), procesaImagenDTO.getTargetHeight(), Scalr.OP_ANTIALIAS);								
			
			File file = new File(procesaImagenDTO.getDirectorio() + procesaImagenDTO.getSecuencia() + procesaImagenDTO.getNombre() + procesaImagenDTO.getExtensionFile());            
            ImageIO.write(img, procesaImagenDTO.getExtensionFile(), file);
			
			 
			 
			//ImageIO.write( img, procesaImagenDTO.getExtensionFile(), baosM );
			//ByteArrayOutputStream baosM = new ByteArrayOutputStream();
			//ImageIO.write( bufferedImage, procesaImagenDTO.getExtensionFile(), baosM );
			//baosM.flush();
			//byte[] imageMInByte = baosM.toByteArray();
			//baosM.close();
			//FileOutputStream fosM = new FileOutputStream(procesaImagenDTO.getDirectorio() + procesaImagenDTO.getSecuencia() + procesaImagenDTO.getNombre() + procesaImagenDTO.getExtensionFile());
			//fosM.write(imageMInByte);
			//fosM.close();			
			
			
			
		} catch (IOException e) {
			LOG.error("Exception en uploadImage ",e);
			throw new UploadImageBOException(e);
		}catch (Exception e) {
			LOG.error("Exception en procesaImagen ",e);
			throw new UploadImageBOException(e.getMessage());
		} 	 
	}
	

	
	protected void procesaImagenOriginal(HttpServletRequest request, String fileName, String path) throws ServletException, IOException {
	    

	    // Create path components to save the file	    
	    final Part filePart = request.getPart("file");
	    //final Part filePart = request.getPart("archivoPrincipal");
	    
	    
	    //final String fileName = getFileName(filePart);

	    OutputStream out = null;
	    InputStream filecontent = null;

	    //final PrintWriter writer = response.getWriter();

	    try {
	        out = new FileOutputStream(new File(path + File.separator+ fileName));
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        //writer.println("New file " + fileName + " created at " + path);
	        
	    } catch (FileNotFoundException fne) {
	        
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }	      
	    }
	}

	
		
}// FIN CLASE
