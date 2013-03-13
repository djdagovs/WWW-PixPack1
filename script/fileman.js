/* FileMan */

ajax_status_call_start=ajax_status_spin_start;
ajax_status_call_stop=ajax_status_spin_stop;

var fileman_folder_loader=null;
var fileman_folder_content_loader=null;
var fileman_file_loader=null;
var fileman_file_content_loader=null;
var fileman_content_loader=null;

var fileman_folder_name;
var fileman_folder_reload=null;
var fileman_file_name;
var fileman_file_reload=null;


/**
 * Disables all button-elements in the fileman-content-area.
 */
function fileman_disable() {
	var contentId=document.getElementById('design_fileman_content');
	if(contentId && contentId!=null) {
		var buttonId=contentId.getElementsByTagName('button');
		for(var i=0; i<buttonId.length; i++) {
			buttonId[i].disabled=true;
		}
	}
}

/**
 * Common Ajax-Loader which will be called by the most functions.
 * The Cursour-focus will be set to the first input/textarea/button-element.
 */
function fileman_load() {
	if(fileman_content_loader==null) return;
	var result=fileman_content_loader.xhr.responseText;
	
	if(result.indexOf('<')==-1) document.getElementById('design_message').innerHTML=result;
	else {
		document.getElementById('design_message').innerHTML='&nbsp;';
		var content=document.getElementById('design_fileman_content');
		content.innerHTML=result;
		
		try {
			var input=content.getElementsByTagName('input');
			if(input.length>0) input[0].focus();
			else {
				input=content.getElementsByTagName('textarea');
				if(input.length>0) input[0].focus();
				else {
					input=content.getElementsByTagName('button');
					if(input.length>0) input[0].focus();
				}
			}
		} catch(e) {
			//IE bug
		}
	}

	fileman_content_loader=null;
	
	if(fileman_folder_reload!=null) {
		fileman_folder(fileman_folder_reload);
		fileman_folder_reload=null;
	}

	if(fileman_file_reload!=null) {
		fileman_file(fileman_file_reload);
		fileman_file_reload=null;
	}
	
	ajax_status_stop();
}

function fileman_loading() {
	ajax_status_start();
	menu_close();
}

function fileman_error() {
	fileman_folder_loader=null;
	fileman_folder_content_loader=null;
	fileman_file_loader=null;
	fileman_file_content_loader=null;
	fileman_content_loader=null;
	
	ajax_status_stop();
	alert('Communication Error');
}

function fileman_folder(name, offset) {
	var url='/fileman';
	fileman_content_loader=null;
	fileman_folder_name=name;
	
	if(!offset || offset==null) offset=0;
	
	if(!name || name.length==0) {
		document.getElementById('fileman_navigation_root').style.display='block';
		document.getElementById('fileman_navigation_folder').style.display='none';
		document.getElementById('fileman_navigation_file').style.display='none';
		document.getElementById('fileman_navigation_restore').style.display='none';
	} else {
		document.getElementById('fileman_navigation_root').style.display='none';
		document.getElementById('fileman_navigation_folder').style.display='block';
		document.getElementById('fileman_navigation_file').style.display='none';
		document.getElementById('fileman_navigation_restore').style.display='none';
	}
	
	if(fileman_folder_loader==null) {
		fileman_folder_loader=new ajax.ContentLoader(url, fileman_folder_load, fileman_loading, fileman_error);
		fileman_folder_loader.param='list=folder&content='+fileman_correct(name);
		fileman_folder_loader.post();
	}

	if(fileman_file_loader==null) {
		fileman_file_loader=new ajax.ContentLoader(url, fileman_file_load, fileman_loading, fileman_error);
		fileman_file_loader.param='list=file&content='+fileman_correct(name)+'&offset='+offset;
		fileman_file_loader.post();
	}
/*
	if(fileman_folder_content_loader==null) {
		fileman_folder_content_loader=new ajax.ContentLoader(url, fileman_folder_content_load, fileman_loading, fileman_error);
		fileman_folder_content_loader.param='type=folder&content='+fileman_correct(name);
		fileman_folder_content_loader.post();
	}
*/
}


