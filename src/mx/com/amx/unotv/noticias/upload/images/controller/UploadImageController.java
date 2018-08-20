package mx.com.amx.unotv.noticias.upload.images.controller;

import javax.servlet.http.HttpServletRequest;

import mx.com.amx.unotv.noticias.upload.images.bo.UploadBannerBO;
import mx.com.amx.unotv.noticias.upload.images.bo.UploadGaleriaBO;
import mx.com.amx.unotv.noticias.upload.images.bo.UploadImageBO;
import mx.com.amx.unotv.noticias.upload.images.bo.UploadInfografiaBO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/")
public class UploadImageController {
	
	//LOG	
	private static Logger LOG = Logger.getLogger(UploadImageController.class);
	@Autowired
	private UploadImageBO uploadImageBO; 
	@Autowired
	private UploadInfografiaBO uploadInfografiaBO;
	@Autowired
	private UploadGaleriaBO uploadGaleriaBO;
	@Autowired
	private UploadBannerBO uploadBannerBO;
	
	
	@RequestMapping(value = "/uploadImagenPrincipal", method = RequestMethod.POST)
	@ResponseBody
    public String uploadImage(HttpServletRequest request) 
	{       
		LOG.info("** Inicia uploadImage **");		
		return uploadImageBO.uploadImage(request);        
    }
	
	@RequestMapping(value = "/uploadInfografia", method = RequestMethod.POST)
	@ResponseBody
    public String uploadInfografia(HttpServletRequest request) 
	{       
		LOG.info("** Inicia uploadInfografia **");		
		return uploadInfografiaBO.uploadInfografia(request);        
				
    }
	
	@RequestMapping(value = "/uploadGaleria", method = RequestMethod.POST)
	@ResponseBody
    public String uploadGaleria(HttpServletRequest request) 
	{       
		LOG.info("** Inicia uploadGaleria **");		
		return uploadGaleriaBO.procesaFicheros(request);     
				
    }
	
	@RequestMapping(value = "/uploadBanner", method = RequestMethod.POST)
	@ResponseBody
    public String uploadBanner(HttpServletRequest request) 
	{       
		LOG.info("** Inicia uploadBanner **");		
		return uploadBannerBO.procesaFicheros(request);     
				
    }
	
	
}//FIN Controller
