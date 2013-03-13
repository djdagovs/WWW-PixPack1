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