function fileman_file(name) {
	var url='/fileman';
	fileman_content_loader=null;
	fileman_file_name=name;
	
	if(!name || name.length==0) {
		document.getElementById('fileman_navigation_root').style.display='none';
		document.getElementById('fileman_navigation_folder').style.display='none';
		document.getElementById('fileman_navigation_file').style.display='block';
		document.getElementById('fileman_navigation_restore').style.display='none';
	} else {
		document.getElementById('fileman_navigation_root').style.display='none';
		document.getElementById('fileman_navigation_folder').style.display='none';
		document.getElementById('fileman_navigation_file').style.display='block';
		document.getElementById('fileman_navigation_restore').style.display='none';
	}

	if(fileman_file_content_loader==null) {
		fileman_file_content_loader=new ajax.ContentLoader(url, fileman_file_content_load, fileman_loading, fileman_error);
		fileman_file_content_loader.param='type=file&content='+fileman_correct(name);
		fileman_file_content_loader.post();
	}
}


function fileman_restore(name) {
	if(fileman_file_content_loader==null) {
		if(!name || name.length==0) {
			name='';

			document.getElementById('fileman_navigation_root').style.display='none';
			document.getElementById('fileman_navigation_folder').style.display='none';
			document.getElementById('fileman_navigation_file').style.display='none';
			document.getElementById('fileman_navigation_restore').style.display='block';
		} else {
			document.getElementById('fileman_navigation_root').style.display='none';
			document.getElementById('fileman_navigation_folder').style.display='none';
			document.getElementById('fileman_navigation_file').style.display='none';
			document.getElementById('fileman_navigation_restore').style.display='block';
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='list=restore&content='+fileman_correct(name);
		fileman_content_loader.post();
	}
}

function fileman_restore_delete() {
	if(fileman_file_content_loader==null) {

		fileman_folder_reload=fileman_folder_name;
		
		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=restore.delete';
		fileman_content_loader.post();
	}
}

function fileman_call(cmd, content) {
	var url='/fileman';
	if(!content || content==null) content='';
	
	if(fileman_content_loader==null) {
		fileman_content_loader=new ajax.ContentLoader(url, fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type='+cmd+'&content='+fileman_correct(content);
		fileman_content_loader.post();
	}
}

function fileman_folder_load() {
	if(fileman_folder_loader==null) return;
	document.getElementById('design_fileman_folder').innerHTML=fileman_folder_loader.xhr.responseText;
	fileman_folder_loader=null;

	ajax_status_stop();
}			

function fileman_file_load() {
	if(fileman_file_loader==null) return;
	document.getElementById('design_fileman_content').innerHTML=fileman_file_loader.xhr.responseText;
	fileman_file_loader=null;

	ajax_status_stop();
}

function fileman_content_load() {
	if(fileman_content_loader==null) return;
	document.getElementById('design_fileman_content').innerHTML=fileman_content_loader.xhr.responseText;
	fileman_content_loader=null;

	ajax_status_stop();
}

function fileman_folder_content_load() {
	if(fileman_folder_content_loader==null) return;
	document.getElementById('design_fileman_content').innerHTML=fileman_folder_content_loader.xhr.responseText;
	fileman_folder_content_loader=null;

	ajax_status_stop();
}			

function fileman_file_content_load() {
	if(fileman_file_content_loader==null) return;
	document.getElementById('design_fileman_content').innerHTML=fileman_file_content_loader.xhr.responseText;
	fileman_file_content_loader=null;

	ajax_status_stop();
}

/* Functions */

function fileman_folder_add() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_folder_reload='';
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=folder.add&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_folder_rename() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_folder_reload=value;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=folder.rename&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_folder_move() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var method=document.getElementById('fileman.method');
		var dest='';
		var value;
		if(!method) value='';
		else {
			method=method.value;
			if(method=='global' || method=='local') {
				var select=document.getElementById('fileman.'+method).getElementsByTagName('select');
				for(i=0; i<select.length; i++) {
					if(select[i].name==method) {
						dest=select[i].value;
						break;
					}
				}
			}
			value=method+':'+dest;
		
			if(method=='local') fileman_folder_reload=dest;
			else fileman_folder_reload='';
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=folder.move&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_folder_move_switch(dest) {
	fileman_disable();

	document.getElementById('fileman.method').value=dest;
	if(dest=='local') {
		document.getElementById('fileman.global').className='hidden';
		document.getElementById('fileman.local').className='visible';
		document.getElementsByName('move')[0].checked=true;
	} else if(dest=='global') {
		document.getElementById('fileman.global').className='visible';
		document.getElementById('fileman.local').className='hidden';
		document.getElementsByName('move')[1].checked=true;
	} else {
		document.getElementById('fileman.global').className='hidden';
		document.getElementById('fileman.local').className='hidden';
	}	
}

function fileman_folder_delete() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_folder_reload='';
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=folder.delete&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_folder_status() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.status.public');
		if(!value) value='';
		else {
			value=(document.getElementById('fileman.status.public').checked ? '1' : '0') + '' + (document.getElementById('fileman.status.adult').checked ? '1' : '0');
			fileman_folder_reload=fileman_folder_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=folder.status&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_rename() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_file_reload=fileman_file_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.rename&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_move() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var method=document.getElementById('fileman.method');
		var dest='';
		var value;
		if(!method) value='';
		else {
			method=method.value;
			if(method=='global' || method=='local') {
				var select=document.getElementById('fileman.'+method).getElementsByTagName('select');
				for(i=0; i<select.length; i++) {
					if(select[i].name==method) {
						dest=select[i].value;
						break;
					}
				}
			}
			value=method+':'+dest;
		
			fileman_folder_reload=fileman_folder_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.move&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_desc() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.text');
		if(!value) value='';
		else {
			value=value.value;
			fileman_file_reload=fileman_file_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.desc&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_status() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.status.public');
		if(!value) value='';
		else {
			value=(document.getElementById('fileman.status.public').checked ? '1' : '0') + '' + (document.getElementById('fileman.status.adult').checked ? '1' : '0');
			fileman_file_reload=fileman_file_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.status&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_delete() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_folder_reload=fileman_folder_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.delete&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_trim() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_folder_reload=fileman_folder_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.trim&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_strip() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_folder_reload=fileman_folder_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.strip&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

function fileman_file_protect() {
	fileman_disable();

	if(fileman_content_loader==null) {
		var value=document.getElementById('fileman.name');
		if(!value) value='';
		else {
			value=value.value;
			fileman_file_reload=fileman_file_name;
		}

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=file.protect&content='+fileman_correct(value);
		fileman_content_loader.post();
	}
}

/* Filter */


function fileman_filter_simple(filter) {
	if(!filter) return;
	if(fileman_content_loader==null) {
		var file=document.getElementById('fileman.file');
		var folder=document.getElementById('fileman.folder');
		if(!file || !folder) return;

		fileman_content_loader=new ajax.ContentLoader('/fileman', fileman_load, fileman_loading, fileman_error);
		fileman_content_loader.param='type=filter.'+filter+'&file='+fileman_correct(file.value)+'&folder='+fileman_correct(folder.value);
		fileman_content_loader.post();

		fileman_folder_reload=folder.value;
	}
}

/**
 * Download-Dialog which builds the parameter-string and calls the download-API.
 */
function fileman_file_download() {
	if(fileman_content_loader==null) {
		var format=document.getElementById('fileman.format');
		var name=document.getElementById('fileman.name');
		var filename=document.getElementById('fileman.filename');
		if(!format || !name || !filename) return;
		
		document.location.href='/download/'+filename.value+'?show=false&format='+format.value+'&name='+name.value;
	}
}

/**
 * Shows a tag which is specified by an id.
 */
function fileman_show(id) {
	menu();
	var fields=document.getElementsByTagName('fieldset');
	for(i=0; i<fields.length; i++) {
		fields[i].className='hidden';
	}
	
	var parent=document.getElementById(id);
	if(!parent) {
		alert('"'+id+'" is not implemented yet!');
		return;
	} else parent=parent.parentNode;
	
	if(parent.nodeName=='FIELDSET') parent.className='visible';
}

function fileman_correct(string) {
	if(!string) return '';
	string=string.replace('\'','?');
	string=string.replace('\"','?');
	return string;
}

