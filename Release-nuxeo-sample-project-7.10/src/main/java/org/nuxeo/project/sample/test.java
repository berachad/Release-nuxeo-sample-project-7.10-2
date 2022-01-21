package org.nuxeo.project.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.icu.text.SimpleDateFormat;




public class test {

	public static void main(String[] args) throws Exception {

//	Test
	
	List<Map<String, String>> ss = new ArrayList<>();
	ss.add((Map<String, String>) new HashMap<>().put("y", "name"));
	if(ss.contains("y")) {
		System.out.println("ddd");
	}
	
	
//	TEST 
// Test
	Calendar calendar = Calendar.getInstance();
	System.out.println(calendar);
    GregorianCalendar gc = (GregorianCalendar) calendar;
    String dateStr = "2011-10-06T12:00:00-08:00";
    SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy");
    Date date = dateParser.parse(dateStr);
    System.out.println(dateFormatter.format(date)); 
	}
}
