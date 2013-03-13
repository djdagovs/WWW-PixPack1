var _voteObjs=new Array();
var _voteURL='/vote';
var _voteText='';
var _votePrefix='vote';

function vote_obj() {
	this.id=-1;
	this.value=-1;
	this.send=false;
}

function vote_set(id, value) {
	if(!id) return;
	var voteId=document.getElementById(_votePrefix+id);
	if(!voteId) return;
	
	if(value<0 || value>10) value=5;

	var i;
	for(i=0; i<_voteObjs.length; i++) {
		if(_voteObjs[i].id==id) break;
	}
	if(i<_voteObjs.length) {
		if(_voteObjs[i].send) return;
		_voteObjs[i].value=value;
	} else {
		var idx=_voteObjs.length;
		_voteObjs[idx]=new vote_obj();
		_voteObjs[idx].id=id;
		_voteObjs[idx].value=value;
	}
	voteId.style.backgroundImage='url(/image/vote/'+value+'.gif)';
}

function vote_minus(id) {
	if(!id) return;

	for(i=0; i<_voteObjs.length; i++) {
		if(_voteObjs[i].id==id) {
			if(_voteObjs[i].value>0) {
				vote_set(id, _voteObjs[i].value-1);
			}
			break;
		}
	}
}

function vote_plus(id) {
	if(!id) return;

	for(i=0; i<_voteObjs.length; i++) {
		if(_voteObjs[i].id==id) {
			if(_voteObjs[i].value<10) {
				vote_set(id, _voteObjs[i].value+1);
			}
			break;
		}
	}
}

function vote_send(id) {
	if(!id) return;
	
	var i;
	for(i=0; i<_voteObjs.length; i++) {
		if(_voteObjs[i].id==id) break;
	}
	if(i>_voteObjs.length) {
		alert('Id not found ('+id+')');
		return;
	}

	if(_voteObjs[i].send) return;
	else _voteObjs[i].send=true;
	
	var xhr=createXHR();
	if(xhr==null) return;

	//state
	xhr.onreadystatechange=function() {
		if(xhr.readyState==4)
			if(xhr.status==200) {
				//var voteId=document.getElementById(_votePrefix+id+'_');
				//if(!voteId) return;
				//voteId.style.backgroundImage='';
				//voteId.innerHTML=_voteText;
				//alert(xhr.responseText);
			} else {
				alert('Connection-Error\n'+xhr.error);
			}
	}
	
	var query='id='+_voteObjs[i].id+'&value='+_voteObjs[i].value;
	query+='&load='+Math.random();
	
	xhr.open('POST',_voteURL);
	xhr.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

	try {
		xhr.send(query);
	} catch(e) {
		alert('Internal error:\n'+e);
	}
}
