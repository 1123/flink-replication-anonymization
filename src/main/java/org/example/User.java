package org.example;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User {
    private Long timestamp;
    private String name;
    private UUID uuid;
    private Integer age;
}

