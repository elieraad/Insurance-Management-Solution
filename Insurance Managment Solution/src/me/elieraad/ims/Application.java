package me.elieraad.ims;

import me.elieraad.ims.db.MySQL;
import me.elieraad.ims.enums.Gender;
import me.elieraad.ims.enums.Relationship;
import me.elieraad.ims.models.Beneficiary;
import me.elieraad.ims.models.Claim;
import me.elieraad.ims.policies.MedicalPolicy;
import me.elieraad.ims.policies.MotorPolicy;
import me.elieraad.ims.policies.PolicyFactory;
import me.elieraad.ims.policies.TravelPolicy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private static MySQL mySQL = MySQL.getInstance();

    public static void main(String[] args) {
        mySQL.connect("root", "root");

        System.out.println("Adding Claim Entries...");
        addPolicyEntries();

        System.out.print("\nAvailable Policies:\n" +
                "-------------------------------------------------------------------------------------\n");
        displayAvailableEntries();

        System.out.print("\nPolicies having premium between 500 and 2000:\n" +
                "-------------------------------------------------------------------------------------\n");
        displayPremiumBetween(500, 2000);

        System.out.println("\nAdding Claim Entries...");
        addClaimEntries();

        System.out.println("\nClaims Stats:");
        displayClaimStats();

    }

    //Attempt adding 8 policy entries, 2 will fail: 1 Travel and 1 Medical
    private static void addPolicyEntries() {

        //This policy will be added successfully
        new TravelPolicy(
                LocalDate.of(2018, 6, 5),
                LocalDate.of(2018, 7, 4),
                "France", "Lebanon", true
        );

        //This policy will be added successfully
        new MotorPolicy(
                LocalDate.of(2019, 6, 5),
                LocalDate.of(2020, 6, 5),
                8750
        );

        //This policy will be added successfully
        List<Beneficiary> beneficiaries = new ArrayList<>();
        beneficiaries.add(new Beneficiary("Elie", Relationship.SELF, Gender.MALE, LocalDate.of(1962, 6, 5)));
        beneficiaries.add(new Beneficiary("Tia", Relationship.SPOUSE, Gender.FEMALE, LocalDate.of(1970, 6, 5)));
        beneficiaries.add(new Beneficiary("Joe", Relationship.SON, Gender.MALE, LocalDate.of(1998, 6, 5)));
        beneficiaries.add(new Beneficiary("Mark", Relationship.SON, Gender.MALE, LocalDate.of(2000, 6, 5)));
        beneficiaries.add(new Beneficiary("Michelle", Relationship.DAUGHTER, Gender.FEMALE, LocalDate.of(2002, 6, 5)));

        new MedicalPolicy(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2025, 1, 1),
                beneficiaries
        );

        //This travel policy will fail, days travelled exceed 30
        new TravelPolicy(
                LocalDate.of(2018, 6, 5),
                LocalDate.of(2018, 7, 7),
                "France", "Lebanon", true
        );

        //This policy will be added successfully
        new TravelPolicy(
                LocalDate.of(2019, 10, 5),
                LocalDate.of(2019, 10, 10),
                "Lebanon", "Italy", false
        );

        //This policy will be added successfully
        new MotorPolicy(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2021, 1, 1),
                5000
        );

        //This medical policy will fail, 2 SELF beneficiaries
        beneficiaries = new ArrayList<>();
        beneficiaries.add(new Beneficiary("Paul", Relationship.SELF, Gender.MALE, LocalDate.of(1998, 6, 5)));
        beneficiaries.add(new Beneficiary("Georges", Relationship.SELF, Gender.MALE, LocalDate.of(1992, 6, 5)));
        beneficiaries.add(new Beneficiary("Maria", Relationship.DAUGHTER, Gender.FEMALE, LocalDate.of(1999, 6, 5)));
        beneficiaries.add(new Beneficiary("Chris", Relationship.SON, Gender.MALE, LocalDate.of(2019, 6, 5)));

        new MedicalPolicy(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2025, 1, 1),
                beneficiaries
        );

        //This policy will be added successfully
        new TravelPolicy(
                LocalDate.of(2019, 12, 23),
                LocalDate.of(2020, 1, 2),
                "Spain", "New York", true
        );
    }

    //Display all available policies
    private static void displayAvailableEntries() {
        try {
            PreparedStatement st = mySQL.getConn().prepareStatement("SELECT * FROM POLICY\n" +
                    "LEFT JOIN MEDICAL ON POLICY.policyNo = MEDICAL.policyNo\n" +
                    "LEFT JOIN TRAVEL ON POLICY.policyNo = TRAVEL.policyNo \n" +
                    "LEFT JOIN MOTOR ON POLICY.policyNo = MOTOR.policyNo");

            ResultSet rs = st.executeQuery();
            //The PolicyFactory will take care of creating the corresponding policy type
            while (rs.next()) System.out.println(PolicyFactory.createPolicy(rs).toString());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Display all policies having premium berween low and high (i.e. 500 and 2000)
    private static void displayPremiumBetween(double low, double high) {
        try {

            PreparedStatement st = mySQL.getConn().prepareStatement("SELECT * FROM POLICY\n" +
                    "LEFT JOIN MEDICAL ON POLICY.policyNo = MEDICAL.policyNo\n" +
                    "LEFT JOIN TRAVEL ON POLICY.policyNo = TRAVEL.policyNo \n" +
                    "LEFT JOIN MOTOR ON POLICY.policyNo = MOTOR.policyNo \n" +
                    "WHERE premium BETWEEN ? AND ?");

            st.setDouble(1, low);
            st.setDouble(2, high);

            ResultSet rs = st.executeQuery();
            //The PolicyFactory will take care of creating the corresponding policy type
            while (rs.next())
                System.out.println(PolicyFactory.createPolicy(rs).toString());


        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    //Attempt adding 15 claim entries, 5 will fail
    private static void addClaimEntries() {

        new Claim(LocalDate.of(2018, 6, 19), "2020-Travel-1", 90);

        new Claim(LocalDate.of(2019, 10, 19), "2020-Motor-2", 750);
        new Claim(LocalDate.of(2020, 1, 5), "2020-Motor-2", 2000);
        new Claim(LocalDate.of(2020, 5, 18), "2020-Motor-2", 500);

        //rejected
        new Claim(LocalDate.of(2020, 10, 19), "2020-Motor-2", 1000);

        //rejected
        new Claim(LocalDate.of(2022, 10, 19), "2020-Motor-3", 1000);

        new Claim(LocalDate.of(2022, 10, 19), "2020-Medical-3", 1000);

        //rejected
        new Claim(LocalDate.of(2019, 10, 19), "2020-Travel-4", 12.99);

        new Claim(LocalDate.of(2020, 3, 18), "2020-Motor-5", 3000);
        new Claim(LocalDate.of(2020, 7, 20), "2020-Motor-5", 1500);

        //rejected
        new Claim(LocalDate.of(2021, 2, 1), "2020-Motor-5", 1000);

        new Claim(LocalDate.of(2023, 12, 1), "2020-Medical-3", 50);
        new Claim(LocalDate.of(2024, 6, 1), "2020-Medical-3", 50);
        new Claim(LocalDate.of(2024, 12, 1), "2020-Medical-3", 50);

        //rejected
        new Claim(LocalDate.of(2025, 6, 1), "2020-Medical-3", 50);

    }

    //Display inside a table the number of claims issued, total, minimum and maximum claim per policy
    private static void displayClaimStats() {
        try {
            String query = "SELECT policyNo, COUNT(*) AS nbClaims,\n" +
                    "SUM(amount) AS totalClaimed,\n" +
                    "MIN(amount) AS minClaimed, MAX(amount) AS maxClaimed\n" +
                    "FROM CLAIM GROUP BY policyNo";
            PreparedStatement st = mySQL.getConn().prepareStatement(query);


            /*PRINT THE HEADER OF THE TABLE*/
            for (int i = 0; i < 116; i++)
                System.out.print("-");
            System.out.println();

            System.out.printf("| %-20s | %-20s | %-20s | %-20s | %-20s |%n", "Policy No", "No of Claims", "Total Amount ($)", "Min Amount ($)", "Max Amount ($)");

            for (int i = 0; i < 116; i++)
                System.out.print("-");
            System.out.println();
            /*PRINT THE HEADER OF THE TABLE*/


            //Print each row inside the table
            for (ResultSet rs = st.executeQuery(); rs.next(); ) {
                System.out.printf("| %-20s | %-20d | %-20.2f | %-20.2f | %-20.2f |%n", rs.getString(1), rs.getInt(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5));
            }

            //Print the bottom line of the table
            for (int i = 0; i < 116; i++)
                System.out.print("-");
            System.out.println();

        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }

}
