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


@Path("/users")
public class UserCtrl {
    private static final int SQL_ERROR_DUPLICATE_KEY = 1062;
    private static final int HTTP_RESPONSE_BAD_REQUEST = 400;
    private static final int HTTP_RESPONSE_SERVER_ERROR = 500;

    private final DBI dbi;

    public UserCtrl(DBI dbi) {
        this.dbi = dbi;
    }

    /**
     * Creates new user
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(User user) {
        if(!user.validate()) {
            return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Invalid fields").build();
        }

        return this.dbi.withHandle((handle) -> {
            try {
                handle.execute("INSERT INTO users (username, password) VALUES (?, ?)", user.getUsername(), user.getPassword());
                BigInteger id = ((BigInteger)handle.select("SELECT LAST_INSERT_ID() AS id").get(0).get("id"));
                return Response.ok(String.valueOf(id)).build();
            } catch(Exception ex) {
                if (ex.getCause() instanceof SQLException) {
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_DUPLICATE_KEY) {
                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Username is taken").build();
                    }    
                }
                System.out.println(ex.getMessage());
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity("Failed to create user").build();
            }
        });
    }
}


