/**
 * @author Jesus Armando Macias Benitez
 */
package mx.com.amx.unotv.noticias.upload.images.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * @author  Jesus Armando Macias Benitez
 *
 */
public class imageTest {

	/**
	 * @param args
	 * @author Jesus Armando Macias Benitez
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			
			System.out.println(" --- imageTest --- ");
			
			File img_tmp_ini = new File("C:\\tmp\\imagen-01.jpg");
	        //BufferedImage bImageTmp = ImageIO.read(img_tmp_ini); 
	        
	        FileInputStream imageInFile = new FileInputStream(img_tmp_ini);
            byte imageData[] = new byte[(int) img_tmp_ini.length()];
            System.out.println(" --- base64encJPG --- ");
			FileOutputStream imageOutFile = new FileOutputStream("C:\\tmp\\img_tmp_fin.jpg");

			imageOutFile.write(imageData);

			imageInFile.close();
			imageOutFile.close();

			
		}catch (Exception e) {
			System.err.println("-- Error imageTest : "+e+"-- " );
		}

	}

}
