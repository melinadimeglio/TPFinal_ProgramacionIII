package com.example.demo.services;

import com.example.demo.DTOs.Dashboard.DashboardDTO;
import com.example.demo.entities.TripEntity;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.repositories.ExpenseRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.security.repositories.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;
    private final ActivityRepository activityRepository;
    private final CredentialRepository credentialRepository;

    public DashboardDTO getDashboard (String email){

        Long userId = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getUser()
                .getId();

        int totalViajes = (int)tripRepository.countByUsersIdAndActiveTrue(userId);

        List<String> destinos = tripRepository.findByUsersIdAndActiveTrue(userId)
                .stream()
                .map(TripEntity::getDestination)
                .distinct()
                .collect(Collectors.toList());

        int totalActividades = (int) activityRepository.countByUsers_Id(userId);

        Map<String, Double> gastosPorCategoria = expenseRepository.findByUserId(userId)
                .stream()
                .filter(e -> e.getCategory() != null && e.isActive())
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().toString(),
                        Collectors.summingDouble(e -> e.getAmount())
                ));

        Map<String, Double> gastosPorMes = expenseRepository.findByUserId(userId)
                .stream()
                .filter(e -> e.getDate()!=null && e.isActive())
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getYear() + "-" + String.format("%02d", e.getDate().getMonthValue()),
                        Collectors.summingDouble(e -> e.getAmount())
                ));

        return DashboardDTO.builder()
                .totalViajes(totalViajes)
                .destinosVisitados(destinos)
                .totalActividades(totalActividades)
                .gastosPorCategoria(gastosPorCategoria)
                .gastosPorMes(gastosPorMes)
                .build();
    }

}
