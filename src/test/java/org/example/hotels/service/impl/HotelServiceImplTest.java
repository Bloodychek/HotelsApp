package org.example.hotels.service.impl;

import org.example.hotels.mapper.HotelMapper;
import org.example.hotels.model.Amenity;
import org.example.hotels.model.AmenityEntity;
import org.example.hotels.model.HotelCreateRequest;
import org.example.hotels.model.HotelDetailResponse;
import org.example.hotels.model.HotelEntity;
import org.example.hotels.model.HotelSummary;
import org.example.hotels.repository.AmenityRepository;
import org.example.hotels.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @InjectMocks
    private HotelServiceImpl hotelServiceImpl;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private HotelMapper hotelMapper;

    private HotelEntity hotelEntity;
    private HotelSummary hotelSummary;

    @BeforeEach
    void setUp() {
        hotelEntity = HotelEntity.builder()
                .id(1)
                .name("DoubleTree by Hilton Minsk")
                .description("Luxurious rooms")
                .brand("Hilton")
                .build();

        hotelSummary = new HotelSummary();
        hotelSummary.setId(1);
        hotelSummary.setName("DoubleTree by Hilton Minsk");
        hotelSummary.setDescription("Luxurious rooms");
    }

    @Test
    @DisplayName("Должен вернуть список всех отелей")
    void shouldReturnAllHotelsTest() {
        // Arrange
        when(hotelRepository.findAll()).thenReturn(List.of(hotelEntity));
        when(hotelMapper.hotelEntityToHotelSummary(hotelEntity)).thenReturn(hotelSummary);

        // Act
        List<HotelSummary> result = hotelServiceImpl.getAllHotels();

        // Assert
        assertEquals(1, result.size());
        assertEquals(hotelSummary, result.get(0));

        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, times(1)).hotelEntityToHotelSummary(hotelEntity);
    }

    @Test
    @DisplayName("Должен вернуть отель по ID")
    void shouldReturnHotelByIdTest() {
        // Arrange
        when(hotelRepository.findById(1)).thenReturn(Optional.of(hotelEntity));
        when(hotelMapper.hotelEntityToHotelDetailResponse(hotelEntity)).thenReturn(new HotelDetailResponse());

        // Act
        HotelDetailResponse result = hotelServiceImpl.getHotelById(1);

        // Assert
        assertNotNull(result);

        verify(hotelRepository, times(1)).findById(1);
        verify(hotelMapper, times(1)).hotelEntityToHotelDetailResponse(hotelEntity);
    }

    @Test
    @DisplayName("Должен вернуть результат поиска отелей по параметрам")
    void shouldReturnHotelsSearchTest() {
        // Arrange
        when(hotelRepository.findAll()).thenReturn(List.of(hotelEntity));
        when(hotelMapper.hotelEntityToHotelSummary(hotelEntity)).thenReturn(hotelSummary);

        // Act
        List<HotelSummary> result = hotelServiceImpl.searchHotels("DoubleTree", "Hilton", null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(hotelSummary, result.get(0));

        verify(hotelRepository, times(1)).findAll();
        verify(hotelMapper, times(1)).hotelEntityToHotelSummary(hotelEntity);
    }

    @Test
    @DisplayName("Должен успешно создать новый отель")
    void shouldCreateNewHotelTest() {
        // Arrange
        HotelCreateRequest request = new HotelCreateRequest();
        when(hotelMapper.hotelCreateRequestToHotelEntity(request)).thenReturn(hotelEntity);
        when(hotelRepository.save(hotelEntity)).thenReturn(hotelEntity);
        when(hotelMapper.hotelEntityToHotelSummary(hotelEntity)).thenReturn(hotelSummary);

        // Act
        HotelSummary result = hotelServiceImpl.createNewHotel(request);

        // Assert
        assertNotNull(result);
        assertEquals(hotelSummary, result);

        verify(hotelRepository, times(1)).save(hotelEntity);
    }

    @Test
    @DisplayName("Должен добавлять удобства к отелю")
    void shouldAddAmenitiesToHotelTest() {
        // Arrange
        Integer hotelId = 1;
        HotelEntity hotel = new HotelEntity();
        hotel.setId(hotelId);
        hotel.setName("Test Hotel");
        hotel.setAmenityEntity(new ArrayList<>());

        Amenity amenityDto = new Amenity().name("Free WiFi");
        List<Amenity> amenities = List.of(amenityDto);

        List<AmenityEntity> existingAmenities = new ArrayList<>();
        existingAmenities.add(new AmenityEntity(1, "Free WiFi", new ArrayList<>()));

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(amenityRepository.findByNameIn(List.of("free wifi"))).thenReturn(existingAmenities);
        when(hotelRepository.save(any(HotelEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        hotelServiceImpl.addAmenitiesToHotel(hotelId, amenities);

        // Assert
        assertNotNull(hotel.getAmenityEntity());
        assertEquals(1, hotel.getAmenityEntity().size());
        assertEquals("Free WiFi", hotel.getAmenityEntity().get(0).getName());

        verify(hotelRepository, times(1)).findById(hotelId);
        verify(amenityRepository, times(1)).findByNameIn(List.of("free wifi"));
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    @DisplayName("Должен возвращать статистику отелей по параметру")
    void shouldReturnHotelsHistogramTest() {
        // Arrange
        when(hotelRepository.findAll()).thenReturn(List.of(hotelEntity));
        hotelEntity.setBrand("Hilton");

        // Act
        Map<String, Integer> result = hotelServiceImpl.getHotelsHistogram("brand");

        // Assert
        assertEquals(1, result.get("Hilton"));
    }
}
