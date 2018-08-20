<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-latest.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/uno_imagen_principal.js"></script>

<script language="JavaScript">
	function htmlDataSubmitPrincipal() {						
		var resultPrincipal = document.getElementById('namePrincipal').value;
		if(resultPrincipal == 'N/A' || resultPrincipal == 'Selecciona el archivo')
			resultPrincipal = '';
		document.getElementById("").value = resultPrincipal;
	}
</script>


<style>
	.liga{
		text-decoration:none;
		color:#00F;
		cursor:pointer;
	}
	.ligaPrincipal{
		border: 3px double #000000;
		font-family: verdana;
		   background: #FFFFFF;		
		font-weight: bold;
	}

	.table1 {
	   width: 100%;
	   border: 1px solid #000;
	}			
</style>

</head>
<body>
<table class="table1" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<table class="table1" cellpadding="0" cellspacing="0">
				<tr bgcolor="#000000">
					<td align="right">&nbsp;</td>
				</tr>
				<tr>
					<td>																			
						<table cellpadding="5" cellspacing="0" class="table1">			
							<tr bgcolor="#CCC">
								<td align="left">
									<strong>Carga de imagen Principal:</strong> 		
								</td>				
							</tr>			
							<tr>													
								<td>									
									<table cellpadding="0" cellspacing="0" width="100%">
										<tr>
											<td colspan="2">																				
												<table cellspacing="1" cellpadding="1" width="100%">
													<tr>				
														<td width="5%" align="center">
															<div id="estatusImagenPrincipal"><img src="<%=request.getContextPath()%>/img/seleccionar.png"></div>
															<input id="namePrincipal" name="namePrincipal" type="hidden" value=""/>																							
														</td>
														<td width="95%" align="left" id="cargaArchivoPrincipal">
															<input id="archivoPrincipal" name="archivoPrincipal" type="file"/>
															<input type="button" name="cargarPrincipal" id="cargarPrincipal" value="Cargar Imagen" onclick="cargaArchivoPrincipal();">
														</td>
														<td id="procesandoPrincipal" style="display:none" width="95%">
															Se esta cargando el archivo... espere un momento
														</td>														
													</tr>
													<tr>
														<td colspan="3" style="color: #666666; font-style: italic; font-weight: normal; white-space: normal;">Tama&ntilde;o Recomendado:640x450px Formato:JPG</td>
													</tr>
												</table>		
											</td>
										</tr>
									</table>				   																				
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>


</body>
</html>