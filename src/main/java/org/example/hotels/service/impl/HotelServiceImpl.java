package org.example.hotels.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hotels.mapper.HotelMapper;
import org.example.hotels.model.Amenity;
import org.example.hotels.model.AmenityEntity;
import org.example.hotels.model.HotelCreateRequest;
import org.example.hotels.model.HotelDetailResponse;
import org.example.hotels.model.HotelEntity;
import org.example.hotels.model.HotelSummary;
import org.example.hotels.repository.AmenityRepository;
import org.example.hotels.repository.HotelRepository;
import org.example.hotels.service.HotelService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс реализации интерфейса {@link HotelService}
 */

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    // Реализация метода для получения всех отелей
    @Override
    public List<HotelSummary> getAllHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(hotelMapper::hotelEntityToHotelSummary)
                .toList();
    }

    // Реализация метода для получения отеля по его ID
    @Override
    public HotelDetailResponse getHotelById(Integer id) {
        return hotelRepository.findById(id)
                .stream()
                .map(hotelMapper::hotelEntityToHotelDetailResponse)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    // Реализация метода для поиска отелей
    @Override
    public List<HotelSummary> searchHotels(String name, String brand, String city,
                                           String county, String amenities) {
        List<HotelEntity> hotels = hotelRepository.findAll();

        hotels = hotels.stream()
                .filter(hotel -> {
                    boolean match = name == null || hotel.getName().contains(name);
                    if (brand != null && !hotel.getBrand().contains(brand)) {
                        match = false;
                    }
                    if (city != null && !hotel.getAddressEntity().getCity().contains(city)) {
                        match = false;
                    }
                    if (county != null && !hotel.getAddressEntity().getCounty().contains(county)) {
                        match = false;
                    }
                    if (amenities != null && hotel.getAmenityEntity().stream()
                            .noneMatch(amenity -> amenity.getName().contains(amenities))) {
                        match = false;
                    }
                    return match;
                })
                .toList();

        return hotels.stream()
                .map(hotelMapper::hotelEntityToHotelSummary)
                .collect(Collectors.toList());
    }

    // Реализация метода для создания нового отеля
    @Override
    public HotelSummary createNewHotel(HotelCreateRequest hotelCreateRequest) {
        HotelEntity hotelEntity = hotelMapper.hotelCreateRequestToHotelEntity(hotelCreateRequest);

        if (hotelEntity.getAddressEntity() != null) {
            hotelEntity.getAddressEntity().setHotelEntity(hotelEntity);
        }

        if (hotelEntity.getArrivalTimeEntity() != null) {
            hotelEntity.getArrivalTimeEntity().setHotelEntity(hotelEntity);
        }

        if (hotelEntity.getContactsEntity() != null) {
            hotelEntity.getContactsEntity().setHotelEntity(hotelEntity);
        }

        hotelEntity = hotelRepository.save(hotelEntity);
        return hotelMapper.hotelEntityToHotelSummary(hotelEntity);
    }

    // Реализация метода для добавления удобств к отелю
    @Override
    public void addAmenitiesToHotel(Integer hotelId, List<Amenity> amenities) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));

        List<String> names = amenities.stream()
                .map(a -> a.getName().trim().toLowerCase().replaceAll("\\s+", " "))
                .distinct()
                .toList();

        List<AmenityEntity> existingAmenities = amenityRepository.findByNameIn(names);
        Set<String> existingNames = existingAmenities.stream()
                .map(AmenityEntity::getName)
                .collect(Collectors.toSet());

        List<AmenityEntity> newAmenities = names.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> new AmenityEntity(null, name, new ArrayList<>()))
                .toList();

        existingAmenities.addAll(amenityRepository.saveAll(newAmenities));

        hotel.getAmenityEntity().addAll(existingAmenities);
        hotelRepository.save(hotel);
    }

    // Реализация метода для получения статистики по отелям
    @Override
    public Map<String, Integer> getHotelsHistogram(String param) {
        List<HotelEntity> hotels = hotelRepository.findAll();

        return switch (param.toLowerCase()) {
            case "brand" -> hotels.stream()
                    .collect(Collectors.toMap(HotelEntity::getBrand, h -> 1, Integer::sum));
            case "city" -> hotels.stream()
                    .collect(Collectors.toMap(h -> h.getAddressEntity().getCity(), h -> 1, Integer::sum));
            case "county" -> hotels.stream()
                    .collect(Collectors.toMap(h -> h.getAddressEntity().getCounty(), h -> 1, Integer::sum));
            case "amenities" -> hotels.stream()
                    .flatMap(h -> h.getAmenityEntity().stream())
                    .collect(Collectors.toMap(AmenityEntity::getName, a -> 1, Integer::sum));
            default -> throw new IllegalArgumentException("Invalid parameter: " + param);
        };
    }
}
