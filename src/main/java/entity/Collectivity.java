package entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode

public class Collectivity {
    public String id;
    public String location;
    public boolean federationApproval;

    public String president;
    public String vicePresident;
    public String treasurer;
    public String secretary;

    public List<Member> members;
}