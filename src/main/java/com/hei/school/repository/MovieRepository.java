package com.hei.school.repository;

import com.hei.school.entity.Genre;
import com.hei.school.entity.Movie;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

  List<Movie> findByGenre(Genre genre);
}
