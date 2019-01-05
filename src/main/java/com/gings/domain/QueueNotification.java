package com.gings.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QueueNotification {
    
    @JsonIgnore
    private int userId;
    private String message;
    // 후에 enum으로 변경
    private String notificationType;
    private LocalDateTime eventAt;
    private boolean confirmed;
    
}
