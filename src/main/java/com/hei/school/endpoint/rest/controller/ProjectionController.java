package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.ProjectionRequestDTO;
import com.hei.school.dto.response.ProjectionResponseDTO;
import com.hei.school.service.ProjectionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProjectionController {

  private final ProjectionService projectionService;

  @GetMapping("/projections")
  public ResponseEntity<List<ProjectionResponseDTO>> getAll(
      @RequestParam(required = false) UUID movieId, @RequestParam(required = false) UUID roomId) {
    return ResponseEntity.ok(projectionService.findAll(movieId, roomId));
  }

  @GetMapping("/projections/{id}")
  public ResponseEntity<ProjectionResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(projectionService.findById(id));
  }

  @PutMapping("/projection")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<ProjectionResponseDTO> createOrUpdate(
      @RequestParam(required = false) UUID id, @Valid @RequestBody ProjectionRequestDTO dto) {
    return ResponseEntity.ok(projectionService.createOrUpdate(id, dto));
  }
}
