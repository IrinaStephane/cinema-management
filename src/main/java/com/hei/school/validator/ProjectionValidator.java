package com.hei.school.validator;

import com.hei.school.dto.request.ProjectionRequestDTO;
import com.hei.school.exception.ValidationException;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class ProjectionValidator {

  public void validate(ProjectionRequestDTO dto) {
    if (dto.getDatetime() == null) {
      throw new ValidationException("datetime is required");
    }
    if (!dto.getDatetime().isAfter(Instant.now())) {
      throw new ValidationException("datetime must be in the future");
    }
    if (dto.getSeatPrice() == null) {
      throw new ValidationException("seatPrice is required");
    }
    if (dto.getSeatPrice().signum() <= 0) {
      throw new ValidationException("seatPrice must be positive");
    }
    if (dto.getSeatPrice().scale() > 2) {
      throw new ValidationException("seatPrice must have at most 2 decimal digits");
    }
    if (dto.getMovieId() == null) {
      throw new ValidationException("movieId is required");
    }
    if (dto.getRoomId() == null) {
      throw new ValidationException("roomId is required");
    }
  }
}
