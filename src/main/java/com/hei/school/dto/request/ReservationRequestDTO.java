package com.hei.school.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

  @NotNull(message = "projectionId is required")
  private UUID projectionId;

  @NotNull(message = "seatId is required")
  private UUID seatId;
}
