package com.framecheckmate.testservice.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSaveRequest {
    private String email;
    private NotificationType type;
}
