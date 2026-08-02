package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.hei.school.entity.*;
import com.hei.school.exception.ForbiddenException;
import com.hei.school.mapper.ReservationMapper;
import com.hei.school.repository.*;
import com.hei.school.security.CustomUserDetails;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {

  private ReservationRepository reservationRepository;
  private ReservationService service;
  private User owner;
  private Reservation reservation;

  @BeforeEach
  void setUp() {
    reservationRepository = mock(ReservationRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    ProjectionRepository projectionRepository = mock(ProjectionRepository.class);
    SeatRepository seatRepository = mock(SeatRepository.class);

    service =
        new ReservationService(
            reservationRepository,
            userRepository,
            projectionRepository,
            seatRepository,
            new ReservationMapper());

    owner = User.builder().id(UUID.randomUUID()).role(UserRole.CLIENT).build();
    reservation =
        Reservation.builder()
            .id(UUID.randomUUID())
            .user(owner)
            .projection(Projection.builder().id(UUID.randomUUID()).build())
            .seat(Seat.builder().id(UUID.randomUUID()).build())
            .createdAt(Instant.now())
            .build();

    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
  }

  @Test
  void shouldAllowOwnerToViewTheirReservation() {
    var ownerDetails =
        new CustomUserDetails(User.builder().id(owner.getId()).role(UserRole.CLIENT).build());

    var result = service.findByIdForCurrentUser(reservation.getId(), ownerDetails);

    assertThat(result.getId()).isEqualTo(reservation.getId());
  }

  @Test
  void shouldForbidNonOwnerClientFromViewingReservation() {
    var otherClient =
        new CustomUserDetails(User.builder().id(UUID.randomUUID()).role(UserRole.CLIENT).build());

    assertThatThrownBy(() -> service.findByIdForCurrentUser(reservation.getId(), otherClient))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void shouldAllowManagerToViewAnyReservation() {
    var manager =
        new CustomUserDetails(User.builder().id(UUID.randomUUID()).role(UserRole.MANAGER).build());

    var result = service.findByIdForCurrentUser(reservation.getId(), manager);

    assertThat(result.getId()).isEqualTo(reservation.getId());
  }
}
