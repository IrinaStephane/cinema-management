package com.hei.school.service;

import com.hei.school.entity.Room;
import com.hei.school.repository.RoomRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eventbridge.model.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  public List<Room> findAll() {
    return roomRepository.findAll();
  }

  public Room findById(UUID id) {
    return roomRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
  }

  public Room save(Room room) {
    return roomRepository.save(room);
  }
}
