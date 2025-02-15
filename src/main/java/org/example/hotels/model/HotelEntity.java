package org.example.hotels.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Класс-сущность, представляющий собой отель
 */

@Entity
@Table(name = "hotels")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "brand")
    private String brand;

    @OneToOne(mappedBy = "hotelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private AddressEntity addressEntity;

    @OneToOne(mappedBy = "hotelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContactsEntity contactsEntity;

    @OneToOne(mappedBy = "hotelEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrivalTimeEntity arrivalTimeEntity;

    @ManyToMany
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<AmenityEntity> amenityEntity;
}