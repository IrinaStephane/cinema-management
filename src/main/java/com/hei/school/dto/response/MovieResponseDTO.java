package com.hei.school.dto.response;

import com.hei.school.entity.Genre;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDTO {

  private UUID id;
  private String title;
  private Genre genre;
  private String description;
  private Long durationMinutes;
}
