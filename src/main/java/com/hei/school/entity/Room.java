package com.hei.school.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String number;

  @Column(nullable = false)
  private Integer capacity;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Seat> seats = new ArrayList<>();

  @OneToMany(mappedBy = "room")
  @Builder.Default
  private List<Projection> projections = new ArrayList<>();
}
