package org.nuxeo.project.sample.services;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DatePojo {
	 @JsonInclude(value= JsonInclude.Include.NON_EMPTY)
	    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MMM-yyyy", timezone="PST")
	    @JsonProperty("date")
	    private Date date;

}
