/**
 * Ajax implementation
 */
var ajax=new Object();
ajax.READY_STATE_UNINITIALIZED=0;
ajax.READY_STATE_LOADING=1;
ajax.READY_STATE_LOADED=2;
ajax.READY_STATE_INTERACTIVE=3;
ajax.READY_STATE_COMPLETE=4;

ajax.ContentLoader=function(url, onload, onloading, onerror) {
	this.url=url;
	this.xhr=null;
	this.param=null;
	this.onload=(onload) ? onload : this.defaultLoad;;
	this.onloading=(onloading) ? onloading : this.defaultLoading;
	this.onerror=(onerror) ? onerror : this.defaultError;
	this.xhr=ajax_createXHR();
	
}

ajax.ContentLoader.prototype= {
    get:function() {
		if (this.xhr) {
		    try {
				var loader=this;
				this.xhr.onreadystatechange=function() {
				    loader.onReadyState.call(loader);
				}
				
				//IE-bug
				var query4IE;
				if(this.url.indexOf('?')!=-1) query4IE='&query4IE='+Math.random();
				else query4IE='?query4IE'+Math.random();
				
				this.xhr.open('GET',this.url+query4IE,true);
				this.xhr.send(null);
		    } catch (err) {
				this.onerror.call(this);
		    }
		}
	},
    post:function() {
		if (this.xhr) {
		    try {
				var loader=this;
				this.xhr.onreadystatechange=function() {
				    loader.onReadyState.call(loader);
				}

				//IE-bug
				var query4IE;
				if(this.url.indexOf('?')!=-1) query4IE='&query4IE'+Math.random();
				else query4IE='?query4IE'+Math.random();
				
				this.xhr.open('POST',this.url,true);
				//IE bug
				this.xhr.setRequestHeader('If-Modified-Since', 'Wed, 15 Nov 1995 04:58:08 GMT');
				//POST-encoding
				this.xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
				this.xhr.setRequestHeader( 'Content-length', this.param.length );

				this.xhr.send(this.param);
		    } catch (err) {
				this.onerror.call(this);
		    }
		}
	},
	onReadyState:function() {
		var ready=this.xhr.readyState;
		if (ready==ajax.READY_STATE_COMPLETE) {
		    var httpStatus=this.xhr.status;
		    if (httpStatus==200 || httpStatus==0) {
		        this.onload.call(this);
		    } else {
		        this.onerror.call(this);
		    }
		} if (ready==ajax.READY_STATE_LOADING) {
		    this.onloading.call(this);
		}
	},
	defaultLoad:function() {
	alert('Error calling default loader\n' +
		'\nSet a default caller!');
	},
	defaultLoading:function() {
	},
	defaultError:function() {
		alert('Error fetching data\n' +
			'\nreadyState: '+this.xhr.readyState +
			'\nstatus: '+this.xhr.status +
			'\nheaders: '+this.xhr.getAllResponseHeaders());
	}
}

//loader holds the response-Object
var ajax_loader=null;
//ID for the Ajax-entry
var ajax_window=null;

function ajax_createXHR() {
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
			} catch (e) {}
		}
	}
	return xhr;
}	

function ajax_open(url) {
	ajax_loader=new ajax.ContentLoader(url, ajax_load, ajax_loading, ajax_error);
	ajax_loader.get();
}


function ajax_load() {
	//alternativ and simple way
	document.getElementById(ajax_window).innerHTML=ajax_loader.xhr.responseText;
	ajax_status_stop();	
	//break
	if(true) return;

	//define elements
	var xml=ajax_loader.xhr.responseXML;
	var root;
	try {
		root=xml.documentElement;
		if(root.nodeName=='redirect') {
			document.location.href=root.firstChild.nodeValue;
			return;
		}
		if(root.nodeName=='error') {
		alert('Internal System-Error!!!');
		ajax_status_stop();	
		return;
		}
	} catch(e) {
		alert('Communication-Error!!!\n\nData:\n'+ajax_loader.xhr.responseText);
		ajax_status_stop();	
		return;
	}

 
	//interpret & remove script-tags	
	var scripts=xml.getElementsByTagName('script');
	var length=scripts.length;
	var content;
	var script='';
	for(i=0; i<length; i++) {
		if(!scripts[i]) continue;
		try {
			content=scripts[i].firstChild;
			if(content!=null) script+=content.nodeValue+';\n';
		} catch(e) {
			alert(e.message);//do nothing
		}
	}

	//dont't remove nodes while reading the same set (Array)
	for(i=length-1; i>=0; i--) {
		if(!scripts[i]) continue;
		try {
			scripts[i].parentNode.removeChild(scripts[i]);	//'0' - the next node
		} catch(e) {
			alert(e.message);//do nothing
		}
	}
 
 
	//get window-node
	var element=document.getElementById(ajax_window);

	//remove all elements of the module
	var length=element.childNodes.length;
	for(i=0; i<length; i++) {
		element.removeChild(element.childNodes[0]);	//'0' means the first node
	}
		
	//clone the node (IE)
	var node;
	if(document.importNode) {
		node=document.importNode(root,true);
	} else {
		node=root.cloneNode(true);
	}

	//IE-dom-alias
	if(document.body.outerHTML && node.xml) element.insertAdjacentHTML('beforeEnd',node.xml);
	else element.appendChild(node);

	ajax_status_stop();	

	//call the script (after include)
	if(!document.importNode && png2ie) png2ie(ajax_window);
	if(script!='') eval(script);
}

