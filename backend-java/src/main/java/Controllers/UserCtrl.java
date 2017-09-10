package controllers;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.SQLException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import org.skife.jdbi.v2.DBI;
import models.User;
//import models.ResponseEntity;

@Path("/users")
public class UserCtrl {
    private static final int SQL_ERROR_DUPLICATE = 1062;
    private static final int HTTP_RESPONSE_BAD_REQUEST = 400;
    private static final int HTTP_RESPONSE_SERVER_ERROR = 500;

    private final DBI dbi;

    public UserCtrl(DBI dbi) {
        this.dbi = dbi;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(User user) {
        return this.dbi.withHandle((handle) -> {
            try {
                handle.execute("INSERT INTO users (username, password) VALUES (?, ?)", user.getUsername(), user.getPassword());
                BigInteger id = ((BigInteger)handle.select("SELECT LAST_INSERT_ID() AS id").get(0).get("id"));
                return Response.ok(String.valueOf(id)).build();
            } catch(Exception ex) {
                if (ex.getCause() instanceof SQLException) {
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_DUPLICATE) {
                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Username is taken").build();
                    }    
                }
                System.out.println(ex.getMessage());
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity("Failed to create user").build();
            }
        });
    }

    /* for testing purpose only */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> get() {
        return this.dbi.withHandle((handle) -> {
            List<Map<String, Object>> rs = handle.select("SELECT * FROM users");
            List<User> users = rs.stream()
                .map(row -> new User(row))
                .collect(Collectors.toList());
            return users;
        });
    }
}


