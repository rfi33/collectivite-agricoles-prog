package entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode

public class Member {
    public String id;
    public String firstName;
    public String lastName;
    public LocalDate birthDate;
    public String gender;
    public String address;
    public String profession;
    public String phoneNumber;
    public String email;
    public String occupation;

    public List<String> referees;
}