function ajax_loading() {
	ajax_status_start();
}

function ajax_error() {
	ajax_status_stop();
	alert('Communication-Error!!!\n\nThe communication with the server was broken');
}


function ajax_submitForm(form) {
	if(ajax_loader!=null) return;

	var url=form.action;
	if(url==null || url=='.') url='';	//window.moduleFunction

	var param=ajax_serializeForm(form);

	if(param.indexOf('submit')==-1) param+='&submit=true';

	ajax_loader=new ajax.ContentLoader(url, ajax_load, ajax_loading, ajax_error);
	ajax_loader.param=param;
	//alert(ajax_serializeForm(ajax_loader.param));
	ajax_loader.post();
}

function ajax_serializeForm(form) {
	var els=form.elements;
	var len=els.length;
	var queryString='';
	this.addField=function(name,value) {
		if(queryString.length>0) {
			queryString+='&';
		}
		queryString +=encodeURIComponent(name) + '=' + encodeURIComponent(value);
	};
	
	for(var i=0;i<len;i++) {
		var el =els[i];
		if(!el.disabled){
			switch(el.type) {
				case 'text':
				case 'password':
				case 'hidden':
				case 'textarea':
					this.addField(el.name,el.value);
				break;
				case 'select-one':
						if(el.selectedIndex>=0) {
						this.addField(el.name,el.options[el.selectedIndex].value);
					}
				break;
					case 'select-multiple':
					for(var j=0;j<el.options.length;j++) {
						if(el.options[j].selected) {
							this.addField(el.name,el.options[j].value);
						}
					}
				break;
				case 'checkbox':
				case 'radio':
					if(el.checked) {
						this.addField(el.name,el.value);
					}
				break;
			}
		}
	}
	return queryString;
}


/* Status */
var ajax_status_isLoading=false;
var ajax_status_call_start=ajax_status_default_start;
var ajax_status_call_stop=ajax_status_default_stop;

function ajax_status_start() {
	if(ajax_status_isLoading) return;
	ajax_status_isLoading=true;
	document.body.style.cursor='wait';
	ajax_status_call_start.call();
}

function ajax_status_stop() {
	ajax_status_isLoading=false;
	ajax_loader=null;
	ajax_status_call_stop.call();
	document.body.style.cursor='auto';
}

/**
 * Sets the function that is called while loading.
 * Note! his function dosn't work fine for all browsers.
 */
function ajax_status_call(funct) {
	if(!funct || funct==null || funct=='null') funct='default';
	
	ajax_status_call_start=eval('ajax_status_'+funct+'_start');
	ajax_status_call_start=eval('ajax_status_'+funct+'_stop');
}

/*Status-default*/
function ajax_status_default_start() {
	if(document.getElementById('ajax_status')) document.getElementById('ajax_status').style.display='inline';
}

function ajax_status_default_stop() {
	if(document.getElementById('ajax_status')) document.getElementById('ajax_status').style.display='none';
}

/*Status-spin*/
var ajax_status_spin_symbol=new Array('-','\\','|','/','-','\\','|','/');
var ajax_status_spin_position=0;
var ajax_status_spin_timeout=200;
var ajax_status_spin_isRunning=false;

function ajax_status_spin_run() {
	if(!ajax_status_spin_isRunning) return;
	var ele=document.getElementById('ajax_status');
	if(!ele) return;
	ele.innerHTML='&#160;'+ajax_status_spin_symbol[ajax_status_spin_position]+'&#160;';
	
	if(ajax_status_spin_position<ajax_status_spin_symbol.length-1) ajax_status_spin_position++;
	else ajax_status_spin_position=0;

	setTimeout('ajax_status_spin_run()',ajax_status_spin_timeout);
}
			
function ajax_status_spin_start() {
	if(document.getElementById('ajax_status')) {
		document.getElementById('ajax_status').style.display='inline';
		ajax_status_spin_isRunning=true;
		ajax_status_spin_run();
	}
}

function ajax_status_spin_stop() {
	if(document.getElementById('ajax_status')) document.getElementById('ajax_status').style.display='none';
	ajax_status_spin_isRunning=false;
}