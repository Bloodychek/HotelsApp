package org.example.hotels.repository;

import org.example.hotels.model.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с отелями
 */
@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Integer> {
}
