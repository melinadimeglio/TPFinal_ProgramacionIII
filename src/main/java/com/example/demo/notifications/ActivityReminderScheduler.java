package com.example.demo.notifications;

import com.example.demo.notifications.interfaces.ActivityReminder;
import com.example.demo.repositories.ActivityRepository;
import com.example.demo.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityReminderScheduler {
    private final NotificationService notificationService;
    private final ActivityRepository activityRepository;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkUpcomingActivities() {
        log.info("Ejecutando job de recordatorios de actividades...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<ActivityReminder> activities =
                activityRepository.findActivitiesByDateWithUsers(tomorrow);

        for (ActivityReminder row : activities) {
            try {
                notificationService.notifyActivityReminder(
                        row.getUserId(),
                        row.getActivityName(),
                        row.getActivityDate(),
                        row.getActivityId()
                );
            } catch (Exception e) {
                log.error("Error enviando recordatorio de actividad activityId={} userId={}: {}",
                        row.getActivityId(), row.getUserId(), e.getMessage());
            }
        }

        log.info("Recordatorios de actividades enviados para {} registro(s).", activities.size());
    }
}
