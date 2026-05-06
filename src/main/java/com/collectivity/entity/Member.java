package com.collectivity.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Member {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private List<Member> referees;
    private List<Collectivity> collectivities;
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;

    public boolean refereesAreEligible() {
        if (referees == null || referees.size() < 2) return false;
        if (collectivities == null || collectivities.isEmpty()) return false;
        long eligible = referees.stream()
                .filter(r -> r.getOccupation() != MemberOccupation.JUNIOR
                        && r.getCollectivities() != null
                        && r.getCollectivities().stream()
                                .anyMatch(rc -> collectivities.contains(rc)))
                .count();
        return eligible >= 2;
    }

    public List<Collectivity> addCollectivity(Collectivity collectivity) {
        if (collectivities == null) collectivities = new ArrayList<>();
        if (!collectivities.contains(collectivity)) collectivities.add(collectivity);
        return collectivities;
    }

    public List<Member> addReferees(List<Member> refs) {
        if (referees == null) referees = new ArrayList<>();
        referees.addAll(refs);
        return referees;
    }
}