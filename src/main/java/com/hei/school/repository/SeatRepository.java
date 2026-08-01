package com.hei.school.repository;

import com.hei.school.entity.Seat;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

  List<Seat> findByRoomId(UUID roomId);
}
