package com.techlabs.SmartBankingSystem.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {

	final String URL = "jdbc:mysql://localhost:3306/smart_bank_system";
	final String USER = "root";
	final String PASS = "admin#123";

	public boolean validateCustomer(int customerId, String inputPassword) {
	    try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
	        String query = "SELECT c.password, c.status AS cust_status, a.status AS acc_status "
	                     + "FROM customers c JOIN accounts a ON c.customer_id = a.customer_id "
	                     + "WHERE c.customer_id = ?";
	        PreparedStatement ps = connection.prepareStatement(query);
	        ps.setInt(1, customerId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            String storedPassword = rs.getString("password");
	            String customerStatus = rs.getString("cust_status");
	            String accountStatus = rs.getString("acc_status");

	            if (!customerStatus.equalsIgnoreCase("ACTIVE")) {
	                System.out.println("Customer account is inactive.");
	                return false;
	            }

	            if (!accountStatus.equalsIgnoreCase("ACTIVE")) {
	                System.out.println("Bank account is inactive.");
	                return false;
	            }

	            if (storedPassword.equals(inputPassword)) {
	                return true;
	            } else {
	                System.out.println("Incorrect password.");
	                return false;
	            }
	        } else {
	            System.out.println("Customer not found.");
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}



}
