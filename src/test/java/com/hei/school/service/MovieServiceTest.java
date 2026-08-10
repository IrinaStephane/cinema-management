package com.hei.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.school.dto.request.MovieRequestDTO;
import com.hei.school.dto.response.MovieResponseDTO;
import com.hei.school.entity.Genre;
import com.hei.school.entity.Movie;
import com.hei.school.exception.ResourceNotFoundException;
import com.hei.school.mapper.MovieMapper;
import com.hei.school.repository.MovieRepository;
import com.hei.school.validator.MovieValidator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

  @Mock MovieRepository movieRepository;
  @Mock MovieMapper movieMapper;
  @Mock MovieValidator movieValidator;
  @InjectMocks MovieService movieService;

  @Test
  void findAll_withoutGenre_returnsAll() {
    Movie movie = Movie.builder().id(UUID.randomUUID()).title("Inception").build();
    when(movieRepository.findAll()).thenReturn(List.of(movie));
    when(movieMapper.toResponseDTO(movie))
        .thenReturn(MovieResponseDTO.builder().id(movie.getId()).title("Inception").build());

    List<MovieResponseDTO> result = movieService.findAll(null);

    assertEquals(1, result.size());
    assertEquals("Inception", result.get(0).getTitle());
    verify(movieRepository).findAll();
    verify(movieRepository, never()).findByGenre(any());
  }

  @Test
  void findAll_withGenre_usesFindByGenre() {
    Movie movie = Movie.builder().id(UUID.randomUUID()).title("Inception").build();
    when(movieRepository.findByGenre(Genre.ACTION)).thenReturn(List.of(movie));
    when(movieMapper.toResponseDTO(movie))
        .thenReturn(MovieResponseDTO.builder().title("Inception").build());

    List<MovieResponseDTO> result = movieService.findAll(Genre.ACTION);

    assertEquals(1, result.size());
    verify(movieRepository).findByGenre(Genre.ACTION);
    verify(movieRepository, never()).findAll();
  }

  @Test
  void findById_existing_returnsDto() {
    Movie movie = Movie.builder().id(UUID.randomUUID()).title("Inception").build();
    when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
    when(movieMapper.toResponseDTO(movie))
        .thenReturn(MovieResponseDTO.builder().id(movie.getId()).title("Inception").build());

    MovieResponseDTO result = movieService.findById(movie.getId());

    assertEquals("Inception", result.getTitle());
  }

  @Test
  void findById_missing_throws() {
    UUID id = UUID.randomUUID();
    when(movieRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> movieService.findById(id));
  }

  @Test
  void createOrUpdate_withoutExistingId_createsNewMovie() {
    MovieRequestDTO dto = new MovieRequestDTO("Inception", Genre.ACTION, "desc", 148L);
    Movie movie = Movie.builder().title("Inception").build();
    when(movieMapper.toEntity(dto)).thenReturn(movie);
    when(movieRepository.save(movie)).thenReturn(movie);
    when(movieMapper.toResponseDTO(movie))
        .thenReturn(MovieResponseDTO.builder().title("Inception").build());

    MovieResponseDTO result = movieService.createOrUpdate(null, dto);

    assertEquals("Inception", result.getTitle());
    verify(movieMapper, never()).updateEntity(any(), any());
  }

  @Test
  void createOrUpdate_withExistingId_updatesMovie() {
    UUID id = UUID.randomUUID();
    MovieRequestDTO dto = new MovieRequestDTO("Inception 2", Genre.ACTION, "desc", 150L);
    Movie existing = Movie.builder().id(id).title("Inception").build();
    when(movieRepository.existsById(id)).thenReturn(true);
    when(movieRepository.findById(id)).thenReturn(Optional.of(existing));
    when(movieRepository.save(existing)).thenReturn(existing);
    when(movieMapper.toResponseDTO(existing))
        .thenReturn(MovieResponseDTO.builder().id(id).title("Inception 2").build());

    MovieResponseDTO result = movieService.createOrUpdate(id, dto);

    assertEquals(id, result.getId());
    verify(movieMapper).updateEntity(existing, dto);
    verify(movieMapper, never()).toEntity(any());
  }
}
