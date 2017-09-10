package models;

import java.util.Map;
import java.sql.Timestamp;

public class User {
	private int id;
    private String username;
    private String password;
    private String creationTime;

    public User() {}

    public User(Map<String, Object> map) {
    	this.id = (int)map.get("id");
        this.username = (String)map.get("username");
        this.password = (String)map.get("password");
        this.creationTime = ((Timestamp)map.get("creationTime")).toString();
    }

    public int getId() {
    	return id;
    }

    public String getUsername() {
    	return username;
    }

    public String getPassword() {
    	return password;
    }

    public String getCreationTime() {
    	return creationTime;
    }
}
