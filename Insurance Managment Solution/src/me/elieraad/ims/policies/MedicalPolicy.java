package me.elieraad.ims.policies;

import me.elieraad.ims.models.Beneficiary;
import me.elieraad.ims.db.MySQL;
import me.elieraad.ims.enums.Relationship;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MedicalPolicy extends Policy {

    private List<Beneficiary> beneficiaries;
    private int dependents;

    //This constructor is used for creating a Policy object retried from DB
    MedicalPolicy(int id, LocalDate effectiveDate, LocalDate expiryDate, String policyNo, double premium, int dependents) {
        super(id, effectiveDate, expiryDate, policyNo, premium);
        this.dependents = dependents;
    }

    //This constructor is used for creating a Policy object to be inserted into the DB
    public MedicalPolicy(LocalDate effectiveDate, LocalDate expiryDate, List<Beneficiary> beneficiaries) {
        super(effectiveDate, expiryDate);
        this.beneficiaries = beneficiaries;
        isValid = validate();
        premium = calculatePremium();
        dependents = getDependents();
        addPolicy();
    }

    //Generate policyNo value to match the following format: {Current Year}-Medical-{Id}
    @Override
    public String generatePolicyNo() {
        return String.format("%s-%s-%s", LocalDate.now().getYear(), "Medical", getId());
    }

    //Add a Medical Policy entry to the DB
    @Override
    public void addPolicy() {
        //First create and add a policy entry to the DB
        super.addPolicy();
        MySQL mySQL = MySQL.getInstance();

        try {

            PreparedStatement statement = mySQL.getConn().prepareStatement("INSERT INTO MEDICAL(policyNo, beneficiaries) VALUES(?, ?)");
            statement.setString(1, policyNo);
            statement.setInt(2, dependents);
            statement.executeUpdate();

            mySQL.getConn().commit(); //COMMIT
            System.out.printf("Policy %s was added successfully!%n", policyNo);

        } catch (SQLException e) {
            try {
                mySQL.getConn().rollback(); //ROLLBACK if something went wrong
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

    }

    /*Calculate the premium:
     *15$ per beneficiary < 10 years old,
     *30$ per beneficiary <= 45 years old
     *63$ per beneficiary > 45 years old
     */
    @Override
    public double calculatePremium() {
        double totalPremium = 0;
        for (Beneficiary beneficiary : beneficiaries) {
            int premium;
            long age = beneficiary.getDateOfBirth().until(LocalDate.now(), ChronoUnit.YEARS);

            if (age < 10) premium = 15;
            else if (age <= 45) premium = 30;
            else premium = 63;


            totalPremium += premium;
        }

        return totalPremium;
    }

    @Override
    public boolean validate() {
        //Return false if expiry date > effective date or no available beneficiaries
        if (getEffectiveDate().isAfter(getExpiryDate()) || beneficiaries.isEmpty())
            return false;

        //Return false if beneficiaries with SELF relationship > 1
        int counter = 0;
        for (Beneficiary beneficiary : beneficiaries) {
            counter = beneficiary.getRelationship() == Relationship.SELF ? counter + 1 : counter;
            if (counter > 1) return false;
        }

        return true;
    }

    //Helper method to calculate nb of dependents
    private int getDependents() {
        //if self relationship exists returns number of beneficiaries - 1
        for (Beneficiary beneficiary : beneficiaries)
            if (beneficiary.getRelationship() == Relationship.SELF)
                return beneficiaries.size() - 1;

        //else return number of beneficiaries
        return beneficiaries.size();
    }



    /*GETTERS AND SETTERS*/
    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    @Override
    public String toString() {
        return String.format(
                "Id: %d%n" +
                        "Effective: %s%n" +
                        "Expiry: %s%n" +
                        "PolicyNo: %s%n" +
                        "Premium: %.2f$%n" +
                        "Valid: %b%n" +
                        "Beneficiaries: %d%n" +
                        "-------------------------------------------------------------------------------------",
                getId(), getEffectiveDate(), getExpiryDate(), getPolicyNo(), getPremium(), isValid(), dependents);
    }
}
