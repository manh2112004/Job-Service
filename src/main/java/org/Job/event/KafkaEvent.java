package org.Job.event;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KafkaEvent {
    private String eventId;
    private String eventType;
    private String userId;
    private String referenceId;
    private String referenceType;
    private String title;
    private String message;
    private LocalDateTime createdAt;
}
