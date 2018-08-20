package mx.com.amx.unotv.noticias.upload.images.bo;

import java.io.IOException;
import java.io.OutputStream;

import mx.com.amx.unotv.noticias.upload.images.dto.ParametrosDTO;
import mx.com.amx.unotv.noticias.upload.images.util.CargaProperties;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContextBO.xml"})
public class UploadImageBOTest {

	private static Logger LOG = Logger.getLogger(UploadImageBO.class);
	
	@Autowired
	private UploadImageBO uploadImageBO;
	
    private static final String textContentType = "text/plain";
    private static final int threshold = 16;

	
	@Test
	public void test_trataImagen()
	{
		LOG.debug("===== Inicia test_uploadImage ======");
		try {
						
			CargaProperties cargaProperties=new CargaProperties();
	        ParametrosDTO parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");
			
	        
	        
	        	        
	        //uploadImageBO.trataImagen(item, parametrosDTO);			
			
		} catch (Exception e) {
			LOG.error("Exception en test_uploadImage");
		}
		
	}
	
	
	/**
     * Create content bytes of a specified size.
     */
    private byte[] createContentBytes(int size) {
        StringBuffer buffer = new StringBuffer(size);
        byte count = 0;
        for (int i = 0; i < size; i++) {
            buffer.append(count+"");
            count++;
            if (count > 9) {
                count = 0;
            }
        }
        return buffer.toString().getBytes();
    }
	
    
    /**
     * Create a FileItem with the specfied content bytes.
     */
    private FileItem createFileItem(byte[] contentBytes) {
        
    	FileItemFactory factory = new DiskFileItemFactory(threshold, null);
        String textFieldName = "nameFile";

        FileItem item = factory.createItem(textFieldName,"image/jpg",true,"Temporal.jpg");
        
        try
        {
            OutputStream os = item.getOutputStream();
            os.write(contentBytes);
            os.close();
        }
        catch(IOException e)
        {
            LOG.error("Unexpected IOException" + e);
        }

        return item;

    }
	
}//FIN CLASE
