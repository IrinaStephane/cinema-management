package com.hei.school.mapper;

import com.hei.school.dto.request.MovieRequestDTO;
import com.hei.school.dto.response.MovieResponseDTO;
import com.hei.school.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

  public Movie toEntity(MovieRequestDTO dto) {
    return Movie.builder()
        .title(dto.getTitle())
        .genre(dto.getGenre())
        .description(dto.getDescription())
        .durationMinutes(dto.getDurationMinutes())
        .build();
  }

  public void updateEntity(Movie movie, MovieRequestDTO dto) {
    movie.setTitle(dto.getTitle());
    movie.setGenre(dto.getGenre());
    movie.setDescription(dto.getDescription());
    movie.setDurationMinutes(dto.getDurationMinutes());
  }

  public MovieResponseDTO toResponseDTO(Movie movie) {
    return MovieResponseDTO.builder()
        .id(movie.getId())
        .title(movie.getTitle())
        .genre(movie.getGenre())
        .description(movie.getDescription())
        .durationMinutes(movie.getDurationMinutes())
        .build();
  }
}
