package com.collectivity.entity;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode
public class Member {
    public String           id;
    public String           firstName;
    public String           lastName;
    public LocalDate        birthDate;
    public Gender           gender;
    public String           address;
    public String           profession;
    public String           phoneNumber;
    public String           email;
    public MemberOccupation occupation;
    public String           collectivityId;
    public LocalDate        joinDate;
    public boolean          registrationFeePaid;
    public boolean          membershipDuesPaid;
    public List<Member>     referees;
}