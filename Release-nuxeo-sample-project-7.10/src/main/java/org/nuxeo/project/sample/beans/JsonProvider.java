package org.nuxeo.project.sample.beans;


import java.text.SimpleDateFormat;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonProvider extends JacksonJaxbJsonProvider {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  static {
    // allow only non-null fields to be serialized
    objectMapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);

    SerializationConfig serConfig = objectMapper.getSerializationConfig();
    serConfig.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
    DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
    deserializationConfig.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
    objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

  }

  public JsonProvider() {
    super.setMapper(objectMapper);
  }
}