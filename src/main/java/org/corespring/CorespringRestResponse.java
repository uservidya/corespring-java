package org.corespring;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorespringRestResponse {

  private final String responseText;
  private final int httpStatus;
  private final String url;
  private final String queryString;
  private final boolean error;

  public CorespringRestResponse(String url, String text, int status) {
    Pattern p = Pattern.compile("([^?]+)\\??(.*)");
    Matcher m = p.matcher(url);
    m.matches();
    this.url = m.group(1);
    this.queryString = m.group(2);
    this.responseText = text;
    this.httpStatus = status;
    this.error = (status >= 400);
  }

  public <T> T get(Class<T> clazz) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(responseText, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> Collection<T> getAll(Class<T> clazz) {
    ObjectMapper objectMapper = new ObjectMapper();
    TypeFactory typeFactory = TypeFactory.defaultInstance();
    try {
      return objectMapper.readValue(responseText, typeFactory.constructCollectionType(List.class, clazz));
    } catch (IOException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

  public String getResponseText() {
    return responseText;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public String getUrl() {
    return url;
  }

  public String getQueryString() {
    return queryString;
  }

  public boolean isError() {
    return error;
  }

}
