package com.hei.school.dto.request;

import com.hei.school.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

  @NotBlank(message = "title is required")
  private String title;

  @NotNull(message = "genre is required")
  private Genre genre;

  private String description;

  @NotNull(message = "duration (in minutes) is required")
  @Positive(message = "duration must be positive")
  private Long durationMinutes;
}
