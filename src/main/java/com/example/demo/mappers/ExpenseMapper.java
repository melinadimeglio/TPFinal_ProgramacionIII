package com.example.demo.mappers;

import com.example.demo.DTOs.Expense.ExpenseCreateDTO;
import com.example.demo.DTOs.Expense.ExpenseResponseDTO;
import com.example.demo.DTOs.Expense.ExpenseUpdateDTO;
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

        @Mapping(source = "trip.id", target = "tripId")
        @Mapping(target = "userIds", expression = "java(mapUsersToIds(entity.getUsers()))")
        @Mapping(target = "dividedAmount", ignore = true)
        public abstract ExpenseResponseDTO toDTO(ExpenseEntity entity);

        public abstract List<ExpenseResponseDTO> toDTOList(List<ExpenseEntity> entities);

        @Mapping(target = "users", expression = "java(mapUserIdsToUsers(dto.getUserIds()))")
        @Mapping(target = "id", ignore = true)
        public abstract ExpenseEntity toEntity(ExpenseCreateDTO dto);

        public abstract void updateEntityFromDTO(ExpenseUpdateDTO dto, @MappingTarget ExpenseEntity entity);

        protected Set<Long> mapUsersToIds(Set<UserEntity> users) {
            return users.stream()
                    .map(UserEntity::getId)
                    .collect(Collectors.toSet());
        }

        protected Set<UserEntity> mapUserIdsToUsers(Set<Long> ids) {
            return ids.stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id)))
                    .collect(Collectors.toSet());
        }
}
