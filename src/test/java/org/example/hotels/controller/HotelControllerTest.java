package org.example.hotels.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hotels.model.Address;
import org.example.hotels.model.Amenity;
import org.example.hotels.model.Contacts;
import org.example.hotels.model.HotelCreateRequest;
import org.example.hotels.model.HotelSummary;
import org.example.hotels.service.HotelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Должен возвращать список отелей")
    void getHotelsTest() throws Exception {
        // Arrange
        when(hotelService.getAllHotels()).thenReturn(List.of(new HotelSummary()));

        // Act & Assert
        mockMvc.perform(get("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(hotelService, times(1)).getAllHotels();
    }

    @Test
    @DisplayName("Должен возвращать отель по ID")
    void getHotelByIdTest() throws Exception {
        // Arrange
        Integer id = 1;
        when(hotelService.getHotelById(id)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/property-view/hotels/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(hotelService, times(1)).getHotelById(id);
    }

    @Test
    @DisplayName("Должен искать отели по параметрам")
    void searchHotelsTest() throws Exception {
        // Arrange
        String name = "DoubleTree";
        String brand = "Hilton";
        String city = "Minsk";
        String county = "Belarus";
        String amenities = "Free WiFi";

        when(hotelService.searchHotels(name, brand, city, county, amenities)).thenReturn(List.of(new HotelSummary()));

        // Act & Assert
        mockMvc.perform(get("/property-view/search")
                        .param("name", name)
                        .param("brand", brand)
                        .param("city", city)
                        .param("county", county)
                        .param("amenities", amenities)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(hotelService, times(1)).searchHotels(name, brand, city, county, amenities);
    }

    @Test
    @DisplayName("Должен создавать новый отель")
    void createHotelTest() throws Exception {
        // Arrange
        Address address = new Address();
        address.setHouseNumber(123);
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setCounty("Test County");
        address.setPostCode(123456);

        Contacts contacts = new Contacts();
        contacts.setPhone("123456789");
        contacts.setEmail("jx3o2@example.com");

        HotelCreateRequest request = new HotelCreateRequest();
        request.setName("Test Hotel");
        request.setDescription("Nice hotel");
        request.setBrand("Test Brand");
        request.setAddress(address);
        request.setContacts(contacts);

        HotelSummary response = new HotelSummary();
        when(hotelService.createNewHotel(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(hotelService, times(1)).createNewHotel(any());
    }

    @Test
    @DisplayName("Должен добавлять удобства к отелю")
    void addHotelAmenitiesTest() throws Exception {
        // Arrange
        Integer hotelId = 1;
        List<Amenity> amenities = List.of(new Amenity().name("Free WiFi"));

        // Act & Assert
        mockMvc.perform(post("/property-view/hotels/{id}/amenities", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amenities)))
                .andExpect(status().isCreated());

        verify(hotelService, times(1)).addAmenitiesToHotel(any(), any());
    }

    @Test
    @DisplayName("Должен возвращать гистограмму отелей по параметру")
    void getHotelsHistogramTest() throws Exception {
        // Arrange
        String param = "city";
        when(hotelService.getHotelsHistogram(param)).thenReturn(new HashMap<>());

        // Act & Assert
        mockMvc.perform(get("/property-view/histogram/{param}", param)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(hotelService, times(1)).getHotelsHistogram(param);
    }
}
