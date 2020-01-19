package me.elieraad.ims.models;

import me.elieraad.ims.db.MySQL;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Claim {
    private int id;
    private LocalDate incurredDate;
    private String policyNo;
    private double claimedAmount;

    public Claim(LocalDate incurredDate, String policyNo, double claimedAmount) {
        this.incurredDate = incurredDate;
        this.policyNo = policyNo;
        this.claimedAmount = claimedAmount;
        addClaim();
    }

    //Add claim to database
    private void addClaim() {
        if (!policyExists()) {
            System.out.printf("Cannot submit a claim for Policy# %s because it does not exist.%n", policyNo);
            return;
        }

        if (!policyActive()) {
            System.out.printf("Claim is rejected because Policy# %s is inactive or expired.%n", policyNo);
            return;
        }

        MySQL mySQL = MySQL.getInstance();
        try {

            PreparedStatement statement = mySQL.getConn().prepareStatement("INSERT INTO CLAIM(policyNo, amount, incurred) VALUES(?, ?, ?)");
            statement.setString(1, policyNo);
            statement.setDouble(2, claimedAmount);
            statement.setDate(3, Date.valueOf(incurredDate));
            statement.executeUpdate();

            mySQL.getConn().commit();

        } catch (SQLException e) {
            try {
                mySQL.getConn().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
    }

    //Return true if query isn't empty
    private boolean policyActive() {
        MySQL mySQL = MySQL.getInstance();
        try {

            PreparedStatement statement = mySQL.getConn().prepareStatement("SELECT * FROM POLICY WHERE policyNo = ? AND effectiveDate < ? AND expiryDate > ?");
            Date date = Date.valueOf(incurredDate);

            statement.setString(1, policyNo);
            statement.setDate(2, date);
            statement.setDate(3, date);
            return statement.executeQuery().next();

        } catch (SQLException ignore) {
        }
        return false;
    }

    //Return true if query isn't empty
    private boolean policyExists() {
        MySQL mySQL = MySQL.getInstance();
        try {

            PreparedStatement statement = mySQL.getConn().prepareStatement("SELECT * FROM POLICY WHERE policyNo = ?");
            statement.setString(1, policyNo);
            return statement.executeQuery().next();

        } catch (SQLException ignore) {
        }
        return false;
    }


    /*GETTERS AND SETTERS*/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getIncurredDate() {
        return incurredDate;
    }

    public void setIncurredDate(LocalDate incurredDate) {
        this.incurredDate = incurredDate;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public double getClaimedAmount() {
        return claimedAmount;
    }

    public void setClaimedAmount(double claimedAmount) {
        this.claimedAmount = claimedAmount;
    }
}
