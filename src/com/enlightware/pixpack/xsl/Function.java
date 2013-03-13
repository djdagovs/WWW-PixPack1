package com.enlightware.pixpack.xsl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.laukien.i18n.Date;

public class Function {
	//Fri, 13 Jul 2007 20:50:31 +0000
	public static String convertBlogDate(String pDate, String pLanguage) {
		Date date=new Date(new Locale(pLanguage));
		SimpleDateFormat decode=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
		try {
			date.setDate(decode.parse(pDate));
		} catch (ParseException e) {
			return "ERROR: "+pDate;
		}
		date.setFormat(Date.LONG);
		return date.toString();
	}
}
