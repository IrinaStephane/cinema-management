package com.hei.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.school.dto.request.ProjectionRequestDTO;
import com.hei.school.dto.response.ProjectionResponseDTO;
import com.hei.school.entity.Movie;
import com.hei.school.entity.Projection;
import com.hei.school.entity.Room;
import com.hei.school.exception.ResourceNotFoundException;
import com.hei.school.mapper.ProjectionMapper;
import com.hei.school.repository.MovieRepository;
import com.hei.school.repository.ProjectionRepository;
import com.hei.school.repository.RoomRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectionServiceTest {

  @Mock ProjectionRepository projectionRepository;
  @Mock MovieRepository movieRepository;
  @Mock RoomRepository roomRepository;
  @Mock ProjectionMapper projectionMapper;
  @InjectMocks ProjectionService projectionService;

  @Test
  void findAll_withoutFilters_returnsAll() {
    Projection projection = Projection.builder().id(UUID.randomUUID()).build();
    when(projectionRepository.findAll()).thenReturn(List.of(projection));
    when(projectionMapper.toResponseDTO(projection))
        .thenReturn(ProjectionResponseDTO.builder().id(projection.getId()).build());

    List<ProjectionResponseDTO> result = projectionService.findAll(null, null);

    assertEquals(1, result.size());
    verify(projectionRepository).findAll();
  }

  @Test
  void findAll_withMovieId_usesFindByMovieId() {
    UUID movieId = UUID.randomUUID();
    when(projectionRepository.findByMovieId(movieId)).thenReturn(List.of());
    assertEquals(0, projectionService.findAll(movieId, null).size());
    verify(projectionRepository, never()).findAll();
  }

  @Test
  void findAll_withMovieAndRoom_usesFindByMovieIdAndRoomId() {
    UUID movieId = UUID.randomUUID();
    UUID roomId = UUID.randomUUID();
    when(projectionRepository.findByMovieIdAndRoomId(movieId, roomId)).thenReturn(List.of());
    assertEquals(0, projectionService.findAll(movieId, roomId).size());
    verify(projectionRepository, never()).findByMovieId(movieId);
    verify(projectionRepository, never()).findByRoomId(roomId);
  }

  @Test
  void findById_missing_throws() {
    UUID id = UUID.randomUUID();
    when(projectionRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> projectionService.findById(id));
  }

  @Test
  void createOrUpdate_createsNewProjection() {
    UUID movieId = UUID.randomUUID();
    UUID roomId = UUID.randomUUID();
    Movie movie = Movie.builder().id(movieId).build();
    Room room = Room.builder().id(roomId).build();
    ProjectionRequestDTO dto =
        new ProjectionRequestDTO(
            Instant.parse("2026-08-01T20:00:00Z"), BigDecimal.valueOf(6000), movieId, roomId);

    when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(projectionRepository.save(any(Projection.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(projectionMapper.toResponseDTO(any(Projection.class)))
        .thenReturn(ProjectionResponseDTO.builder().movieId(movieId).roomId(roomId).build());

    ProjectionResponseDTO result = projectionService.createOrUpdate(null, dto);

    assertEquals(movieId, result.getMovieId());
    verify(projectionMapper).updateEntity(any(Projection.class), any(), any(), any());
  }

  @Test
  void createOrUpdate_withUnknownMovie_throws() {
    UUID movieId = UUID.randomUUID();
    UUID roomId = UUID.randomUUID();
    ProjectionRequestDTO dto =
        new ProjectionRequestDTO(
            Instant.parse("2026-08-01T20:00:00Z"), BigDecimal.valueOf(6000), movieId, roomId);
    when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> projectionService.createOrUpdate(null, dto));
  }

  @Test
  void createOrUpdate_withUnknownRoom_throws() {
    UUID movieId = UUID.randomUUID();
    UUID roomId = UUID.randomUUID();
    ProjectionRequestDTO dto =
        new ProjectionRequestDTO(
            Instant.parse("2026-08-01T20:00:00Z"), BigDecimal.valueOf(6000), movieId, roomId);
    when(movieRepository.findById(movieId)).thenReturn(Optional.of(Movie.builder().build()));
    when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> projectionService.createOrUpdate(null, dto));
  }
}
