package com.hei.school.config;

import com.hei.school.entity.Genre;
import com.hei.school.entity.Movie;
import com.hei.school.entity.Projection;
import com.hei.school.entity.Room;
import com.hei.school.entity.Seat;
import com.hei.school.repository.MovieRepository;
import com.hei.school.repository.ProjectionRepository;
import com.hei.school.repository.RoomRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

  private final RoomRepository roomRepository;
  private final MovieRepository movieRepository;
  private final ProjectionRepository projectionRepository;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (movieRepository.count() > 0 && roomRepository.count() > 0) {
      return;
    }
    List<Room> rooms = seedRooms();
    List<Movie> movies = seedMovies();
    seedProjections(rooms, movies);
  }

  private List<Room> seedRooms() {
    List<Room> rooms = new ArrayList<>();
    rooms.add(buildRoom("Salle 1", 4, 6));
    rooms.add(buildRoom("Salle 2", 5, 4));
    rooms.add(buildRoom("Salle 3", 3, 3));
    return rooms.stream().map(roomRepository::save).toList();
  }

  private Room buildRoom(String number, int rows, int cols) {
    Room room = Room.builder().number(number).capacity(rows * cols).build();
    List<Seat> seats = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      char rowLetter = (char) ('A' + r);
      for (int c = 1; c <= cols; c++) {
        seats.add(Seat.builder().number("" + rowLetter + c).room(room).build());
      }
    }
    room.setSeats(seats);
    return room;
  }

  private List<Movie> seedMovies() {
    List<Movie> movies =
        List.of(
            movie(
                "Inception",
                Genre.ACTION,
                "A thief who steals corporate secrets through dream-sharing technology.",
                148L),
            movie(
                "The Notebook",
                Genre.ROMANCE,
                "A poor yet passionate young man falls in love with a rich young woman.",
                123L),
            movie(
                "Superbad",
                Genre.COMEDY,
                "Two co-dependent high school seniors are forced to deal with separation anxiety.",
                113L),
            movie(
                "The Shawshank Redemption",
                Genre.DRAMA,
                "Two imprisoned men bond over a number of years.",
                142L),
            movie(
                "Interstellar",
                Genre.SCI_FI,
                "A team of explorers travel through a wormhole in space.",
                169L),
            movie(
                "The Lion King",
                Genre.ANIMATION,
                "Lion prince Simba flees his kingdom only to learn the true meaning of"
                    + " responsibility.",
                88L));
    return movies.stream().map(movieRepository::save).toList();
  }

  private Movie movie(String title, Genre genre, String description, Long durationMinutes) {
    return Movie.builder()
        .title(title)
        .genre(genre)
        .description(description)
        .durationMinutes(durationMinutes)
        .build();
  }

  private void seedProjections(List<Room> rooms, List<Movie> movies) {
    Instant now = Instant.now();
    int index = 0;
    for (int i = 0; i < movies.size(); i++) {
      Movie movie = movies.get(i);
      Room room = rooms.get(i % rooms.size());
      for (int p = 0; p < 2; p++) {
        projectionRepository.save(
            Projection.builder()
                .datetime(now.plus(index * 3, ChronoUnit.DAYS))
                .seatPrice(BigDecimal.valueOf(5000 + index * 500L))
                .movie(movie)
                .room(room)
                .build());
        index++;
      }
    }
  }
}
