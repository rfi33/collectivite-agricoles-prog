package com.collectivity.dto.request;

import com.collectivity.entity.Gender;
import com.collectivity.entity.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

public class CreateMemberRequest {
    public String           firstName;
    public String           lastName;
    public LocalDate        birthDate;
    public Gender           gender;
    public String           address;
    public String           profession;
    public String           phoneNumber;
    public String           email;
    public MemberOccupation occupation;
    public String           collectivityIdentifier;
    public List<RefereeInfoRequest> referees;
    public Boolean          registrationFeePaid;
    public Boolean          membershipDuesPaid;
}