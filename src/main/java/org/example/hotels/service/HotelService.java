package org.example.hotels.service;

import org.example.hotels.model.Amenity;
import org.example.hotels.model.HotelCreateRequest;
import org.example.hotels.model.HotelDetailResponse;
import org.example.hotels.model.HotelSummary;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс сервиса для работы с отелями
 */

public interface HotelService {

    // Получить все отели
    List<HotelSummary> getAllHotels();

    // Получить отель по id
    HotelDetailResponse getHotelById(Integer id);

    // Поиск отелей
    List<HotelSummary> searchHotels(String name, String brand, String city,
                                    String county, String amenities);

    // Создание нового отеля
    HotelSummary createNewHotel(HotelCreateRequest hotelCreateRequest);

    // Добавление удобств к отелю
    void addAmenitiesToHotel(Integer hotelId, List<Amenity> amenities);

    // Получение статистики по отелям
    Map<String, Integer> getHotelsHistogram(String param);
}
