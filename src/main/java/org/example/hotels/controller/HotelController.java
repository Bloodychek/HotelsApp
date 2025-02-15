package org.example.hotels.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.hotels.api.PropertyViewApi;
import org.example.hotels.model.Amenity;
import org.example.hotels.model.HotelCreateRequest;
import org.example.hotels.model.HotelDetailResponse;
import org.example.hotels.model.HotelSummary;
import org.example.hotels.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для работы с отелями
 */

@RestController
@RequiredArgsConstructor
public class HotelController implements PropertyViewApi {
    private final HotelService hotelService;

    // Получить все отели
    @Override
    public ResponseEntity<List<HotelSummary>> getHotels() {
        List<HotelSummary> hotelSummaryList = hotelService.getAllHotels();
        return ResponseEntity.ok(hotelSummaryList);
    }

    // Получить отель по id
    @Override
    public ResponseEntity<HotelDetailResponse> getHotelById(Integer id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    // Поиск отелей
    @Override
    public ResponseEntity<List<HotelSummary>> searchHotels(String name, String brand, String city, String county, String amenities) {
        return ResponseEntity.ok(hotelService.searchHotels(name, brand, city, county, amenities));
    }

    // Создание нового отеля
    @Override
    public ResponseEntity<HotelSummary> createHotel(@Valid @RequestBody HotelCreateRequest hotelCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.createNewHotel(hotelCreateRequest));
    }

    // Добавление удобств к отелю
    @Override
    public ResponseEntity<Void> addHotelAmenities(
            @PathVariable("id") Integer id,
            @Valid @RequestBody List<@Valid Amenity> amenity
    ) {
        hotelService.addAmenitiesToHotel(id, amenity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Получение статистики по отелям
    @Override
    public ResponseEntity<Map<String, Integer>> getHotelsHistogram(String param) {
        return ResponseEntity.ok(hotelService.getHotelsHistogram(param));
    }
}
