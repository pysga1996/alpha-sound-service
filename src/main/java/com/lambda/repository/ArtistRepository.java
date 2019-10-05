package com.lambda.repository;

import com.lambda.model.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    @Query(value = "SELECT * FROM album WHERE BINARY name=:name", nativeQuery = true)
    Artist findByName(@Param("name") String name);

    Iterable<Artist> findFirst10ByNameContaining(String name);

    Page<Artist> findAllByNameContaining(String name, Pageable pageable);

    Page<Artist> findAllByAlbums_Name(String name, Pageable pageable);
}
