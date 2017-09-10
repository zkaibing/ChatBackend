package controllers;

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
import javax.ws.rs.QueryParam;
import org.skife.jdbi.v2.DBI;
import models.Message;
import models.Message.MessageType;
import models.TextMessage;
import models.ImageMessage;
import models.VideoMessage;

@Path("/messages")
public class MessageCtrl {
    private static final int HTTP_RESPONSE_BAD_REQUEST = 400;
    private static final int HTTP_RESPONSE_SERVER_ERROR = 500;

    private final DBI dbi;

    public MessageCtrl(DBI dbi) {
        this.dbi = dbi;
    }

    @POST
    @Path("/text")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postText(TextMessage text) {
        return this.dbi.withHandle((handle) -> {
            try {
                String query = 
                "INSERT INTO messages " + 
                "   (senderId, recipientId, type, content) " +
                "VALUES " + 
                "   (?, ?, 'TEXT', ?) ";

                handle.execute(query, text.getSenderId(), text.getRecipientId(), text.getContent());
                //ResponseBuilder rb = Response.ok("Successfully sent massage");
                //return rb.build();
                return Response.ok("Successfully sent massage").build();
            } catch(Exception ex) {
                /*
                if (ex.getCause() instanceof SQLException) {
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_DUPLICATE) {

                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Username is taken").build();
                    }    
                }
                */
                
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        });
    }

    @POST
    @Path("/image")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postImage(ImageMessage image) {
        return this.dbi.withHandle((handle) -> {
            try {
                String query1 = 
                "INSERT INTO messages " + 
                "   (senderId, recipientId, type, content) " +
                "VALUES " + 
                "   (?, ?, 'IMAGE', ?) ";

                String query2 =
                "INSERT INTO imageMetadata " + 
                "   (messageId, width, height) " +
                "VALUES " + 
                "   (LAST_INSERT_ID(), ?, ?) ";

                handle.execute(query1, image.getSenderId(), image.getRecipientId(), image.getContent());
                handle.execute(query2, image.getWidth(), image.getHeight());

                return Response.ok("Successfully sent massage").build();
            } catch(Exception ex) {
                
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        });
    }

    @POST
    @Path("/video")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postVideo(VideoMessage video) {
        return this.dbi.withHandle((handle) -> {
            try {
                String query1 = 
                "INSERT INTO messages " + 
                "   (senderId, recipientId, type, content) " +
                "VALUES " + 
                "   (?, ?, 'VIDEO', ?); ";

                String query2 = 
                "INSERT INTO videoMetadata " + 
                "   (messageId, length, source) " +
                "VALUES " + 
                "   (LAST_INSERT_ID(), ?, ?) ";

                
                handle.execute(query1, video.getSenderId(), video.getRecipientId(), video.getContent());
                handle.execute(query2, video.getLength(), video.getSource());

                return Response.ok("Successfully sent massage").build();
            } catch(Exception ex) {
                
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Message> get(@QueryParam("senderId") int senderId,
                             @QueryParam("recipientId") int recipientId) {
        System.out.println("HAHAHAHAHA");
        return this.dbi.withHandle((handle) -> {
            String query = 
            "SELECT " +
            "   m.id, m.senderId, m.recipientId, m.type, m.content, m.creationTime, " +
            "   i.width, i.height, v.length, v.source " +
            "FROM messages m " +
            "   LEFT JOIN imageMetadata i " +
            "       ON m.id = i.messageId " + 
            "   LEFT JOIN videoMetadata v " +
            "       ON m.id = v.messageId " +
            "WHERE m.senderId = ? " +
            "   AND m.recipientId = ? " +
            "ORDER BY m.creationTime ";

            List<Map<String, Object>> rows = handle.select(query, senderId, recipientId);
            List<Message> messages = new ArrayList<>();
            for(Map<String, Object> map : rows) {
                switch(MessageType.valueOf((String)map.get("type"))) {
                    case TEXT:
                        messages.add(new TextMessage(map));
                        break;
                    case  IMAGE:
                        messages.add(new ImageMessage(map));
                        break;
                    case VIDEO:
                        messages.add(new VideoMessage(map));
                        break;
                }
            }
            return messages;
        });
    }
}

