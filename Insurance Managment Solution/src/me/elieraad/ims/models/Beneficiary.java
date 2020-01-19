package me.elieraad.ims.models;

import me.elieraad.ims.enums.Gender;
import me.elieraad.ims.enums.Relationship;

import java.time.LocalDate;

public class Beneficiary {
    private String name;
    private Relationship relationship;
    private Gender gender;
    private LocalDate dateOfBirth;

    public Beneficiary(String name, Relationship relationship, Gender gender, LocalDate dateOfBirth) {
        this.name = name;
        this.relationship = relationship;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
