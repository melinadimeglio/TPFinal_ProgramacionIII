package com.example.demo.services;

import com.example.demo.DTOs.Notification.Request.NotificationCreateDTO;
import com.example.demo.DTOs.Notification.Response.NotificationResponseDTO;
import com.example.demo.DTOs.Notification.Summary;
import com.example.demo.entities.NotificationEntity;
import com.example.demo.enums.NotificationCategory;
import com.example.demo.enums.NotificationType;
import com.example.demo.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int DEFAULT_PAGE_SIZE = 30;

    private final NotificationRepository notificationRepository;

    public Summary getNotifications(Long userId, NotificationCategory category) {
        var pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);

        List<NotificationEntity> notifications = category != null
                ? notificationRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(userId, category, pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        long unreadCount = notificationRepository.countByUserIdAndReadFalse(userId);

        return Summary.builder()
                .unreadCount(unreadCount)
                .notifications(notifications.stream().map(this::toResponse).collect(Collectors.toList()))
                .build();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "Notificación no encontrada o no pertenece al usuario.");
        }
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public NotificationEntity create(NotificationCreateDTO request) {
        NotificationEntity notification = NotificationEntity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .category(request.getCategory())
                .title(request.getTitle())
                .body(request.getBody())
                .relatedEntityId(request.getRelatedEntityId())
                .relatedEntityType(request.getRelatedEntityType())
                .build();
        return notificationRepository.save(notification);
    }

    public void notifyPaymentConfirmed(Long userId, String activityName, Long reservationId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.PAYMENT_CONFIRMED)
                .category(NotificationCategory.PAYMENTS)
                .title("Pago confirmado")
                .body("Tu pago por \"" + activityName + "\" fue procesado exitosamente.")
                .relatedEntityId(reservationId)
                .relatedEntityType("RESERVATION")
                .build());
    }

    public void notifyPaymentFailed(Long userId, String activityName, Long reservationId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.PAYMENT_FAILED)
                .category(NotificationCategory.PAYMENTS)
                .title("Pago rechazado")
                .body("No pudimos procesar tu pago por \"" + activityName + "\". Revisá tu método de pago.")
                .relatedEntityId(reservationId)
                .relatedEntityType("RESERVATION")
                .build());
    }

    public void notifyReservationCancelled(Long userId, String activityName, Long reservationId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.RESERVATION_CANCELLED)
                .category(NotificationCategory.TRIPS)
                .title("Reserva cancelada")
                .body("Tu reserva para \"" + activityName + "\" fue cancelada por la empresa.")
                .relatedEntityId(reservationId)
                .relatedEntityType("RESERVATION")
                .build());
    }

    public void notifyReservationConfirmed(Long userId, String activityName, Long reservationId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.RESERVATION_CONFIRMED)
                .category(NotificationCategory.TRIPS)
                .title("Reserva confirmada")
                .body("Tu reserva para \"" + activityName + "\" fue confirmada.")
                .relatedEntityId(reservationId)
                .relatedEntityType("RESERVATION")
                .build());
    }

    public void notifySharedExpenseAssigned(Long userId, String expenseName, double amount, Long expenseId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.SHARED_EXPENSE_ASSIGNED)
                .category(NotificationCategory.TRIPS)
                .title("Gasto compartido")
                .body("Te asignaron $" + String.format("%.0f", amount) + " del gasto \"" + expenseName + "\".")
                .relatedEntityId(expenseId)
                .relatedEntityType("EXPENSE")
                .build());
    }

    public void notifyTripInvite(Long userId, String tripName, String inviterName, Long tripId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.TRIP_INVITE)
                .category(NotificationCategory.TRIPS)
                .title("Invitación a viaje")
                .body(inviterName + " te invitó al viaje \"" + tripName + "\".")
                .relatedEntityId(tripId)
                .relatedEntityType("TRIP")
                .build());
    }

    public void notifyFriendRequest(Long userId, String senderName, Long senderId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.FRIEND_REQUEST)
                .category(NotificationCategory.SOCIAL)
                .title("Solicitud de amistad")
                .body(senderName + " quiere conectarse con vos en TravelPlanner.")
                .relatedEntityId(senderId)
                .relatedEntityType("USER")
                .build());
    }

    public void notifyFriendRequestAccepted(Long userId, String accepterName, Long accepterId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.FRIEND_REQUEST_ACCEPTED)
                .category(NotificationCategory.SOCIAL)
                .title("Solicitud aceptada")
                .body(accepterName + " aceptó tu solicitud de amistad.")
                .relatedEntityId(accepterId)
                .relatedEntityType("USER")
                .build());
    }

    public void notifyTripReminder(Long userId, String tripName, int daysUntilTrip, Long tripId) {
        create(NotificationCreateDTO.builder()
                .userId(userId)
                .type(NotificationType.TRIP_REMINDER)
                .category(NotificationCategory.TRIPS)
                .title("Recordatorio de viaje")
                .body("Tu viaje a \"" + tripName + "\" comienza en " + daysUntilTrip + " días. ¡Revisá tu checklist!")
                .relatedEntityId(tripId)
                .relatedEntityType("TRIP")
                .build());
    }

    private NotificationResponseDTO toResponse(NotificationEntity n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .type(n.getType())
                .category(n.getCategory())
                .title(n.getTitle())
                .body(n.getBody())
                .relatedEntityId(n.getRelatedEntityId())
                .relatedEntityType(n.getRelatedEntityType())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .readAt(n.getReadAt())
                .build();
    }

}
