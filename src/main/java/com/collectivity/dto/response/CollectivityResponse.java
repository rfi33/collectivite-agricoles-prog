package com.collectivity.dto.response;

import java.util.List;

public class CollectivityResponse {
    public String                        id;
    public String                        name;
    public Integer                       number;   // integer dans la spec, pas String
    public String                        location;
    public CollectivityStructureResponse structure;
    public List<MemberResponse>          members;
}

