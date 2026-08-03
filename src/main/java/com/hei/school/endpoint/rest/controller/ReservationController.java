package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.ReservationRequestDTO;
import com.hei.school.dto.response.ReservationResponseDTO;
import com.hei.school.security.CustomUserDetails;
import com.hei.school.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping("/reservations")
  @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
  public ResponseEntity<List<ReservationResponseDTO>> getAll() {
    return ResponseEntity.ok(reservationService.findAll());
  }

  @GetMapping("/reservationById/{id}")
  public ResponseEntity<ReservationResponseDTO> getById(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails currentUser) {
    return ResponseEntity.ok(reservationService.findByIdForCurrentUser(id, currentUser));
  }

  @PutMapping("/reservation")
  @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
  public ResponseEntity<ReservationResponseDTO> createOrUpdate(
      @RequestParam(required = false) UUID id,
      @Valid @RequestBody ReservationRequestDTO dto,
      @AuthenticationPrincipal CustomUserDetails currentUser) {
    return ResponseEntity.ok(reservationService.createOrUpdate(id, dto, currentUser));
  }
}
