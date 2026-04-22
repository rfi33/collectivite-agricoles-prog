package com.collectivity.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class CreateMemberDTO {

    // MemberInformation fields
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;

    // CreateMember-specific fields
    private String collectivityIdentifier;  // ID of the target collectivity
    private List<String> referees;          // List<MemberIdentifier>
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;

    public CreateMemberDTO() {}

}