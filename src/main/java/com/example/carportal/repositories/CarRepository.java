package com.example.carportal.repositories;

import com.example.carportal.models.Car;
import com.example.carportal.repositories.utility.DBConnector;

import java.sql.*;
import java.util.List;

public class CarRepository implements ICarRepository {

    private DBConnector dbc = new DBConnector();
    private Connection con;


    @Override //TODO Needs to be changed, since repository can't know models MÅSKE
    public Car getOneEntity(int ID) {
        con = dbc.getConnection();
        Car car = null;
        try{
            ResultSet rs;
            Statement stmt;
            String sqlString = "SELECT * FROM `car` WHERE Car_ID = " + ID + "" + ";";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sqlString);
            rs.next();
            car = new Car(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(6));
        } catch (SQLException e){
            e.printStackTrace();
        }
        return car;
    }

    @Override
    public List getAllEntities() { // Might not be needed.
        return null;
    }

    @Override
    public boolean create(Car entity) { // Might not be needed
        return false;
    }

    @Override
    public boolean update(int id) { // Change status Available/Unavailable
        int availableNumber = 1;
            if (getOneEntity(id).isAvailable()) {
                availableNumber = 0;
            }
            con = dbc.getConnection();
            try
            {
                String sqlString = "UPDATE car SET Available = '" + availableNumber + "' WHERE car_id = '" + id + "'";
                PreparedStatement preparedStatement = con.prepareStatement(sqlString);
                preparedStatement.executeUpdate();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }

        return false;
    }

    public static void main(String[] args) {
        CarRepository c = new CarRepository();
        c.update(1);
        System.out.println(c.getOneEntity(1));
    }

}
