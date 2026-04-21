package com.collectivity.dto.request;

import java.util.List;

public class CreateCollectivityRequest {
    public String                            location;
    public Boolean                           federationApproval;
    public CreateCollectivityStructureRequest structure;
    public List<String>                      members;
}