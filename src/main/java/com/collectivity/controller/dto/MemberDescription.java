package com.collectivity.controller.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode
public class MemberDescription {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String occupation;
}