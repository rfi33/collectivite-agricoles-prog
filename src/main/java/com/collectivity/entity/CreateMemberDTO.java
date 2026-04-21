package com.collectivity.entity;
import java.time.LocalDate;
import java.util.List;

public class CreateMemberDTO {
    public String firstName;
    public String lastName;
    public LocalDate birthDate;
    public Gender gender;
    public String address;
    public String profession;
    public String phoneNumber;
    public String email;
    public MemberOccupation occupation;

    public Long collectivityIdentifier;
    public List<String> referees;

    public boolean registrationFeePaid;
    public boolean membershipDuesPaid;
}
