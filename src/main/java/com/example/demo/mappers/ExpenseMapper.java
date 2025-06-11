package com.example.demo.mappers;

import com.example.demo.DTOs.Expense.Request.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
import com.example.demo.DTOs.Expense.Response.ExpenseResumeDTO;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

        protected UserRepository userRepository;

        @Autowired
        public void setUserRepository(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        // Mapea hacia el DTO completo (por si lo seguís usando)
        @Mapping(source = "trip.id", target = "tripId")
        @Mapping(target = "userIds", expression = "java(mapUsersToIds(entity.getUsers()))")
        public abstract ExpenseResponseDTO toDTO(ExpenseEntity entity);

        public abstract List<ExpenseResponseDTO> toDTOList(List<ExpenseEntity> entities);

        // Mapea hacia el DTO resumido
        @Mapping(source = "trip.id", target = "tripId")
        @Mapping(target = "userIds", expression = "java(mapUsersToIds(entity.getUsers()))")
        public abstract ExpenseResumeDTO toResumeDTO(ExpenseEntity entity);

        public abstract List<ExpenseResumeDTO> toResumeList(List<ExpenseEntity> entities);

        // Creación de entidad desde el create DTO
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "trip", ignore = true)
        @Mapping(target = "users", ignore = true)
        public abstract ExpenseEntity toEntity(ExpenseCreateDTO dto);

        // Actualización de entidad desde update DTO
        public abstract void updateEntityFromDTO(ExpenseUpdateDTO dto, @MappingTarget ExpenseEntity entity);

        // Conversión de Set<UserEntity> a Set<Long>
        protected Set<Long> mapUsersToIds(Set<UserEntity> users) {
                return users.stream()
                        .map(UserEntity::getId)
                        .collect(Collectors.toSet());
        }

        public Set<UserEntity> mapSharedUserIdsToUsers(Set<Long> sharedIds) {
                if (sharedIds == null) return Set.of();
                return sharedIds.stream()
                        .map(id -> userRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id)))
                        .collect(Collectors.toSet());
        }
}