var gUploadType='file';
var gIsOpenMenu=false;

function setLanguage(lang) {
	var input=document.getElementById('language_form').getElementsByTagName('input');
	for(i=0; i<input.length; i++) {
		if(input[i].value==lang) input[i].checked=true;
	}
}

function waitForSubmit(obj, text) {
	if(!obj) return;
	
	document.body.style.cursor='wait';
	
	var input=obj.getElementsByTagName('input');
	for(i=0; i<input.length; i++) {
		if(input[i].name=='submit') {
			input[i].style.display='none';
			document.getElementById('upload.process').style.display='block';
			//input[i].parentNode.appendChild(document.createTextNode(text));
			break;
		}
	}
}

function dummy() {
	return false;
}

/**
 * Sets the focus to the first input-element.
 */
function setFocus(id) {
	var form;
	if(!id) {
		if(document.forms.length>0) id=document.forms[0];
		else return;
	} else {
		if(!document.getElementById(id)) id=document.getElementById(id);
		else return;
	}

	if(id.getElementsByTagName('input').length>0) id.getElementsByTagName('input')[0].focus();
}

function upload_type(pType) {
	if(pType==gUploadType) return;
	if(pType=='file') {
		document.getElementById('upload_file').disabled=false;
		document.getElementById('upload_url').disabled=true;
		document.getElementById('upload_file').focus();
		document.getElementById('upload_file').parentNode.getElementsByTagName('input')[0].checked=true;
	} else {
		document.getElementById('upload_file').disabled=true;
		document.getElementById('upload_url').disabled=false;
		document.getElementById('upload_url').focus();
		document.getElementById('upload_url').parentNode.getElementsByTagName('input')[0].checked=true;
	}
	gUploadType=pType;
}

function upload_show(id) {
	var detail=document.getElementById('upload_'+id);
	if(!detail) return;
	var display=detail.style.display;
	
	//hide all dialogs
	if(document.getElementById('upload_text')) document.getElementById('upload_text').style.display='none';
	if(document.getElementById('upload_advertisement')) document.getElementById('upload_advertisement').style.display='none';
	if(document.getElementById('upload_image')) document.getElementById('upload_image').style.display='none';
	if(document.getElementById('upload_thumbnail')) document.getElementById('upload_thumbnail').style.display='none';
	if(document.getElementById('upload_detail')) document.getElementById('upload_detail').style.display='none';
	if(document.getElementById('upload_help')) document.getElementById('upload_help').style.display='none';
	
	if(display=='none') {
		detail.style.display='block';
		upload_extractFilename();
	} else if(document.getElementById('upload_advertisement')) document.getElementById('upload_advertisement').style.display='block';
}

function upload_extractFilename() {
	var id=document.getElementById('upload_filename');
	if(id.value!='') return;
	
	var filename;
	if(!document.getElementById('upload_file').disabled) filename=document.getElementById('upload_file').value;
	else filename=document.getElementById('upload_url').value;
	
	pos=filename.lastIndexOf('.');
	if(pos==-1) return;	//invalid filename
	
	//ZIP-->no filename
	if(filename.lastIndexOf('zip')==pos+1 || filename.lastIndexOf('ZIP')==pos+1 || filename.lastIndexOf('Zip')==pos+1) return "";
	
	filename=filename.substring(0,pos);

	pos=filename.lastIndexOf('/');
	if(pos==-1) pos=filename.lastIndexOf('\\');
	if(pos==-1) return;	//no path
	filename=filename.substring(pos+1);
	
	
	id.value=filename;
	id.focus();
	id.select();
}

function hotlink_highlight(field) {
	field.focus();
	field.select();
}

