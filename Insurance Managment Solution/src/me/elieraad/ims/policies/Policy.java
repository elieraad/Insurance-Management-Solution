package me.elieraad.ims.policies;

import me.elieraad.ims.db.MySQL;

import java.sql.*;
import java.time.LocalDate;

public abstract class Policy {
    private int id;
    private LocalDate effectiveDate, expiryDate;

    String policyNo;
    double premium;
    boolean isValid;


    //This constructor is used for creating a Policy object retried from DB
    Policy(int id, LocalDate effectiveDate, LocalDate expiryDate, String policyNo, double premium) {
        this.id = id;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.policyNo = policyNo;
        this.premium = premium;
        isValid = true;
    }

    //This constructor is used for creating a Policy object to be inserted into the DB
    Policy(LocalDate effectiveDate, LocalDate expiryDate) {
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
    }

    //TO OVERRIDE: calculate the premium for each policy
    public abstract double calculatePremium();

    //TO OVERRIDE: check the validation rules for each policy
    public abstract boolean validate();

    //TO OVERRIDE:  generate the policyNo for each policy
    public abstract String generatePolicyNo();

    //Add a policy entry to the DB
    public void addPolicy() {
        if (!isValid) {
            System.out.println("Cannot add policy because it is invalid.");
            return;
        }

        MySQL mySQL = MySQL.getInstance();

        try {
            //INSERT A POLICY ENTRY
            PreparedStatement statement = mySQL.getConn().prepareStatement("INSERT INTO POLICY(effectiveDate, expiryDate, premium) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setDate(1, Date.valueOf(getEffectiveDate()));
            statement.setDate(2, Date.valueOf(getExpiryDate()));
            statement.setDouble(3, getPremium());
            statement.executeUpdate();

            //GET THE AUTO-GENERATED ID
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                //GENERATE policyNo
                id = rs.getInt(1);
                policyNo = generatePolicyNo();

                //UPDATE THE policyNo value to match the following format: {Current Year}-{Policy Type}-{Id}
                statement = mySQL.getConn().prepareStatement("UPDATE POLICY SET policyNo = ? WHERE id = ?");
                statement.setString(1, policyNo);
                statement.setInt(2, id);
                statement.executeUpdate();
            } else {
                mySQL.getConn().rollback(); //ROLLBACK QUERIES IN CASE SOMETHING WENT WRONG
            }
        } catch (SQLException e) {
            try {
                mySQL.getConn().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*GETTERS AND SETTERS*/
    int getId() {
        return id;
    }

    LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    LocalDate getExpiryDate() {
        return expiryDate;
    }

    String getPolicyNo() {
        return policyNo;
    }

    double getPremium() {
        return premium;
    }

    boolean isValid() {
        return isValid;
    }

}
