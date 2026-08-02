package com.hei.school.mapper;

import com.hei.school.dto.response.ReservationResponseDTO;
import com.hei.school.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

  public ReservationResponseDTO toResponseDTO(Reservation reservation) {
    return ReservationResponseDTO.builder()
        .id(reservation.getId())
        .createdAt(reservation.getCreatedAt())
        .userId(reservation.getUser().getId())
        .projectionId(reservation.getProjection().getId())
        .seatId(reservation.getSeat().getId())
        .build();
  }
}
