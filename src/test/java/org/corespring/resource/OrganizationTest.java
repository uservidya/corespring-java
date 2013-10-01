package org.corespring.resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class OrganizationTest {

  @Test
  public void testSerialization() throws IOException {
    Organization organization = new Organization("id", "name", new ArrayList<String>());
    ObjectMapper objectMapper = new ObjectMapper();

    assertEquals("{\"id\":\"id\",\"name\":\"name\",\"path\":[]}", objectMapper.writeValueAsString(organization));
  }

  @Test
  public void testDeserialization() throws IOException {
    String json = "{\"id\":\"id\",\"name\":\"name\",\"path\":[]}";
    ObjectMapper objectMapper = new ObjectMapper();
    Organization deserialized = objectMapper.readValue(json, Organization.class);

    assertEquals("id", deserialized.getId());
    assertEquals("name", deserialized.getName());
    assertEquals(Collections.EMPTY_LIST, deserialized.getPath());
  }

}