package com.example.demo.notifications;

import com.example.demo.entities.ExpenseEntity;
import com.example.demo.enums.NotificationType;
import com.example.demo.notifications.interfaces.TripBudget;
import com.example.demo.notifications.interfaces.TripReminder;
import com.example.demo.repositories.ExpenseRepository;
import com.example.demo.repositories.TripRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.services.EmailService;
import com.example.demo.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripReminderScheduler {
    private final NotificationService notificationService;
    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendTripReminders() {
        log.info("Ejecutando job de recordatorios de viaje...");
        for (int days : List.of(3, 1)) {
            sendRemindersForDate(LocalDate.now().plusDays(days), days);
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void checkBudgets() {
        log.info("Ejecutando job de control de presupuestos...");

        List<TripBudget> rows = tripRepository.findActiveTripsWithBudgetAndUsers();

        Map<Long, List<TripBudget>> byTrip = rows.stream()
                .collect(Collectors.groupingBy(TripBudget::getTripId));

        for (Map.Entry<Long, List<TripBudget>> entry : byTrip.entrySet()) {
            Long tripId = entry.getKey();
            List<TripBudget> tripRows = entry.getValue();

            String tripName = tripRows.getFirst().getTripName();
            double estimated = tripRows.getFirst().getEstimatedBudget();

            List<ExpenseEntity> expenses = expenseRepository.findByTripIdAndActiveTrue(tripId);
            double total = expenses.stream().mapToDouble(ExpenseEntity::getAmount).sum();

            if (total <= estimated * 0.5) continue;

            boolean exceeded = total > estimated;
            NotificationType tipoNotif = exceeded
                    ? NotificationType.BUDGET_EXCEEDED
                    : NotificationType.BUDGET_HALF_SPENT;

            for (TripBudget row : tripRows) {
                try {
                    log.info("Procesando userId={} tripId={}", row.getUserId(), tripId);

                    boolean yaNotificado = notificationService.wasNotifiedToday(
                            row.getUserId(), tipoNotif, tripId);
                    log.info("wasNotifiedToday={}", yaNotificado);

                    if (yaNotificado) continue;

                    if (exceeded) {
                        notificationService.notifyBudgetExceeded(row.getUserId(), tripName, tripId);
                    } else {
                        notificationService.notifyBudgetHalfSpent(row.getUserId(), tripName, tripId);
                    }
                    log.info("Notificacion interna creada para userId={}", row.getUserId());

                    userRepository.findById(row.getUserId()).ifPresent(user -> {
                        CredentialEntity credential = user.getCredential();
                        if (credential != null && credential.getEmail() != null) {
                            emailService.sendBudgetAlert(
                                    credential.getEmail(),
                                    user.getUsername(),
                                    tripName,
                                    estimated,
                                    total
                            );
                        }
                    });

                } catch (Exception e) {
                    log.error("Error enviando notificación de presupuesto tripId={} userId={}: {}",
                            tripId, row.getUserId(), e.getMessage());
                }
            }
        }

        log.info("Job de presupuestos finalizado.");
    }

    private void sendRemindersForDate(LocalDate startDate, int daysUntilTrip) {
        List<TripReminder> trips = tripRepository.findActiveTripsWithUsersByStartDate(startDate);

        for (TripReminder row : trips) {
            try {
                notificationService.notifyTripReminder(
                        row.getUserId(), row.getDestination(), daysUntilTrip, row.getTripId());

                if (daysUntilTrip == 3) {
                    userRepository.findById(row.getUserId()).ifPresent(user -> {
                        CredentialEntity credential = user.getCredential();
                        if (credential != null && credential.getEmail() != null) {
                            emailService.sendTripReminder(
                                    credential.getEmail(),
                                    user.getUsername(),
                                    row.getDestination(),
                                    startDate
                            );
                        }
                    });
                }

            } catch (Exception e) {
                log.error("Error enviando recordatorio para tripId={} userId={}: {}",
                        row.getTripId(), row.getUserId(), e.getMessage());
            }
        }

        log.info("Recordatorios enviados para {} viaje(s) que empiezan en {} día(s).", trips.size(), daysUntilTrip);
    }
}
