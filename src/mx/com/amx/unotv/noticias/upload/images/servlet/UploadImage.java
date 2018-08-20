package mx.com.amx.unotv.noticias.upload.images.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.com.amx.unotv.noticias.upload.images.bo.ProcesoBO;
import mx.com.amx.unotv.noticias.upload.images.bo.UploadImageBO;
import mx.com.amx.unotv.noticias.upload.images.dto.ParametrosDTO;
import mx.com.amx.unotv.noticias.upload.images.util.CargaProperties;
import mx.com.amx.unotv.noticias.upload.images.util.Scalr;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;


public class UploadImage extends HttpServlet {
	
	private final int BASE_ORIGINAL=640;
	private final int ALTO_ORIGINAL=450;
	
	private static final long serialVersionUID = 1L;
       	
	private final Logger LOG = Logger.getLogger(UploadImage.class);
	
    public UploadImage() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.debug("Intento de peticion Get");
	}

	/**
	 * 
	 * */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		LOG.debug("Inicia doPost en UploadImage");		
		PrintWriter out = response.getWriter();	      
		
		try {			
	        UploadImageBO uploadImageBO = new UploadImageBO();	        
	        uploadImageBO.uploadImage(request);        	
        	out.print("");
        	return;        
		}catch(Exception ex){
			LOG.error("Ocurrio un error subiendo el archivo: " + ex.getMessage() , ex);
			 out.print( "ERROR" );
		}
	}
	
	

	
	@SuppressWarnings({ "unchecked" })
	public String procesaFicheros(HttpServletRequest req, ParametrosDTO parametrosDTO) {
        try {
        	//long maxBites = 1024*1024*parametrosDTO.getMaxMegas();        	        	
        	long maxBites = 1024*parametrosDTO.getMaxKBImagenes();
                        
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
	                	if(sizeFile<=maxBites) {
	                		String directorio = parametrosDTO.getPathLocalImagenes();
	                		String directorioClaroIdeas=parametrosDTO.getPathLocalImagenesClaroIdeas();
	                		boolean continuar = false;
	                		Integer secuencia = 0;
	                		
	                		try { 
	                			
								ProcesoBO procesoBo = new ProcesoBO();
								secuencia = procesoBo.getSecuencia("",parametrosDTO.getURL_WS_BASE());
								//directorio += secuencia  + "/";
								continuar = createFolders(directorio);
								createFolders(directorioClaroIdeas);
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
	                			
		                		/*fichero = new  File(directorio + secuencia + "-" + prefix + "." + extFile);
		                		actual.write(fichero);
		                		fileName = fichero.getName();*/
	                			InputStream in = new ByteArrayInputStream( actual.get() );
								BufferedImage bImageFromConvert = ImageIO.read(in);
								try {
									BufferedImage principal = Scalr.resize(bImageFromConvert, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, 640, 450, Scalr.OP_ANTIALIAS);								
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									ImageIO.write( principal, extFile, baos );
									baos.flush();
									byte[] imageInByte = baos.toByteArray();
									baos.close();
									FileOutputStream fos = new FileOutputStream(directorio + secuencia + "-Principal." + extFile);
									fos.write(imageInByte);
									fos.close();
								} catch (Exception e) {
									LOG.error("Error en imagen principal: ", e);
								}
								
								try {
									BufferedImage mediana = Scalr.resize(bImageFromConvert, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, 360, 253, Scalr.OP_ANTIALIAS);								
									ByteArrayOutputStream baosM = new ByteArrayOutputStream();
									ImageIO.write( mediana, extFile, baosM );
									baosM.flush();
									byte[] imageMInByte = baosM.toByteArray();
									baosM.close();
									FileOutputStream fosM = new FileOutputStream(directorio + secuencia + "-Mediana." + extFile);
									fosM.write(imageMInByte);
									fosM.close();
								} catch (Exception e) {
									LOG.error("Error al reajustar imagen mediana: ", e);
								}
								try {
									BufferedImage miniatura = Scalr.resize(bImageFromConvert, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, 100, 70, Scalr.OP_ANTIALIAS);								
									ByteArrayOutputStream baosT = new ByteArrayOutputStream();
									ImageIO.write( miniatura, extFile, baosT );
									baosT.flush();
									byte[] imageTInByte = baosT.toByteArray();
									baosT.close();
									FileOutputStream fosT = new FileOutputStream(directorio + secuencia + "-Miniatura." + extFile);
									fosT.write(imageTInByte);
									fosT.close();
								} catch (Exception e) {
									LOG.error("Error al reajustar imagen miniatura: ", e);
								}
								
								try {
									
									float porcentajeParaRedimensionar1=0.555f;
									float porcentajeParaRedimensionar2=0.3125f;
									
									BufferedImage principal = Scalr.resize(bImageFromConvert, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, 640, 450, Scalr.OP_ANTIALIAS);
									BufferedImage image1=generaImagenVertical(principal, BASE_ORIGINAL, ALTO_ORIGINAL, porcentajeParaRedimensionar1);
									BufferedImage image2=generaImagenHorizontal(principal, BASE_ORIGINAL, ALTO_ORIGINAL, porcentajeParaRedimensionar2);
									
									saveImage(image1, directorioClaroIdeas + secuencia + "-imagent1."+extFile);
									saveImage(image2, directorioClaroIdeas + secuencia + "-imagent2."+extFile);
									
								} catch (Exception e) {
									LOG.error("Error ");
								}
		                		
								fileName = secuencia + "-Principal." + extFile;
								
	                		} else {
	                			fileName = "ERROR";
	                		}
	                		
	                	} else {
	                		LOG.info("Se intento subir un archivo de tamano mayor al permitido " + parametrosDTO.getMaxMegas() + " MB");
		                	fileName = "MAXTAM";
	                	}
	                } else { 
	                	LOG.info("Se intento subir otro tipo de archivo");
	                	fileName = "TIPOAR";
	                }	                
                }
            }
            return fileName;
        }catch (SizeLimitExceededException le) {
        	LOG.info("Se intento subir un archivo de tamano mayor al permitido " + parametrosDTO.getMaxMegas() + " MB");
        	return "MAXTAM";
        }catch(Exception e) {
        	LOG.error("******************************Error de Aplicacion " + e.getMessage(), e);
            return null;
        }
    }	
	
	private void saveImage(BufferedImage image, String directorio) throws Exception{
		try {
			ByteArrayOutputStream baosT = new ByteArrayOutputStream();
			ImageIO.write( image, "jpg", baosT );
			baosT.flush();
			byte[] imageTInByte = baosT.toByteArray();
			baosT.close();
			FileOutputStream fosT = new FileOutputStream(directorio);
			fosT.write(imageTInByte);
			fosT.close();
			LOG.debug("--->Image "+directorio+" was saved ok.");
		} catch (Exception e) {
			LOG.error("Error saveImage: "+e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	private BufferedImage generaImagenHorizontal(BufferedImage bImageFromConvert, int base, int alto, float porcentajeRedimensionar) throws Exception{
		
		final int ALTO_IMAGEN_TRANSFORMADA=120;
		
		int baseRedimension=0;
		int altoRedimension=0;
		
		BufferedImage imagenRecortada=null;
		try {
			
			baseRedimension=(int) Math.ceil(base * porcentajeRedimensionar);
			altoRedimension=(int) Math.ceil(alto * porcentajeRedimensionar) - 1 ;
			
			int recorteVertical=(altoRedimension - ALTO_IMAGEN_TRANSFORMADA)/2;
			
			BufferedImage imageRedimensionada = Scalr.resize(bImageFromConvert, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, baseRedimension, altoRedimension, Scalr.OP_ANTIALIAS);
			
			imagenRecortada=Scalr.crop(imageRedimensionada, 0, recorteVertical, baseRedimension, altoRedimension-(recorteVertical*2), null);
			
		} catch (Exception e) {
			LOG.error("Exception generaImagenHorizontal: ",e);			
			throw new Exception(e);
		}
		return imagenRecortada;
	}

	private BufferedImage generaImagenVertical(BufferedImage bImageFromConvert, int base, int alto, float porcentajeRedimensionar) throws Exception{
		
		final int BASE_IMAGEN_TRANSFORMADA=200;
		
		int baseRedimension=0;
		int altoRedimension=0;
		
		BufferedImage imagenRecortada=null;
		try {
			
			baseRedimension=(int) Math.ceil(base * porcentajeRedimensionar);
			altoRedimension=(int) Math.ceil(alto * porcentajeRedimensionar);
			
			int recorteDeLado=(baseRedimension - BASE_IMAGEN_TRANSFORMADA)/2;
			
			BufferedImage imageRedimensionada = Scalr.resize(bImageFromConvert, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, baseRedimension, altoRedimension, Scalr.OP_ANTIALIAS);
			
			imagenRecortada=Scalr.crop(imageRedimensionada, recorteDeLado, 0, baseRedimension-(recorteDeLado*2), altoRedimension, null);
			
		} catch (Exception e) {
			LOG.error("Exception generaImagenVertical: ",e);			
			throw new Exception(e);
		}
		return imagenRecortada;
	}
	

			
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


}
