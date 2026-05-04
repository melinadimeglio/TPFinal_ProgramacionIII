package com.example.demo.notifications.interfaces;

import java.time.LocalDate;

public interface ActivityReminder {
    Long getActivityId();
    String getActivityName();
    LocalDate getActivityDate();
    Long getUserId();
}
