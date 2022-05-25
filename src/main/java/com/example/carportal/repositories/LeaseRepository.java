package com.example.carportal.repositories;

import com.example.carportal.models.*;
import com.example.carportal.repositories.utility.DBConnector;
import org.apache.tomcat.jni.Local;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaseRepository implements ILeaseRepository {
    //TODO we should consider having a DamageRepository, there are a lot of methods for Damages

    private DBConnector dbc = new DBConnector();
    private Connection con;

    @Override
    public Object getOneEntity(int ID) { //For future implementations
        con = dbc.getConnection();
        Lease lease = null;
        try {
            ResultSet rs;
            Statement stmt;
            String sqlString = "SELECT * FROM `lease` WHERE `lease_id` = '" + ID + "'";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                LocalDate startDate = rs.getDate(5).toLocalDate();
                LocalDate endDate = rs.getDate(6).toLocalDate();
                lease = new Lease(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), startDate, endDate, rs.getBoolean(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lease;
    }


    @Override
    public List getAllEntities() { //For future implementations
        ArrayList<Lease> listOfLeases = new ArrayList<>();
        con = dbc.getConnection();
        Lease lease;
        try {
            ResultSet rs;
            Statement stmt;
            String sqlString = "SELECT * FROM `lease`";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                LocalDate startDate = rs.getDate(5).toLocalDate();
                LocalDate endDate = rs.getDate(6).toLocalDate();
                lease = new Lease(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), startDate, endDate, rs.getBoolean(7));
                listOfLeases.add(lease);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfLeases;
    }


    @Override
    public boolean create(Object entity) { // Add lease to database

        con = dbc.getConnection();
        int carID = ((Lease) entity).getCarID();
        int costumerID = ((Lease) entity).getCustomerID();
        double price = ((Lease) entity).getPrice();
        LocalDate startDate = ((Lease) entity).getStartDate();
        LocalDate endDate = ((Lease) entity).getEndDate();
        boolean status = ((Lease) entity).isStatus();
        try {
            PreparedStatement preparedStatement = con.prepareStatement
                    ("INSERT INTO `zz8alsto5xji5csq`.`lease` (`Car_id`, `Costumer_id`, `Price`, `Start_date`, `Stop_date`, `Status`) VALUES (?,?,?,?,?,?);");
            preparedStatement.setInt(1, carID);
            preparedStatement.setInt(2, costumerID);
            preparedStatement.setDouble(3, price);
            preparedStatement.setDate(4, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(5, java.sql.Date.valueOf(endDate));
            preparedStatement.setBoolean(6, status);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean damageReport(int leaseID, ArrayList<Damage> listOfDamages) {    // Create dmgReport for the chosen lease.
        Lease lease = (Lease) getOneEntity(leaseID);
        int carID = lease.getCarID();
        con = dbc.getConnection();

        try {
            for (Damage listOfDamage : listOfDamages) {
                Damage damage = new Damage(listOfDamage.getDescription(), listOfDamage.getPrice());
                PreparedStatement preparedStatement = con.prepareStatement
                        ("INSERT INTO `zz8alsto5xji5csq`.`damage`(`Car_id`, `Lease_id`, `Dmg_description`, `Price`, `Repaired`) VALUES (?,?,?,?,?);");
                preparedStatement.setInt(1, carID);
                preparedStatement.setInt(2, leaseID);
                preparedStatement.setString(3, damage.getDescription());
                preparedStatement.setDouble(4, damage.getPrice());
                preparedStatement.setBoolean(5, false);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return closeLease(leaseID);


    }

    public ArrayList<Damage> listOfDamagesOnLease(int leaseID) { //Gets all damages associated with the specific lease
        ArrayList<Damage> damageArrayList = new ArrayList<>();
        Damage damage;
        con = dbc.getConnection();
        try {
            ResultSet rs;
            Statement stmt;
            String sqlString = "SELECT * FROM `damage` WHERE `Lease_id` = " + leaseID + "" + ";";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                damage = new Damage(rs.getInt(1), rs.getString(4), rs.getDouble(5));
                damageArrayList.add(damage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return damageArrayList;

    }

    public boolean closeLease(int leaseID){
        con = dbc.getConnection();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE `zz8alsto5xji5csq`.`lease` SET `Status` = '0' WHERE (`Lease_id` = '" + leaseID + "')");
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Lease> getAllOpenLeases() { // When user needs to create a dmgReport, get a
                                                 // monthly income and see list of cars rented out.
        ArrayList<Lease> listOfLeases = new ArrayList<>();
        con = dbc.getConnection();
        Lease lease;
        try {
            ResultSet rs;
            Statement stmt;
            String sqlString = "SELECT * FROM `lease` WHERE `status` = 1 ORDER BY car_id";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                LocalDate startDate = rs.getDate(5).toLocalDate();
                LocalDate endDate = rs.getDate(6).toLocalDate();
                lease = new Lease(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), startDate, endDate, rs.getBoolean(7));
                listOfLeases.add(lease);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfLeases;
    }

    @Override
    public boolean update(int ID) {     // Not needed, but can't be deleted
        //COULD BE UPDATE DAMAGE TO REPAIRED
        return false;
    }

    @Override
    public boolean delete(int ID) {
        con = dbc.getConnection();
        try
        {
            String sqlString = "DELETE FROM `zz8alsto5xji5csq`.`lease` WHERE (`Lease_ID` = '" + ID + "');";
            PreparedStatement ps = con.prepareStatement(sqlString);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return true;
    }

    //TEST
    public static void main(String[] args) {
        LeaseRepository lr = new LeaseRepository();
        UserRepository userRepository = new UserRepository();
        lr.getOneEntity(1);


    }


}
