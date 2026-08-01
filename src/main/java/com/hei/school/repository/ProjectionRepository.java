package com.hei.school.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.Projection;

public interface ProjectionRepository extends JpaRepository<Projection, UUID> {

  List<Projection> findByMovieId(UUID movieId);

  List<Projection> findByRoomId(UUID roomId);
}