function hotlink_option(option) {
	var srvname=document.getElementById('hotlink.srvname').value;
	var srvid=document.getElementById('hotlink.srvid').value;
	var timestamp=document.getElementById('hotlink.timestamp').value;
	var key=document.getElementById('hotlink.key').value;
	var extension=document.getElementById('hotlink.extension').value;
	var user=document.getElementById('hotlink.user').value;
	
	var copyright=document.getElementById('hotlink.copyright');
	var thumbnail=document.getElementById('hotlink.thumbnail');
	var statistic=document.getElementById('hotlink.statistic');

	if(option!=null && option) {
		if(option=='copyright') {
			if(copyright.value=='on') {
				copyright.value='off';
				document.getElementById('hotlink.copyright.button').innerHTML=document.getElementById('hotlink.copyright.on').value;
			} else {
				copyright.value='on';
				document.getElementById('hotlink.copyright.button').innerHTML=document.getElementById('hotlink.copyright.off').value;
			}
		} else if(option=='thumbnail') {
			if(thumbnail.value=='on') {
				thumbnail.value='off';
				document.getElementById('hotlink.thumbnail.button').innerHTML=document.getElementById('hotlink.thumbnail.on').value;
				if(user.length>0) {
					if(statistic.value=='on') document.getElementById('hotlink.copyright.button').disabled=false;
					document.getElementById('hotlink.statistic.button').disabled=false;
				}
			} else {
				thumbnail.value='on';
				document.getElementById('hotlink.thumbnail.button').innerHTML=document.getElementById('hotlink.thumbnail.off').value;
				if(user.length>0) {
					document.getElementById('hotlink.copyright.button').disabled=true;
					document.getElementById('hotlink.statistic.button').disabled=true;
				}
			}
		} else if(option=='statistic') {
			if(statistic.value=='on') {
				statistic.value='off';
				document.getElementById('hotlink.statistic.button').innerHTML=document.getElementById('hotlink.statistic.on').value;
				copyright.value='off';
				document.getElementById('hotlink.copyright.button').disabled=true;
			} else {
				statistic.value='on';
				document.getElementById('hotlink.statistic.button').innerHTML=document.getElementById('hotlink.statistic.off').value;
				document.getElementById('hotlink.copyright.button').disabled=false;
			}
		}
	}

	var isCopyright=copyright.value=='on' && user.length>0;
	var isThumbnail=thumbnail.value=='on';
	var isStatistic=statistic.value=='on' && user.length>0;

	var image='http://';
	if(isThumbnail) {
		image+=srvid+'.'+srvname+'/_'+timestamp+'_'+key+'.'+extension;
		document.getElementById('hotlink.message').innerHTML=document.getElementById('hotlink.message.superfast').value;
	} else if(isCopyright) {
		image+=srvname+'/'+user+':'+timestamp+'_'+key+'.'+extension;
		document.getElementById('hotlink.message').innerHTML=document.getElementById('hotlink.message.superslow').value;
	} else if(isStatistic) {
		image+=srvname+'/'+timestamp+'_'+key+'.'+extension;
		document.getElementById('hotlink.message').innerHTML=document.getElementById('hotlink.message.slow').value;
	} else {
		image+=srvid+'.'+srvname+'/'+timestamp+'_'+key+'.'+extension;
		document.getElementById('hotlink.message').innerHTML=document.getElementById('hotlink.message.fast').value;
	}
	var link='http://'+srvname+'/show/'+timestamp+'/'+key+(isCopyright ? '/'+user : '');
	
	document.getElementById('hotlink.link.forum1').value='[URL='+link+'][IMG]'+image+'[/IMG][/URL]';
	document.getElementById('hotlink.link.forum2').value='[url='+link+'][img='+image+'][/url]';
	document.getElementById('hotlink.link.website').value='<a href="'+link+'"><img src="'+image+'" border="0" alt="Image hosted by Pixpack.net and Enlightware.com"/></a>';
	document.getElementById('hotlink.link.show').value=link;
	document.getElementById('hotlink.link.direct').value=image;
}

function menu(id) {
	var menu=document.getElementById('menu_'+id);
	var display;
	if(menu) {
		if(menu.style.display=='block') {
			display='block';
		} else {
			display='none';
		}
	}
	
	menu_close();
	
	if(menu) {
		if(display!='block') {
			menu.style.display='block';
			gIsOpenMenu=true;
		} else {
			menu.style.display='none';
			gIsOpenMenu=false;
		}
	}
}

