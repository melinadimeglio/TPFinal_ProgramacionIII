package com.example.demo.controllers;

import com.example.demo.notifications.TripReminderScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev/test")
@RequiredArgsConstructor
public class NotificationTestController {
    private final TripReminderScheduler tripReminderScheduler;

    @PostMapping("/budgets")
    public ResponseEntity<String> testBudgets() {
        tripReminderScheduler.checkBudgets();
        return ResponseEntity.ok("Budget check ejecutado");
    }
}
