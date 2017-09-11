package controllers;

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import org.skife.jdbi.v2.DBI;
import models.Message;
import models.Message.MessageType;
import models.TextMessage;
import models.ImageMessage;
import models.VideoMessage;

@Path("/messages")
public class MessageCtrl {
    private static final int SQL_ERROR_FOREIGN_KEY = 1452;
    private static final int HTTP_RESPONSE_BAD_REQUEST = 400;
    private static final int HTTP_RESPONSE_SERVER_ERROR = 500;

    private final DBI dbi;

    public MessageCtrl(DBI dbi) {
        this.dbi = dbi;
    }


    /**
     * Sends text message
     */
    @POST
    @Path("/text")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postText(TextMessage text) {
        if(!text.validate()) {
            return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Invalid fields").build();
        }

        return this.dbi.withHandle((handle) -> {
            try {
                String query = 
                "INSERT INTO messages " + 
                "   (senderId, recipientId, type, content) " +
                "VALUES " + 
                "   (?, ?, 'TEXT', ?) ";

                handle.execute(query, text.getSenderId(), text.getRecipientId(), text.getContent());

                BigInteger id = ((BigInteger)handle.select("SELECT LAST_INSERT_ID() AS id").get(0).get("id"));
                return Response.ok(String.valueOf(id)).build();
            } catch(Exception ex) {
                if (ex.getCause() instanceof SQLException) {
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_FOREIGN_KEY) {
                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("User doesn't exist").build();
                    }    
                }
                System.out.println(ex.getMessage());
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity("Failed to send message").build();
            }
        });
    }


    /**
     * Sends image message
     */
    @POST
    @Path("/image")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postImage(ImageMessage image) {
        if(!image.validate()) {
            return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Invalid fields").build();
        }
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

                BigInteger id = ((BigInteger)handle.select("SELECT LAST_INSERT_ID() AS id").get(0).get("id"));
                return Response.ok(String.valueOf(id)).build();
            } catch(Exception ex) {
                if (ex.getCause() instanceof SQLException) {
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_FOREIGN_KEY) {
                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("User doesn't exist").build();
                    }    
                }
                System.out.println(ex.getMessage());
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity("Failed to send message").build();
            }
        });
    }


    /**
     * Sends video message
     */
    @POST
    @Path("/video")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postVideo(VideoMessage video) {
        if(!video.validate()) {
            return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("Invalid fields").build();
        }
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

                BigInteger id = ((BigInteger)handle.select("SELECT LAST_INSERT_ID() AS id").get(0).get("id"));
                return Response.ok(String.valueOf(id)).build();
            } catch(Exception ex) {
                if (ex.getCause() instanceof SQLException) {
                    System.out.println("SQLException!!!");
                    System.out.println(((SQLException)ex.getCause()).getErrorCode());
                    if(((SQLException)ex.getCause()).getErrorCode() == SQL_ERROR_FOREIGN_KEY) {
                        return Response.status(HTTP_RESPONSE_BAD_REQUEST).entity("User doesn't exist").build();
                    }    
                }
                System.out.println(ex.getMessage());
                return Response.status(HTTP_RESPONSE_SERVER_ERROR).entity("Failed to send message").build();
            }
        });
    }


    /**
     * Gets a list of messages between two users
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("user1") int user1,
                        @QueryParam("user2") int user2,
                        @QueryParam("messageCnt") Integer messageCnt,
                        @QueryParam("pageNum") Integer pageNum) {
        return this.dbi.withHandle((handle) -> {
            try {
                String query = 
                "SELECT " +
                "   m.id, m.senderId, m.recipientId, m.type, m.content, m.creationTime, " +
                "   i.width, i.height, v.length, v.source " +
                "FROM messages m " +
                "   LEFT JOIN imageMetadata i " +
                "       ON m.id = i.messageId " + 
                "   LEFT JOIN videoMetadata v " +
                "       ON m.id = v.messageId " +
                "WHERE (m.senderId = ? " +
                "   AND m.recipientId = ?) " +
                "OR (m.senderId = ? " +
                "   AND m.recipientId = ?) " +
                "ORDER BY m.creationTime ";

                if(messageCnt != null && messageCnt > 0 && pageNum != null && pageNum > 0) {
                    query += String.format("LIMIT %d, %d ", (pageNum - 1) * messageCnt, messageCnt);
                }
                List<Map<String, Object>> rows = handle.select(query, user1, user2, user2, user1);
                List<Message> messages = new ArrayList<>();
                for(Map<String, Object> map : rows) {
                    switch(MessageType.valueOf((String)map.get("type"))) {
                        case TEXT:
                            messages.add(new TextMessage(map));
                            break;
                        case IMAGE:
                            messages.add(new ImageMessage(map));
                            break;
                        case VIDEO:
                            messages.add(new VideoMessage(map));
                            break;
                    }
                }
                return Response.ok(messages).build();
            } catch(Exception ex) {
                //JSONObject error = new JSONObject();
                //error.put("errorMsg", "There was some error handling the request, please try again.");
                return Response.status(500).entity("Failed to get messages").build();
            }
        });
    }
}

