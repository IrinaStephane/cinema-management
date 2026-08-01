package com.hei.school.service;

import com.hei.school.entity.Seat;
import com.hei.school.repository.SeatRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;

  public List<Seat> findAll() {
    return seatRepository.findAll();
  }

  public List<Seat> findByRoom(UUID roomId) {
    return seatRepository.findByRoomId(roomId);
  }
}
