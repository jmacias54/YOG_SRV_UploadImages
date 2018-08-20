package mx.com.amx.unotv.noticias.upload.images.bo;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mx.com.amx.unotv.noticias.upload.images.dto.ParametrosDTO;
import mx.com.amx.unotv.noticias.upload.images.util.CargaProperties;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContextBO.xml"})
public class CallWSBOTest {

	//LOG
	private static Logger LOG = Logger.getLogger(CallWSBOTest.class);
	@Autowired
	private CallWSBO callWSBO;
	
	/*
	 * 
	 * */
	@Test
	public void testGetSecuencia() 
	{
		LOG.debug("Inicia testGetSecuencia");		
		try {			
			CargaProperties cargaProperties=new CargaProperties();
	        ParametrosDTO parametrosDTO=cargaProperties.obtenerPropiedades("ambiente.resources.properties");			
			Integer intSec = callWSBO.getSecuencia( parametrosDTO);
			Assert.assertNotNull(intSec);
			LOG.debug("<<<<<<intSec: "+intSec);			
		} catch (Exception e) {
			LOG.error("Exception en testGetSecuencia",e);			
			Assert.assertTrue(false);
		}		
	}
	
	
}//FINC LASE
