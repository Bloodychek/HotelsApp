package org.example.hotels.repository;

import org.example.hotels.model.AmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с удобствами
 */

@Repository
public interface AmenityRepository extends JpaRepository<AmenityEntity, Long> {
    List<AmenityEntity> findByNameIn(List<String> names);
}
