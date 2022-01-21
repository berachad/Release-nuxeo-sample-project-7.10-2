package org.nuxeo.project.sample.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class JacksonConfig {


    public ObjectMapper createObjectMapper() {  
        ObjectMapper mapper = new ObjectMapper();
       // mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}