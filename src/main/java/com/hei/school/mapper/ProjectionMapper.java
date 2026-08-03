package com.hei.school.mapper;

import com.hei.school.dto.request.ProjectionRequestDTO;
import com.hei.school.dto.response.ProjectionResponseDTO;
import com.hei.school.entity.Movie;
import com.hei.school.entity.Projection;
import com.hei.school.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class ProjectionMapper {

  public void updateEntity(
      Projection projection, ProjectionRequestDTO dto, Movie movie, Room room) {
    projection.setDatetime(dto.getDatetime());
    projection.setSeatPrice(dto.getSeatPrice());
    projection.setMovie(movie);
    projection.setRoom(room);
  }

  public ProjectionResponseDTO toResponseDTO(Projection projection) {
    Movie movie = projection.getMovie();
    Room room = projection.getRoom();

    return ProjectionResponseDTO.builder()
        .id(projection.getId())
        .datetime(projection.getDatetime())
        .seatPrice(projection.getSeatPrice())
        .movieId(movie != null ? movie.getId() : null)
        .movieTitle(movie != null ? movie.getTitle() : null)
        .roomId(room != null ? room.getId() : null)
        .roomNumber(room != null ? room.getNumber() : null)
        .build();
  }
}
