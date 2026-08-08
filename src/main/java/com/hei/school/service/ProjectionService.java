package com.hei.school.service;

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
import com.hei.school.validator.ProjectionValidator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectionService {

  private final ProjectionRepository projectionRepository;
  private final MovieRepository movieRepository;
  private final RoomRepository roomRepository;
  private final ProjectionMapper projectionMapper;
  private final ProjectionValidator projectionValidator;

  public List<ProjectionResponseDTO> findAll(UUID movieId, UUID roomId) {
    List<Projection> projections;
    if (movieId != null && roomId != null) {
      projections = projectionRepository.findByMovieIdAndRoomId(movieId, roomId);
    } else if (movieId != null) {
      projections = projectionRepository.findByMovieId(movieId);
    } else if (roomId != null) {
      projections = projectionRepository.findByRoomId(roomId);
    } else {
      projections = projectionRepository.findAll();
    }
    return projections.stream().map(projectionMapper::toResponseDTO).collect(Collectors.toList());
  }

  public ProjectionResponseDTO findById(UUID id) {
    return projectionMapper.toResponseDTO(getProjectionOrThrow(id));
  }

  @Transactional
  public ProjectionResponseDTO createOrUpdate(UUID id, ProjectionRequestDTO dto) {
    projectionValidator.validate(dto);
    Movie movie =
        movieRepository
            .findById(dto.getMovieId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Movie not found: " + dto.getMovieId()));
    Room room =
        roomRepository
            .findById(dto.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + dto.getRoomId()));

    Projection projection;
    if (id != null && projectionRepository.existsById(id)) {
      projection = getProjectionOrThrow(id);
    } else {
      projection = new Projection();
    }

    projectionMapper.updateEntity(projection, dto, movie, room);

    return projectionMapper.toResponseDTO(projectionRepository.save(projection));
  }

  private Projection getProjectionOrThrow(UUID id) {
    return projectionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Projection not found: " + id));
  }
}
