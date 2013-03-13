var pixpack_user;
var pixpack_folder;
var pixpack_width;
var pixpack_height;
var pixpack_method;
var pixpack_charset;
var pixpack_language;
var pixpack_callback;
var pixpack_logo;
var pixpack_image;

var _pixpack_win;
var _pixpack_suffix='?';
var _pixpack_key;
var _pixpack_filename;
var _pixpack_isCallback;
var _pixpack_isImage;
var _pixpack_isChecked;
var _pixpack_serverRoot='${root}';
//var _pixpack_serverRoot='http://pixpack.net';

/**
 * Checks the parameters which have to be defined for the upload-inlet.
 */
function pixpack_checkParameter() {
	if(typeof pixpack_callback=='undefined' || pixpack_callback==null) _pixpack_isCallback=false;
	else _pixpack_isCallback=true;

	if(typeof pixpack_image=='undefined' || pixpack_image==null) {
		pixpack_image='pixpack_image';
		_pixpack_isImage=false;
	} else _pixpack_isImage=true;
	
	if(typeof pixpack_logo=='undefined' || pixpack_logo==null) {
		pixpack_logo='32x32';
	}

	if(typeof pixpack_default=='undefined' || pixpack_default==null) pixpack_default=_pixpack_serverRoot+'/image/logo.gif';
	else pixpack_default=pixpack_default;
	
	if(typeof pixpack_error=='undefined' || pixpack_error==null) pixpack_error=_pixpack_serverRoot+'/image/error.gif';
	else pixpack_error=pixpack_error;
	
	
}

function pixpack_createSuffix() {
	_pixpack_suffix+='user='+pixpack_user+'&';
	_pixpack_suffix+='folder='+pixpack_folder+'&';
	_pixpack_suffix+='width='+pixpack_width+'&';
	_pixpack_suffix+='height='+pixpack_height+'&';
	_pixpack_suffix+='method='+pixpack_method+'&';
	_pixpack_suffix+='charset='+pixpack_charset+'&';
	_pixpack_suffix+='language='+pixpack_language+'&';
	_pixpack_suffix+='callback='+pixpack_callback+'&';
	_pixpack_suffix+='key=';
}	

function pixpack_writeForm() {
//!!!!!!!!!!!!!!
	if(navigator.userAgent.toLowerCase().indexOf('gecko')==-1) {
		var lang;
		if(typeof pixpack_language=='undefined') lang='en';
		else lang=pixpack_language;
		
		document.writeln('<div id="pixpack_compatible" style="display: none; position: absolute; top: 0px; left: 0px; z-index: 2; width: 468px; background-color: #E8EEFC; border: 1px solid #9EC79C; padding: 2px;">');
		document.writeln('<p>'+pixpack_compatibleMessage(lang)+'</p>');
		document.writeln('<form><input type="button" value="'+pixpack_compatibleButton(lang)+'" onclick="pixpack_compatibel()"/></form>');
		document.writeln('<div style="padding-top: 10px; border-top: 1px solid #9EC79C;"><a href="http://pixpack.net/browser.html" title="Optimal PixPack-Browser" target="browser">'+pixpack_compatibleBrowser(lang)+'</a></div>');
		document.writeln('</div>');
	}
	document.writeln('<form method="post" action="" onsubmit="return pixpack_dummy()">');
	document.writeln('<input type="image" src="'+_pixpack_serverRoot+'/image/pixpack_'+pixpack_logo'+.gif" onclick="pixpack_openWindow()">');
	document.writeln('</form>');
	if(_pixpack_isImage) document.writeln('<img alt="PixPack" title="PixPack" src="'+pixpack_default+'" id="'+pixpack_image+'" onerror="pixpack_checkExtension(this.src)" onload="pixpack_call(this.src)"/>');
	else document.writeln('<img alt="PixPack" title="PixPack" src="'+pixpack_default+'" id="'+pixpack_image+'" onerror="pixpack_checkExtension(this.src)" onload="pixpack_call(this.src)" width="1px" height="1px" style="display: none;"/>');
}

function pixpack_writeError() {
	document.writeln('<div style="color: red; font-weight: bold;">Unable to load the PixPack-Inlet.</div>');
}

