package com.collectivity.dto.request;

import com.collectivity.entity.Specialization;

import java.util.List;

public class CreateCollectivityRequest {
    public String                            location;
    public Specialization                    specialization;
    public Boolean                           federationApproval;
    public CreateCollectivityStructureRequest structure;
    public List<String>                      members;
}