function menu_close() {
	if(!gIsOpenMenu) return;
	
	var ids=document.getElementsByTagName('ul');
	for(i=0; i<ids.length; i++) {
		if(ids[i].id.indexOf('menu_')==0) ids[i].style.display='none';
	}
	gIsOpenMenu=false;
}

function image_open(img, width, height) {
	var x=screen.width;
	var y=screen.height;
	var isScroll;
	if(width>(x-10) || height>(y-50)) {
		if((width+10)>x) {
			width=x;
			if((height+50)>y) {
				width=width-20;
				height=y-50;
			}
		} else if((height+50)>y) {
			width=width-20;
			height=y-50;
		}
		isScroll=true;
	} else isScroll=false;
		
	//resize
	//var i=0;
	//if(navigator.appName=='Netscape') i=40;
	var win=window.open('','img','width='+width+', height='+height+', scrollbars='+(isScroll ? 1 : 0)+', dependent=1, hotkeys=0, location=0, menubar=0, resizable=0, statusbar=0, toolbar=0, personalbar=0');
	win.document.open();
	win.document.write('<html style="margin:0px; padding:0px"><head><title>PixPack</title></head>');
	win.document.write('<body style="margin:0px; padding:0px" onblur="window.close()"><a href="#" onclick="window.close()" style="text-decoration: none">');
	win.document.write('<img border="0" src="'+img+'" alt="Pixpack"/>');

	//if(win.document.images[0]) win.resizeTo(win.document.images[0].width+20, win.document.images[0].height+140-i);

	win.document.write('</a></body>');
	win.document.close();

	//focus
	win.focus();
	
	//set position
	win.moveTo(0,0);
}

function gallery_show(timestamp, key) {
	var win=window.open('/gallery.show.html?timestamp='+timestamp+'&key='+key,'','width=750, height=470, dependent=true, hotkeys=false, location=false, menubar=false, resizable=true, scrollbars=yes, statusbar=false, toolbar=false, personalbar=false');

	//set position
	win.moveTo(0,0);
	win.focus();
}

function gallery_hotlink(timestamp, key) {
	var link=document.getElementById("gallery.inlet");
	if(!link) return;
	link.style.display='none';

	link=document.getElementById("gallery.hotlink");
	if(!link) return;
	link.style.display='block';
	
	var input=document.getElementById('gallery.hotlink.field');
	if(!input) return;
	
	input.value='http://pixpack.net/gallery.show.html?timestamp='+timestamp+'&key='+key;
	input.focus();
	input.select();	
}
/*
function gallery_inlet(timestamp, key) {
	var link=document.getElementById("gallery.hotlink");
	if(!link) return;
	link.style.display='none';

	link=document.getElementById("gallery.inlet");
	if(!link) return;
	link.style.display='block';
	
	var input=document.getElementById('gallery.inlet.field');
	if(!input) return;
	
	var content='<script type="text/javascript" src="http://pixpack.net/inlet/gallery.js?timestamp='+timestamp+'&key='+key+'">';
	content+='\n\tPixPack Inlet\n';
	content+='</script>';
	
	input.value=content;
	
	input.focus();
	input.select();	
}
*/

function gallery_inlet(timestamp, key) {
	var link=document.getElementById("gallery.hotlink");
	if(!link) return;
	link.style.display='none';

	link=document.getElementById("gallery.inlet");
	if(!link) return;
	link.style.display='block';
	
	var input=document.getElementById('gallery.inlet.field');
	if(!input) return;
	
	var content='<script type="text/javascript" src="http://pixpack.net/inlet/gallery.js?timestamp='+timestamp+'&key='+key+'&width=640&height=480&css=">';
	content+='\n\t<noscript><h3>PixPack - Inlet</h3><a href="http://pixpack.net" title="PixPack">PixPack</a><p><b>PixPack - Free Image Hosting.</b></p></noscript>\n';
	content+='</script>';
	
	input.value=content;
	
	input.focus();
	input.select();	
}

function preview_show(timestamp, key) {
	var win=window.open('/preview.show.html?timestamp='+timestamp+'&key='+key,'','width=750, height=470, dependent=true, hotkeys=false, location=false, menubar=false, resizable=true, scrollbars=yes, statusbar=false, toolbar=false, personalbar=false');

	//set position
	win.moveTo(0,0);
	win.focus();
}