function pixpack_openWindow() {
	_pixpack_isChecked=false;
	
	if(document.getElementById('pixpack_compatible')) {
		document.getElementById('pixpack_compatible').style.display='block';
	}
	
	_pixpack_win=window.open(_pixpack_serverRoot+'/inlet.jsp'+_pixpack_suffix+'${key}','pixpack','width=400, height=300');
	_pixpack_win.moveTo(screen.width-400,screen.height-300);
	_pixpack_win.focus();

	//no dialog
	if(!document.getElementById('pixpack_compatible')) pixpack_checkWindow();
	
	return false;
}

function pixpack_compatibel() {
	document.getElementById('pixpack_compatible').style.display='none';
	document.getElementById(pixpack_image).src=_pixpack_serverRoot+'/'+pixpack_user+':'+_pixpack_key+'.jpg';
}

function pixpack_compatibleMessage(lang) {
	if(lang=='de') return 'Ihr Browser ist nicht "up-to-date"!<br/>Aus diesem Grund ist es notwendig den Upload manuell zu best&auml;tigen.';
	else return 'Your browser is not up-to-date!<br/>So you have to confirm the upload manually.';
}

function pixpack_compatibleButton(lang) {
	if(lang=='de') return 'Upload beendet';
	else return 'Upload finished';
}

function pixpack_compatibleBrowser(lang) {
	if(lang=='de') return 'Hier bekommen sie einen aktuellen Browser!';
	else return 'Here you get a modern Browser!';
	return result;
}

function pixpack_dummy() {
	return false;
}

function pixpack_checkWindow() {
	if(_pixpack_win.closed) {
		document.getElementById(pixpack_image).src=_pixpack_serverRoot+'/'+pixpack_user+':'+_pixpack_key+'.jpg';
		 
		//pixpack_call(_pixpack_serverRoot+'/'+pixpack_user+':'+_pixpack_key+'.jpg');
		return;
	}
	setTimeout('pixpack_checkWindow()',100);
}

function pixpack_call(img) {
	if(
		_pixpack_isCallback && 
		img.indexOf(pixpack_default)==-1 &&
		img.indexOf(pixpack_error)==-1
	)
	eval(pixpack_callback+'("'+img+'")');
}
/*
function pixpack_getUniqueKey() {
	var key="";
	var now=new Date();
	
	if(now.getYear()<999) key+=now.getYear()+1900;
	else key+=now.getYear();

	if(now.getMonth()<10) key+='0'+(now.getMonth()+1);
	else key+=(now.getMonth()+1);

	if(now.getDate()<10) key+='0'+now.getDate();
	else key+=now.getDate();

	if(now.getHours()<10) key+='0'+now.getHours();
	else key+=now.getHours();

	if(now.getMinutes()<10) key+='0'+now.getMinutes();
	else key+=now.getMinutes();

	if(now.getSeconds()<10) key+='0'+now.getSeconds();
	else key+=now.getSeconds();

	if(now.getMilliseconds()<10) key+='0'+now.getMilliseconds();
	else key+=now.getMilliseconds();

	key+='_';
	
	for(i=0; i<10; i++) {
		key+=pixpack_randomLetter();
	}

	_pixpack_key=key;
	return _pixpack_key;
}


function pixpack_randomLetter() {
	return String.fromCharCode(97 + Math.round(Math.random() * 25));
}
*/

function pixpack_checkExtension(file) {
	if(_pixpack_isChecked) return;
	
	if(file.lastIndexOf('.')!=-1) {
		var extension;
		if(file.indexOf('.jpg')!=-1) extension='.gif';
		else if(file.indexOf('.gif')!=-1) extension='.png';
		else if(file.indexOf('.png')!=-1) extension='.jpeg';
		else {
			//if(_pixpack_isCallback) pixpack_call(null);
			//only if it is shown
			if(_pixpack_isImage) document.getElementById(pixpack_image).src=pixpack_error;
			_pixpack_isChecked=true;
			return;
		}
	} else document.getElementById(pixpack_image).src=file+'.jpg';
	
	file=file.substring(0,file.length-4);
	//if(_pixpack_isCallback) pixpack_call(file+extension);
	document.getElementById(pixpack_image).src=file+extension;
}

pixpack_checkParameter();
pixpack_createSuffix();
pixpack_writeForm();
