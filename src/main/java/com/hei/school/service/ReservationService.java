package com.hei.school.service;

import com.hei.school.dto.request.ReservationRequestDTO;
import com.hei.school.dto.response.ReservationResponseDTO;
import com.hei.school.entity.Projection;
import com.hei.school.entity.Reservation;
import com.hei.school.entity.Seat;
import com.hei.school.entity.User;
import com.hei.school.exception.ForbiddenException;
import com.hei.school.exception.ResourceNotFoundException;
import com.hei.school.mapper.ReservationMapper;
import com.hei.school.repository.ProjectionRepository;
import com.hei.school.repository.ReservationRepository;
import com.hei.school.repository.SeatRepository;
import com.hei.school.repository.UserRepository;
import com.hei.school.security.CustomUserDetails;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final ProjectionRepository projectionRepository;
  private final SeatRepository seatRepository;
  private final ReservationMapper reservationMapper;

  public List<ReservationResponseDTO> findAll() {
    return reservationRepository.findAll().stream().map(reservationMapper::toResponseDTO).toList();
  }

  public ReservationResponseDTO findByIdForCurrentUser(UUID id, CustomUserDetails currentUser) {
    Reservation reservation = getReservationOrThrow(id);

    boolean isOwner = reservation.getUser().getId().equals(currentUser.getId());
    boolean isStaff =
        currentUser.getAuthorities().stream()
            .anyMatch(
                authority ->
                    authority.getAuthority().equals("ROLE_MANAGER")
                        || authority.getAuthority().equals("ROLE_EMPLOYEE"));

    if (!isOwner && !isStaff) {
      throw new ForbiddenException("You are not allowed to view this reservation");
    }

    return reservationMapper.toResponseDTO(reservation);
  }

  @Transactional
  public ReservationResponseDTO createOrUpdate(
      UUID id, ReservationRequestDTO dto, CustomUserDetails currentUser) {
    User user = getUserOrThrow(currentUser.getId());
    Projection projection = getProjectionOrThrow(dto.getProjectionId());
    Seat seat = getSeatOrThrow(dto.getSeatId());

    Reservation reservation;
    if (id != null && reservationRepository.existsById(id)) {
      reservation = getReservationOrThrow(id);
      reservation.setProjection(projection);
      reservation.setSeat(seat);
    } else {
      reservation =
          Reservation.builder()
              .user(user)
              .projection(projection)
              .seat(seat)
              .createdAt(Instant.now())
              .build();
    }

    return reservationMapper.toResponseDTO(reservationRepository.save(reservation));
  }

  private Reservation getReservationOrThrow(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
  }

  private User getUserOrThrow(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
  }

  private Projection getProjectionOrThrow(UUID id) {
    return projectionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Projection not found: " + id));
  }

  private Seat getSeatOrThrow(UUID id) {
    return seatRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + id));
  }
}
