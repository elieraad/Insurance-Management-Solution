package me.elieraad.ims.policies;

import me.elieraad.ims.db.MySQL;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TravelPolicy extends Policy {

    private String departure, destination;
    private boolean family;
    private long daysTravelled;

    //This constructor is used for creating a Policy object to be inserted into the DB
    public TravelPolicy(LocalDate effectiveDate, LocalDate expiryDate, String departure, String destination, boolean family) {
        super(effectiveDate, expiryDate);

        this.departure = departure;
        this.destination = destination;
        this.family = family;

        daysTravelled = effectiveDate.until(expiryDate, ChronoUnit.DAYS);
        isValid = validate();
        premium = calculatePremium();
        addPolicy();
    }

    //This constructor is used for creating a Policy object retried from DB
    TravelPolicy(int id, LocalDate effective, LocalDate expiry, String policyNo, double premium, String departure, String destination, boolean family) {
        super(id, effective, expiry, policyNo, premium);
        this.departure = departure;
        this.destination = destination;
        this.family = family;
    }

    //Generate policyNo value to match the following format: {Current Year}-Travel-{Id}
    @Override
    public String generatePolicyNo() {
        return String.format("%s-%s-%s", LocalDate.now().getYear(), "Travel", getId());
    }

    //Add a Travel Policy entry to the DB
    @Override
    public void addPolicy() {
        //First create and add a policy entry to the DB
        super.addPolicy();

        MySQL mySQL = MySQL.getInstance();

        try {

            PreparedStatement statement = mySQL.getConn().prepareStatement("INSERT INTO TRAVEL(policyNo, departure, destination, family) VALUES(?,?,?,?)");
            statement.setString(1, policyNo);
            statement.setString(2, getDeparture());
            statement.setString(3, getDestination());
            statement.setBoolean(4, isFamily());
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

    //Return true if expiry date > effective date and days travel less than 30
    @Override
    public boolean validate() {
        return getEffectiveDate().isBefore(getExpiryDate()) && daysTravelled < 31;
    }

    //Calculate the premium: 10$/day with the family, 5$/day without the family
    @Override
    public double calculatePremium() {
        return daysTravelled * (family ? 10 : 5);
    }


    /*GETTERS AND SETTERS*/
    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }

    public long getDaysTravelled() {
        return daysTravelled;
    }

    public void setDaysTravelled(long daysTravelled) {
        this.daysTravelled = daysTravelled;
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
                        "Departure: %s%n" +
                        "Destination: %s%n" +
                        "Family: %b%n" +
                        "-------------------------------------------------------------------------------------",
                getId(), getEffectiveDate(), getExpiryDate(), getPolicyNo(), getPremium(), isValid(), getDeparture(), getDestination(), isFamily());
    }
}
