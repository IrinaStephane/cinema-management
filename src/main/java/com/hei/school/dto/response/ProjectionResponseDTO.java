package com.hei.school.dto.response;

import java.math.BigDecimal;
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
public class ProjectionResponseDTO {

  private UUID id;
  private Instant datetime;
  private BigDecimal seatPrice;
  private UUID movieId;
  private String movieTitle;
  private UUID roomId;
  private String roomNumber;
}
