package com.hei.school.entity;

import jakarta.persistence.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.expression.spel.ast.Projection;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  @Column(length = 2000)
  private String description;

  @Column(nullable = false)
  private Long durationMinutes;

  @OneToMany(mappedBy = "movie")
  @Builder.Default
  private List<Projection> projections = new ArrayList<>();

  @Transient
  public Duration getDuration() {
    return durationMinutes == null ? null : Duration.ofMinutes(durationMinutes);
  }

  public void setDuration(Duration duration) {
    this.durationMinutes = duration == null ? null : duration.toMinutes();
  }
}
