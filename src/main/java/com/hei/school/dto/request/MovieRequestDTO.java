package com.hei.school.dto.request;

import com.hei.school.entity.Genre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

  @NotBlank(message = "title is required")
  @Size(max = 200, message = "title must be at most 200 characters")
  private String title;

  @NotNull(message = "genre is required")
  private Genre genre;

  @Size(max = 2000, message = "description must be at most 2000 characters")
  private String description;

  @NotNull(message = "duration (in minutes) is required")
  @Positive(message = "duration must be positive")
  @Max(value = 600, message = "duration must be at most 600 minutes")
  private Long durationMinutes;
}
