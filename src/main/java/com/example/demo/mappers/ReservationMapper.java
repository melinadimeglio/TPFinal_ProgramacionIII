package com.example.demo.mappers;

import com.example.demo.DTOs.Reservation.Request.ReservationCreateDTO;
import com.example.demo.DTOs.Reservation.Response.ReservationResponseDTO;
import com.example.demo.entities.ReservationEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.ActivityEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "activity", source = "activity")
    @Mapping(target = "reservationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "paid", constant = "true") // Por ahora siempre pagado (luego lo veremos para Mercado Pago)
    @Mapping(target = "status", constant = "ACTIVE")
    ReservationEntity toEntity(ReservationCreateDTO dto, UserEntity user, ActivityEntity activity);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "activity.id", target = "activityId")
    ReservationResponseDTO toDTO(ReservationEntity entity);
}
