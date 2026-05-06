package com.collectivity.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Collectivity {
    private String id;
    private String name;
    private Integer number;
    private String location;
    private CollectivityStructure collectivityStructure;
    private List<Member> members;
    private Boolean federationApproval;

    public boolean hasEnoughMembers() {
        return members != null && members.size() >= 10;
    }

    public List<Member> addMembers(List<Member> newMembers) {
        if (members == null) members = new ArrayList<>();
        for (Member member : newMembers) {
            if (member.getCollectivities() == null) member.setCollectivities(new ArrayList<>());
            member.getCollectivities().add(this);
        }
        members.addAll(newMembers);
        return members;
    }
}