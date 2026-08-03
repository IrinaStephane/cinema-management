package com.hei.school.endpoint.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.school.entity.Projection;
import com.hei.school.entity.Reservation;
import com.hei.school.entity.Seat;
import com.hei.school.entity.User;
import com.hei.school.entity.UserRole;
import com.hei.school.repository.ProjectionRepository;
import com.hei.school.repository.ReservationRepository;
import com.hei.school.repository.SeatRepository;
import com.hei.school.repository.UserRepository;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AuthReservationSecurityIT {

  @DynamicPropertySource
  static void awsProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.eventBridge.bus", () -> "dummy-bus");
    registry.add("aws.s3.bucket", () -> "dummy-bucket");
  }

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired UserRepository userRepository;
  @Autowired ProjectionRepository projectionRepository;
  @Autowired SeatRepository seatRepository;
  @Autowired ReservationRepository reservationRepository;

  private static final AtomicInteger SEQUENCE = new AtomicInteger();

  private String clientEmail;
  private String managerEmail;
  private String clientToken;
  private String managerToken;
  private Projection projection;
  private Seat seat;

  @BeforeEach
  void setUp() throws Exception {
    int n = SEQUENCE.incrementAndGet();
    clientEmail = "it-client-" + n + "@test.com";
    managerEmail = "it-manager-" + n + "@test.com";
    clientToken = register(clientEmail, UserRole.CLIENT);
    managerToken = register(managerEmail, UserRole.MANAGER);
    projection = projectionRepository.findAll().get(0);
    seat = seatRepository.findAll().get(0);
  }

  private String register(String email, UserRole role) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            Map.of(
                                "firstName", "IT",
                                "lastName", "User",
                                "birthdate", "2000-01-01",
                                "email", email,
                                "password", "password123",
                                "role", role.name()))))
            .andExpect(status().isCreated())
            .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
  }

  private String clientToken() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            Map.of("email", clientEmail, "password", "password123"))))
            .andExpect(status().isOk())
            .andReturn();
    return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
  }

  private Reservation createReservation(User user) {
    return reservationRepository.save(
        Reservation.builder()
            .user(user)
            .projection(projection)
            .seat(seat)
            .createdAt(Instant.now())
            .build());
  }

  private String reservationJson() throws Exception {
    return objectMapper.writeValueAsString(
        Map.of("projectionId", projection.getId(), "seatId", seat.getId()));
  }

  // ----- AUTH -----

  @Test
  void register_returnsToken() throws Exception {
    String token = clientToken();
    token.isEmpty();
  }

  @Test
  void register_duplicateEmail_returns409() throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "firstName", "IT",
                            "lastName", "User",
                            "birthdate", "2000-01-01",
                            "email", clientEmail,
                            "password", "password123",
                            "role", "CLIENT"))))
        .andExpect(status().isConflict());
  }

  @Test
  void login_returnsToken() throws Exception {
    String token = clientToken();
    token.isEmpty();
  }

  @Test
  void login_wrongPassword_returns403() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of("email", clientEmail, "password", "wrong"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void login_unknownEmail_returns403() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of("email", "unknown@test.com", "password", "password123"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void register_invalidPayload_returns400() throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"not-an-email\"}"))
        .andExpect(status().isBadRequest());
  }

  // ----- GET /reservations -----

  @Test
  void getReservations_withoutToken_returns403() throws Exception {
    mockMvc.perform(get("/reservations")).andExpect(status().isForbidden());
  }

  @Test
  void getReservations_asClient_returns403() throws Exception {
    mockMvc
        .perform(get("/reservations").header("Authorization", "Bearer " + clientToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservations_asManager_returns200() throws Exception {
    mockMvc
        .perform(get("/reservations").header("Authorization", "Bearer " + managerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  // ----- PUT /reservation -----

  @Test
  void putReservation_asClient_returns403() throws Exception {
    mockMvc
        .perform(
            put("/reservation")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson()))
        .andExpect(status().isForbidden());
  }

  @Test
  void putReservation_asManager_returns200() throws Exception {
    mockMvc
        .perform(
            put("/reservation")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.projectionId").value(projection.getId().toString()))
        .andExpect(jsonPath("$.seatId").value(seat.getId().toString()));
  }

  @Test
  void putReservation_withExistingId_updatesAndReturns200() throws Exception {
    User manager = userRepository.findByEmail(managerEmail).orElseThrow();
    Reservation reservation = createReservation(manager);

    mockMvc
        .perform(
            put("/reservation?id=" + reservation.getId())
                .header("Authorization", "Bearer " + managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reservation.getId().toString()));
  }

  @Test
  void putReservation_unknownProjection_returns404() throws Exception {
    mockMvc
        .perform(
            put("/reservation")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "projectionId",
                            "00000000-0000-0000-0000-000000000000",
                            "seatId",
                            seat.getId()))))
        .andExpect(status().isNotFound());
  }

  @Test
  void putReservation_unknownSeat_returns404() throws Exception {
    mockMvc
        .perform(
            put("/reservation")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        Map.of(
                            "projectionId",
                            projection.getId(),
                            "seatId",
                            "00000000-0000-0000-0000-000000000000"))))
        .andExpect(status().isNotFound());
  }

  @Test
  void putReservation_invalidToken_returns403() throws Exception {
    mockMvc
        .perform(
            put("/reservation")
                .header("Authorization", "Bearer not-a-valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationJson()))
        .andExpect(status().isForbidden());
  }

  // ----- GET /reservationById/{id} -----

  @Test
  void getReservationById_unknownId_returns404() throws Exception {
    mockMvc
        .perform(
            get("/reservationById/00000000-0000-0000-0000-000000000000")
                .header("Authorization", "Bearer " + clientToken))
        .andExpect(status().isNotFound());
  }

  @Test
  void getReservationById_asOwner_returns200() throws Exception {
    User client = userRepository.findByEmail(clientEmail).orElseThrow();
    Reservation reservation = createReservation(client);

    mockMvc
        .perform(
            get("/reservationById/" + reservation.getId())
                .header("Authorization", "Bearer " + clientToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reservation.getId().toString()))
        .andExpect(jsonPath("$.userId").value(client.getId().toString()));
  }

  @Test
  void getReservationById_asOtherClient_returns403() throws Exception {
    User client = userRepository.findByEmail(clientEmail).orElseThrow();
    Reservation reservation = createReservation(client);

    String otherClientToken = register("it-other-" + SEQUENCE.get() + "@test.com", UserRole.CLIENT);

    mockMvc
        .perform(
            get("/reservationById/" + reservation.getId())
                .header("Authorization", "Bearer " + otherClientToken))
        .andExpect(status().isForbidden());
  }

  @Test
  void getReservationById_asManager_returns200() throws Exception {
    User client = userRepository.findByEmail(clientEmail).orElseThrow();
    Reservation reservation = createReservation(client);

    mockMvc
        .perform(
            get("/reservationById/" + reservation.getId())
                .header("Authorization", "Bearer " + managerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reservation.getId().toString()));
  }

  @Test
  void getReservationById_asEmployee_returns200() throws Exception {
    User client = userRepository.findByEmail(clientEmail).orElseThrow();
    Reservation reservation = createReservation(client);

    String employeeToken =
        register("it-employee-" + SEQUENCE.get() + "@test.com", UserRole.EMPLOYEE);

    mockMvc
        .perform(
            get("/reservationById/" + reservation.getId())
                .header("Authorization", "Bearer " + employeeToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reservation.getId().toString()));
  }

  @Test
  void getReservationById_employeeSeesAllReservations() throws Exception {
    mockMvc
        .perform(get("/reservations").header("Authorization", "Bearer " + managerToken))
        .andExpect(status().isOk());
  }

  @Test
  void seededData_serializesReservationsAsJson() throws Exception {
    mockMvc
        .perform(get("/projections").header("Authorization", "Bearer " + managerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].seatPrice").isNumber());
  }

  @Test
  void ping_isPublic() throws Exception {
    mockMvc.perform(get("/ping")).andExpect(status().isOk());
  }
}
