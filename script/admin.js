var admin_content_loader=null;

function admin_load() {
	if(admin_content_loader==null) return;
	var result=admin_content_loader.xhr.responseText;
	document.getElementById('suggestion').innerHTML=result;
	
	admin_content_loader=null;
	
	ajax_status_stop();
}

function admin_loading() {
	ajax_status_start();
}

function admin_error() {
	admin_folder_loader=null;
	admin_folder_content_loader=null;
	admin_file_loader=null;
	admin_file_content_loader=null;
	admin_content_loader=null;
	
	ajax_status_stop();
	alert('Communication Error');
}

function admin_referer_move_mail() {
	var form=document.getElementById('move_form');
	var mail=document.getElementById('mail');

	admin_content_loader=new ajax.ContentLoader('/api/account/mail', admin_load, admin_loading, admin_error);
	//admin_content_loader.param='list=restore&content='+fileman_correct(name);
	//admin_content_loader.post();
	admin_content_loader=ajax_submitForm(form);
}

function admin_referer_move_folder() {
	var form=document.getElementById('move_form');
	var mail=document.getElementById('mail');
	var folder=document.getElementById('folder');
	
}