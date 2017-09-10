import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.skife.jdbi.v2.DBI;

/*
@Path("/users")
public class MessageCtrl {
    private final DBI dbi;

    public MessageCtrl(DBI dbi) {
        this.dbi = dbi;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(User user) {
        return this.dbi.withHandle((handle) -> {
            try {
                handle.execute("INSERT INTO users (username, password) VALUES (?, ?)", user.username, user.password);
                return Response.ok("Successfully created user").build();
            } catch(Exception ex) {
                if (ex.getCause() instanceof SQLException) {
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_DUPLICATE) {

                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Username is taken").build();
                    }    
                }
                
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        });
    }

    /* for testing purpose only *
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> get() {
        return this.dbi.withHandle((handle) -> {
            List<Map<String, Object>> rs = handle.select("SELECT * FROM users");
            List<User> users = new ArrayList<>();
            for(Map<String, Object> map : rs) {
                User user = new User();
                user.id = (int)map.get("id");
                user.username = (String)map.get("username");
                user.password = (String)map.get("password");
                user.creationTime = (String)map.get("creationTime");
                users.add(user);
            }
            return users;
        });
    }
}

*/
