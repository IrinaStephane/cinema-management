package com.hei.school.repository;

import com.hei.school.entity.Projection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectionRepository extends JpaRepository<Projection, UUID> {

  List<Projection> findByMovieId(UUID movieId);

  List<Projection> findByRoomId(UUID roomId);

  List<Projection> findByMovieIdAndRoomId(UUID movieId, UUID roomId);
}
