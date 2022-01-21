package org.nuxeo.project.sample.beans;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.ibm.icu.text.SimpleDateFormat;
import com.steadystate.css.parser.ParseException;
import com.sun.star.io.IOException;

import java.util.Date;

import org.codehaus.jackson.map.JsonDeserializer;


public class CustomerDateAndTimeDeserialize extends JsonDeserializer<Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

   

	@Override
	public Date deserialize(org.codehaus.jackson.JsonParser jp, org.codehaus.jackson.map.DeserializationContext ctxt)
			throws java.io.IOException, org.codehaus.jackson.JsonProcessingException {
		// TODO Auto-generated method stub
		
		String str = null;
		try {
			str = jp.getText().trim();
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ctxt.parseDate(str);
	}
}