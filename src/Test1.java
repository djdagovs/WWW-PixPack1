import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;


public class Test1 {

	public static void main(String[] args) throws ImageFormatException, IOException {
		Opener open=new Opener();
		ImagePlus imp=open.openImage("logo.png");
		if (imp==null) {
		    ImageProcessor ip  = new ColorProcessor(300,200);
		    ip.setColor(Color.white);
		    ip.drawString("Error opening image: " 
		      + new File(".").getAbsolutePath(),10,20);
		    imp = new ImagePlus("Debug", ip);
		}

		ImageProcessor ip=imp.getProcessor();
		ip=ip.resize(300, 300);
		ip.setColor(Color.red);
		ip.xor(100);
		ip.drawString("Test",10,20);
		ip.drawLine(0,0,100,100);


		//output jpeg file
		FileOutputStream   out   = new FileOutputStream("logo.jpg");
		BufferedOutputStream  bout  = new BufferedOutputStream(out);

		BufferedImage   bi  = new BufferedImage(ip.getWidth(), 
		    ip.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		g.drawImage(ip.createImage(), 0, 0, null);
		g.dispose();            
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bout);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		param.setQuality(50f, true);
		encoder.encode(bi, param);

		bout.close();
		out.close();
		

		
	}
}
