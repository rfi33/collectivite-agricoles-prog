package com.collectivity.dto.response;

import com.collectivity.entity.Gender;
import com.collectivity.entity.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

public class MemberResponse {
    public String               id;
    public String               firstName;
    public String               lastName;
    public LocalDate            birthDate;
    public Gender               gender;
    public String               address;
    public String               profession;
    public String               phoneNumber;
    public String               email;
    public MemberOccupation     occupation;
    public List<MemberResponse> referees;
}