package com.hei.school.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectionRequestDTO {

  @NotNull(message = "datetime is required")
  private Instant datetime;

  @NotNull(message = "seatPrice is required")
  @Positive(message = "seatPrice must be positive")
  private BigDecimal seatPrice;

  @NotNull(message = "movieId is required")
  private UUID movieId;

  @NotNull(message = "roomId is required")
  private UUID roomId;
}
