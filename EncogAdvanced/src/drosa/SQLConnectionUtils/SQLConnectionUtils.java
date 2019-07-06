package drosa.SQLConnectionUtils;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnectionUtils {	    
    
	Connection conn = null;
	String bbdd = null;
	Statement stmt;
	
	public void init(){
		
		String userName = "root";
        String password = "root";
        String url = "jdbc:mysql://localhost/finances";
    	    	
    	try {
			Class.forName ("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			conn = DriverManager.getConnection (url, userName, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
        try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init(String bbddName){
		
		this.bbdd = bbddName;
		String userName = "root";
        String password = "";
        String url = "jdbc:mysql://localhost/"+bbddName;
    	    	
    	try {
			Class.forName ("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			conn = DriverManager.getConnection (url, userName, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeConnection(){		
		try {
			stmt.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn = null;
	}
	
    public Connection getConnection(){
    	return conn;
    }

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getBbdd() {
		return bbdd;
	}

	public void setBbdd(String bbdd) {
		this.bbdd = bbdd;
	}

	public Statement getStatement() {
		// TODO Auto-generated method stub
		return stmt;
	}
    
    
    
}
