/**
 * @author Jesus Armando Macias Benitez
 */
package mx.com.amx.unotv.noticias.upload.images.util;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * @author  Jesus Armando Macias Benitez
 *
 */
public class Utils {
	private static Logger log = Logger.getLogger(Utils.class);
	
	
	public  boolean createFolders(String carpetaContenido) {
			log.debug(" --- createFolders [ Utils ]--- ");
			log.debug(" --- carpetaContenido : "+carpetaContenido+"--- ");
			boolean success = false;
			try {
				File carpetas = new File(carpetaContenido);
				if (!carpetas.exists()) {
					success = carpetas.mkdirs();
				} else
					success = true;
			} catch (Exception e) {
				success = false;
				log.error("Ocurrio error al crear las carpetas: ", e);
				log.debug("Ocurrio error al crear las carpetas: ", e);
			}
			return success;
		}

}
