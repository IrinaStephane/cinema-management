package com.hei.school.endpoint.rest.controller;

import com.hei.school.dto.request.MovieRequestDTO;
import com.hei.school.dto.response.MovieResponseDTO;
import com.hei.school.service.MovieService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

  private final MovieService movieService;

  @GetMapping
  public ResponseEntity<List<MovieResponseDTO>> getAll() {
    return ResponseEntity.ok(movieService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<MovieResponseDTO> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(movieService.findById(id));
  }

  @PutMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<MovieResponseDTO> createOrUpdate(
      @RequestParam(required = false) UUID id, @RequestBody MovieRequestDTO dto) {
    MovieResponseDTO result = movieService.createOrUpdate(id, dto);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }
}