function preview_hotlink(timestamp, key) {
	var link=document.getElementById("preview.inlet");
	if(!link) return;
	link.style.display='none';

	link=document.getElementById("preview.hotlink");
	if(!link) return;
	link.style.display='block';
	
	var input=document.getElementById('preview.hotlink.field');
	if(!input) return;
	
	input.value='http://pixpack.net/preview.show.html?timestamp='+timestamp+'&key='+key;
	input.focus();
	input.select();	
}
/*
function preview_inlet(timestamp, key) {
	var link=document.getElementById("preview.hotlink");
	if(!link) return;
	link.style.display='none';

	link=document.getElementById("preview.inlet");
	if(!link) return;
	link.style.display='block';
	
	var input=document.getElementById('preview.inlet.field');
	if(!input) return;
	
	var content='<script type="text/javascript" src="http://pixpack.net/inlet/preview.js?timestamp='+timestamp+'&key='+key+'">';
	content+='\n\tPixPack Inlet\n';
	content+='</script>';
	
	input.value=content;
	
	input.focus();
	input.select();	
}
*/

function preview_inlet(timestamp, key) {
	var link=document.getElementById("preview.hotlink");
	if(!link) return;
	link.style.display='none';

	link=document.getElementById("preview.inlet");
	if(!link) return;
	link.style.display='block';
	
	var input=document.getElementById('preview.inlet.field');
	if(!input) return;
	
	var content='<script type="text/javascript" src="http://pixpack.net/inlet/preview.js?timestamp='+timestamp+'&key='+key+'">';
	content+='\n\t<noscript><h3>PixPack - Inlet</h3><a href="http://pixpack.net" title="PixPack">PixPack</a><p><b>PixPack - Free Image Hosting.</b></p></noscript>\n';
	content+='</script>';
	
	input.value=content;
	
	input.focus();
	input.select();	
}

function preview_allfolder() {
	if(disabled=document.getElementsByName('allfolder')[0].checked) document.getElementsByName('folder')[0].style.display='none';
	else document.getElementsByName('folder')[0].style.display='block';
}

/**
 * Saves the last selected folder.
 *
 * @deprecated
 */
function setFolder(obj) {
	alert((typeof obj));
}

function extra_bookmark(){
  if ((typeof window.sidebar=='object') && (typeof window.sidebar.addPanel=='function')) {
    if (window.location.protocol!='file:') window.sidebar.addPanel('PixPack','http://pixpack.net','');
  } else {
	  if ((typeof window.external=='object') && (typeof window.external.AddFavorite!='undefined')) {
		window.external.AddFavorite('http://pixpack.net','PixPack');
	  } else {
    	alert('Your browser doesn\'t support this function');
	  }
  }
  //close menu
  menu_close();
}

function extra_sidebar() {
  if ((typeof window.sidebar=='object') && (typeof window.sidebar.addPanel=='function')) {
    if (window.location.protocol!='file:') window.sidebar.addPanel('PixPack - Sidebar','http://pixpack.net/index.sidebar','');
  } else {
	  if ((typeof window.external=='object') && (typeof window.external.AddFavorite!='undefined')) {
		var bookmark="javascript:void(_search=open('http://pixpack.net/index.sidebar', '_search'))";
		window.external.AddFavorite(bookmark, 'PixPack - Sidebar');
	  } else {
    	alert('Your browser dosn\'t support this function');
	  }
  }
}

function createXHR() {
	var xhr=null;
	if (window.XMLHttpRequest) { // Mozilla, Safari,...
		xhr=new XMLHttpRequest();
		//if (xhr.overrideMimeType) xhr.overrideMimeType('text/xml');
  	} else if (window.ActiveXObject) { // IE
		try {
			xhr=new ActiveXObject('Msxml2.XMLHTTP');
		} catch (e) {
			try {
				xhr=new ActiveXObject('Microsoft.XMLHTTP');
			} catch (e) {
				xhr=null;
			}
		}
	}
	if(xhr==null) alert('Your browser doesn\'t support Ajax!');
	
	return xhr;
}
