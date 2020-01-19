package me.elieraad.ims.policies;

import me.elieraad.ims.db.MySQL;

import java.sql.*;
import java.time.LocalDate;

public class MotorPolicy extends Policy {

    private double vehiclePrice;

    public MotorPolicy(LocalDate effectiveDate, LocalDate expiryDate, double vehiclePrice) {
        super(effectiveDate, expiryDate);
        this.vehiclePrice = vehiclePrice;
        isValid = validate();
        premium = calculatePremium();
        addPolicy();
    }

    //This constructor is used for creating a Motor Policy object retried from DB
    MotorPolicy(int id, LocalDate effective, LocalDate expiry, String policyNo, double premium, double price) {
        super(id, effective, expiry, policyNo, premium);
        vehiclePrice = price;
    }

    //Generate policyNo value to match the following format: {Current Year}-Motor-{Id}
    @Override
    public String generatePolicyNo() {
        return String.format("%s-%s-%s", LocalDate.now().getYear(), "Motor", getId());
    }

    //Add a Motor Policy entry to the DB
    @Override
    public void addPolicy() {
        //First create and add a policy entry to the DB
        super.addPolicy();
        MySQL mySQL = MySQL.getInstance();

        try {

            PreparedStatement statement = mySQL.getConn().prepareStatement("INSERT INTO MOTOR(policyNo, vehiclePrice) VALUES(?, ?)");
            statement.setString(1, policyNo);
            statement.setDouble(2, vehiclePrice);
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

    //Return true if expiry date > effective date
    @Override
    public boolean validate() {
        return getEffectiveDate().isBefore(getExpiryDate());
    }

    //Calculate the premium
    @Override
    public double calculatePremium() {
        return vehiclePrice * 0.2;
    }



    /*GETTERS AND SETTERS*/
    public double getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(double vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
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
                        "Vehicle Price: %.2f$%n" +
                        "-------------------------------------------------------------------------------------",
                getId(), getEffectiveDate(), getExpiryDate(), getPolicyNo(), getPremium(), isValid(), vehiclePrice);
    }
}
