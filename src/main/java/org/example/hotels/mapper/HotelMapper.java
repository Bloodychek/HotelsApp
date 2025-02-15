package org.example.hotels.mapper;


import org.example.hotels.model.AddressEntity;
import org.example.hotels.model.HotelCreateRequest;
import org.example.hotels.model.HotelDetailResponse;
import org.example.hotels.model.HotelEntity;
import org.example.hotels.model.HotelSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер-интерфейс, который преобразует сущности в dto и наоборот
 */

@Mapper(componentModel = "spring")
public interface HotelMapper {

    // Преобразование HotelEntity в HotelSummary
    @Mapping(target = "address", source = "addressEntity")
    @Mapping(target = "phone", source = "contactsEntity.phone")
    HotelSummary hotelEntityToHotelSummary(HotelEntity hotel);

    default String map(AddressEntity addressEntity) {
        if (addressEntity == null) {
            return null;
        }
        return String.format("%s %s, %s, %s, %s",
                addressEntity.getHouseNumber(),
                addressEntity.getStreet(),
                addressEntity.getCity(),
                addressEntity.getPostCode(),
                addressEntity.getCounty());
    }

    // Преобразование HotelEntity в HotelDetailResponse
    @Mapping(target = "address", source = "addressEntity")
    @Mapping(target = "arrivalTime", source = "arrivalTimeEntity")
    @Mapping(target = "contacts", source = "contactsEntity")
    @Mapping(target = "amenities", source = "amenityEntity")
    HotelDetailResponse hotelEntityToHotelDetailResponse(HotelEntity hotel);

    // Преобразование HotelCreateRequest в HotelEntity
    @Mapping(target = "addressEntity", source = "address")
    @Mapping(target = "arrivalTimeEntity", source = "arrivalTime")
    @Mapping(target = "contactsEntity", source = "contacts")
    HotelEntity hotelCreateRequestToHotelEntity(HotelCreateRequest request);
}
