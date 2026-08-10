package com.hei.school.validator;

import com.hei.school.dto.request.MovieRequestDTO;
import com.hei.school.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MovieValidator {

  private static final int MIN_DURATION_MINUTES = 1;
  private static final int MAX_DURATION_MINUTES = 600;

  public void validate(MovieRequestDTO dto) {
    if (dto.getTitle() == null || dto.getTitle().isBlank()) {
      throw new ValidationException("title is required");
    }
    if (dto.getTitle().length() > 200) {
      throw new ValidationException("title must be at most 200 characters");
    }
    if (dto.getGenre() == null) {
      throw new ValidationException("genre is required");
    }
    if (dto.getDescription() != null && dto.getDescription().length() > 2000) {
      throw new ValidationException("description must be at most 2000 characters");
    }
    if (dto.getDurationMinutes() == null) {
      throw new ValidationException("duration (in minutes) is required");
    }
    if (dto.getDurationMinutes() < MIN_DURATION_MINUTES
        || dto.getDurationMinutes() > MAX_DURATION_MINUTES) {
      throw new ValidationException(
          "duration must be between "
              + MIN_DURATION_MINUTES
              + " and "
              + MAX_DURATION_MINUTES
              + " minutes");
    }
  }
}
