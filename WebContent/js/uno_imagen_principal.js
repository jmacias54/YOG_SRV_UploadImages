var pathLigaPrincipal = '/portal/unotv/imagenes/';

function cargaArchivoPrincipal(){		
	var nameArchivo = document.getElementById('archivoPrincipal').value;
	document.getElementById('estatusImagenPrincipal').innerHTML = '<img src="/wps/STDynamicTemplate/img/seleccionar.png">';
	if(nameArchivo == '') {
		alert('Debes seleccionar una imagen Principal.');		
	} else {
		document.getElementById('cargaArchivoPrincipal').style.display = 'none';
		document.getElementById('procesandoPrincipal').style.display = 'block';
		document.getElementById('namePrincipal').value = 'N/A';
		uploadPrincipalAMX();				
	}
}


function cargarOtraImagenPrincipal() {
	document.getElementById('namePrincipal').value = "N/A";
	document.getElementById('cargaArchivoPrincipal').style.display = 'block';
	document.getElementById('estatusImagenPrincipal').innerHTML = '<img src="/wps/STDynamicTemplate/img/seleccionar.png">';
	document.getElementById('procesandoPrincipal').innerHTML = 'Se esta cargando el archivo... espere un momento'; 
	document.getElementById('procesandoPrincipal').style.display = 'none';   
}

function uploadPrincipalAMX(){	
	try {		
		var inputFileImage = document.getElementById('archivoPrincipal');
		var file = inputFileImage.files[0];
		var data = new FormData();	
		data.append('nameFile',file);		
		$.ajax({
		    url: '/YOG_SRV_UploadImages/servlet/uploadImagenPrincipal',
		    data: data,
		    cache: false,
		    contentType: false,
		    processData:false,
		    type: 'POST',
		    success: function(data){
		    	console.log(data);		    	
			if(data == 'ERROR') {
				document.getElementById('namePrincipal').value = 'N/A';
				document.getElementById('cargaArchivoPrincipal').style.display = 'none';
				document.getElementById('procesandoPrincipal').style.display = 'block';
				document.getElementById('procesandoPrincipal').innerHTML='Ocurrio un error al cargar el archivo. <a href="javascript:cargarOtraImagenPrincipal()">Intentar de nuevo.</a>';
			} else if (data == 'MAXTAM') {
				document.getElementById('namePrincipal').value = 'N/A';
				document.getElementById('cargaArchivoPrincipal').style.display = 'none';
				document.getElementById('procesandoPrincipal').style.display = 'block';
				document.getElementById('procesandoPrincipal').innerHTML='Se intento subir un archivo de mayor tama&ntilde;o al maximo permitido. <a href="javascript:cargarOtraImagenPrincipal()">Intentar de nuevo.</a>';
			}else if (data == 'TIPOAR') {
				document.getElementById('namePrincipal').value = 'N/A';
				document.getElementById('cargaArchivoPrincipal').style.display = 'none';
				document.getElementById('procesandoPrincipal').style.display = 'block';
				document.getElementById('procesandoPrincipal').innerHTML='Se intento subir un archivo de diferente extenci&oacute;n a la permitidas. <a href="javascript:cargarOtraImagenPrincipal()">Intentar de nuevo.</a>';
			} else {
				sleepProcesoPrincipal(5000);
				document.getElementById('namePrincipal').value = pathLigaPrincipal + data;
				document.getElementById('cargaArchivoPrincipal').style.display = 'block';
				document.getElementById('estatusImagenPrincipal').innerHTML = '<img src="/wps/STDynamicTemplate/img/seleccionado.png">';
				document.getElementById('procesandoPrincipal').style.display = 'none';   
			}
			
		    },
		    error: function(error) {	
			document.getElementById('namePrincipal').value = 'N/A';
			document.getElementById('cargaArchivoPrincipal').style.display = 'none';
			document.getElementById('procesandoPrincipal').style.display = 'block';
			document.getElementById('procesandoPrincipal').innerHTML='Ocurrio un error al cargar el archivo. <a href="javascript:cargarOtraImagenPrincipal()">Intentar de nuevo.</a>';
		    }
		});
	} catch(err) {
		document.getElementById('namePrincipal').value = 'N/A';
		document.getElementById('cargaArchivoPrincipal').style.display = 'none';
		document.getElementById('procesandoPrincipal').style.display = 'block';
		document.getElementById('procesandoPrincipal').innerHTML='Ocurrio un error al cargar el archivo. <a href="javascript:cargarOtraImagenPrincipal()">Intentar de nuevo.</a>';
	}	
}

function sleepProcesoPrincipal(milliSeconds) {
	var startTime = new Date().getTime();
       while (new Date().getTime() < startTime + milliSeconds);
}