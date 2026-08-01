package com.hei.school.service;

import com.hei.school.entity.Movie;
import com.hei.school.repository.MovieRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;
  private final MovieMapper movieMapper;

  public List<MovieResponseDTO> findAll() {
    return movieRepository.findAll().stream()
        .map(movieMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  public MovieResponseDTO findById(UUID id) {
    return movieMapper.toResponseDTO(getMovieOrThrow(id));
  }

  @Transactional
  public MovieResponseDTO createOrUpdate(UUID id, MovieRequestDTO dto) {
    Movie movie;
    if (id != null && movieRepository.existsById(id)) {
      movie = getMovieOrThrow(id);
      movieMapper.updateEntity(movie, dto);
    } else {
      movie = movieMapper.toEntity(dto);
    }
    return movieMapper.toResponseDTO(movieRepository.save(movie));
  }

  private Movie getMovieOrThrow(UUID id) {
    return movieRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
  }
}
