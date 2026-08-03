package com.hei.school.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EndpointSecurityIT {

  @DynamicPropertySource
  static void awsProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.eventBridge.bus", () -> "dummy-bus");
    registry.add("aws.s3.bucket", () -> "dummy-bucket");
  }

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @Test
  void getMovies_isPublic() throws Exception {
    mockMvc.perform(get("/movies")).andExpect(status().isOk());
  }

  @Test
  void getProjections_isPublic() throws Exception {
    mockMvc.perform(get("/projections")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "CLIENT")
  void putMovie_asClient_returns403() throws Exception {
    mockMvc
        .perform(put("/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "EMPLOYEE")
  void putMovie_asEmployee_returns403() throws Exception {
    mockMvc
        .perform(put("/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void putMovie_asManager_returns200() throws Exception {
    mockMvc
        .perform(put("/movies").contentType(MediaType.APPLICATION_JSON).content(movieJson()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("New Movie"));
  }

  @Test
  @WithMockUser(roles = "CLIENT")
  void putProjection_asClient_returns403() throws Exception {
    mockMvc
        .perform(
            put("/projection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectionJsonFromSeededData()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "EMPLOYEE")
  void putProjection_asEmployee_returns403() throws Exception {
    mockMvc
        .perform(
            put("/projection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectionJsonFromSeededData()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "MANAGER")
  void putProjection_asManager_returns200() throws Exception {
    mockMvc
        .perform(
            put("/projection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(projectionJsonFromSeededData()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.seatPrice").value(6000));
  }

  private String movieJson() throws Exception {
    return objectMapper.writeValueAsString(
        Map.of(
            "title", "New Movie",
            "genre", "ACTION",
            "description", "test",
            "durationMinutes", 120));
  }

  private String projectionJsonFromSeededData() throws Exception {
    String body =
        mockMvc.perform(get("/projections")).andReturn().getResponse().getContentAsString();
    JsonNode first = objectMapper.readTree(body).get(0);
    return objectMapper.writeValueAsString(
        Map.of(
            "datetime",
            "2026-08-01T20:00:00Z",
            "seatPrice",
            6000,
            "movieId",
            first.get("movieId").asText(),
            "roomId",
            first.get("roomId").asText()));
  }
}
