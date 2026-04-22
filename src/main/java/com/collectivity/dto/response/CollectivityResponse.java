package com.collectivity.dto.response;

import java.util.List;

public class CollectivityResponse {
    public String                        id;
    public String                        name;
    public String                        number;
    public String                        location;
    public CollectivityStructureResponse structure;
    public List<MemberResponse>          members;
}