package com.collectivity.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode
public class Collectivity {
    public String       id;
    public String       name;
    public String       number;
    public String       location;
    public boolean      federationApproval;
    public Member       president;
    public Member       vicePresident;
    public Member       treasurer;
    public Member       secretary;
    public List<Member> members;
}