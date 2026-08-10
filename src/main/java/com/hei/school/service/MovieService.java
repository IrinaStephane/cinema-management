package com.hei.school.service;

import com.hei.school.dto.request.MovieRequestDTO;
import com.hei.school.dto.response.MovieResponseDTO;
import com.hei.school.entity.Genre;
import com.hei.school.entity.Movie;
import com.hei.school.exception.ResourceNotFoundException;
import com.hei.school.mapper.MovieMapper;
import com.hei.school.repository.MovieRepository;
import com.hei.school.validator.MovieValidator;
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
  private final MovieValidator movieValidator;

  public List<MovieResponseDTO> findAll(Genre genre) {
    List<Movie> movies =
        genre == null ? movieRepository.findAll() : movieRepository.findByGenre(genre);
    return movies.stream().map(movieMapper::toResponseDTO).collect(Collectors.toList());
  }

  public MovieResponseDTO findById(UUID id) {
    return movieMapper.toResponseDTO(getMovieOrThrow(id));
  }

  @Transactional
  public MovieResponseDTO createOrUpdate(UUID id, MovieRequestDTO dto) {
    movieValidator.validate(dto);
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
