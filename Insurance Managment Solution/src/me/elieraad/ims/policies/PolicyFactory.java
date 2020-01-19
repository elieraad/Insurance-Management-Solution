package me.elieraad.ims.policies;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;

/*THIS IS A FACTORY CLASS TO TAKE CARE OF CREATING POLICY OBJECTS WITH THE CORRESPONDING TYPE (Travel, Medical, Motor)*/
public class PolicyFactory {

    public static Policy createPolicy(ResultSet rs) {
        Policy policy = null;

        try {

            /*GET ALL THE FIELD OF POLICY (id, effectiveDate, expiryDate, premium)*/
            int id = rs.getInt(1);
            Calendar date = Calendar.getInstance();

            date.setTime(rs.getDate(2));
            LocalDate effective = LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));

            date.setTime(rs.getDate(3));
            LocalDate expiry = LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));

            double premium = rs.getDouble(4);
            String policyNo = rs.getString(5);
            /*GET ALL THE FIELD OF POLICY (id, effectiveDate, expiryDate, premium)*/


            if (policyNo.contains("Medical")) {

                /*Get the number of dependents and create Medical Policy object*/
                int dependents = rs.getInt(7);
                policy = new MedicalPolicy(id, effective, expiry, policyNo, premium, dependents);

            } else if (policyNo.contains("Travel")) {

                //Get the departure, destination and family field
                String departure = rs.getString(9);
                String destination = rs.getString(10);
                boolean family = rs.getBoolean(11);

                //Create Medical Policy object
                policy = new TravelPolicy(id, effective, expiry, policyNo, premium, departure, destination, family);

            } else {

                /*Get the price of the vehicle and create Motor Policy object*/
                double price = rs.getDouble(13);
                policy = new MotorPolicy(id, effective, expiry, policyNo, premium, price);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return policy;
    }
}
