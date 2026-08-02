package com.hei.school.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
  private UUID id;
  private Instant createdAt;
  private UUID userId;
  private UUID projectionId;
  private UUID seatId;
}
