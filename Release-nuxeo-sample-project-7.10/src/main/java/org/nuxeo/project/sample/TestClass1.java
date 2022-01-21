package org.nuxeo.project.sample;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestClass1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		   System.out.println("ffff"); 

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();  
		   System.out.println(now); 
		   System.out.println(dtf.format(now));  
		   
	}

}
