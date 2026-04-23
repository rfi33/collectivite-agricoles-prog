package com.collectivity.dto.response;

import com.collectivity.entity.Specialization;

import java.util.List;

public class CollectivityResponse {
    public String                        id;
    public String                        name;
    public Integer                       number;
    public String                        location;
    public Specialization                specialization;
    public CollectivityStructureResponse structure;
    public List<MemberResponse>          members;
}