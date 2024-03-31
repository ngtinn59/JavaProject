package ctu.cit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Path("/awards")
public class AwardsResponse {
    private AwardsRepository repository = new AwardsRepository();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAwardsByToken(@Context HttpHeaders headers) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Invalid profile ID format in token")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Fetch the awards for the profile ID extracted from the token
        List<Awards> awards = repository.getAllAwardsByProfileId(profileId);

        // Convert the list of awards to a JSON array
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Awards award : awards) {
            JsonObject awardJson = Json.createObjectBuilder()
                    .add("id", award.getId())
                    .add("title", award.getTitle())
                    .add("provider", award.getProvider())
                    .add("issueDate", award.getIssueDate())
                    .add("description", award.getDescription())
                    .build();
            jsonArrayBuilder.add(awardJson);
        }

        // Create the final JSON response object
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("data", jsonArrayBuilder.build())
                .build();

        // Return the response with the list of awards
        return Response.ok(jsonResponse).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAward(@Context HttpHeaders headers, Awards newAward) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Unauthorized access")
                .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
        }   
        
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Awards createdAward = repository.insertAward(newAward, profileId);
        if (createdAward == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Failed to create a new award")
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }

        JsonObject awardJson = Json.createObjectBuilder()
            .add("id", createdAward.getId())
            .add("title", createdAward.getTitle())
            .add("provider", createdAward.getProvider())
            .add("issueDate", createdAward.getIssueDate())
            .add("description", createdAward.getDescription())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Award created successfully")
            .add("award", awardJson)
            .build();

        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }
    @GET
    @Path("{aboutmeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAwardById(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
        }   
        
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Awards award = repository.getAwardById(aboutmeId, profileId);
        // Check if the award exists
        if (award == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Award with ID " + aboutmeId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Create the JSON response for the award
        JsonObject awardJson = Json.createObjectBuilder()
            .add("id", award.getId())
            .add("title", award.getTitle())
            .add("provider", award.getProvider())
            .add("issueDate", award.getIssueDate())
            .add("description", award.getDescription())
            .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("message", "Success")
            .add("data", awardJson)
            .add("status_code", 200)
            .build();

        // Return the response with the award details
        return Response.ok(jsonResponse).build();
    }
    
    @DELETE
    @Path("{aboutmeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAward(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Fetch the award by its ID
        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
        }   
        
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Awards existingAward = repository.getAwardById(aboutmeId, profileId);

        // Check if the award exists
        if (existingAward == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Award with ID " + aboutmeId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Perform the deletion in the database
        boolean success = repository.deleteAward(aboutmeId);

        if (success) {
            // Create the JSON response for the deletion confirmation
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Award deleted successfully")
                    .build();

            // Return the response with the deletion confirmation
            return Response.ok(jsonResponse).build();
        } else {
            // Return an internal server error response if the deletion fails
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Failed to delete the award")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }
    }
    
    @PUT
    @Path("{aboutmeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAward(@Context HttpHeaders headers, @PathParam("aboutmeId") int aboutmeId, Awards updatedAward) {
        // Retrieve the token from the Authorization header
        String token = headers.getHeaderString("Authorization");

        // Check if the token is present and valid
        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Unauthorized access")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Extract profile ID from the token
        int profileId = 0;
        try {
            profileId = Integer.parseInt(extractIdFromToken(token));
        } catch (NumberFormatException e) {
            // Handle the exception, maybe log it and/or set a default value
            System.out.println("Error parsing profileId: " + e.getMessage());
            // Depending on your application logic, you might want to throw an exception or handle this case accordingly.
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Invalid profile ID format in token")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Check if the profile ID is valid
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        // Fetch the existing award from the repository
        Awards existingAward = repository.getAwardById(aboutmeId, profileId);

        // Check if the award exists
        if (existingAward == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Award with ID " + aboutmeId + " not found")
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Update the existing award with the new information
        existingAward.setTitle(updatedAward.getTitle());
        existingAward.setProvider(updatedAward.getProvider());
        existingAward.setIssueDate(updatedAward.getIssueDate());
        existingAward.setDescription(updatedAward.getDescription());

        // Perform the update operation in the repository
        boolean success = repository.updateAward(existingAward);

        if (success) {
            // Create the JSON response for the update confirmation
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Award updated successfully")
                    .add("data", Json.createObjectBuilder()
                            .add("id", existingAward.getId())
                            .add("title", existingAward.getTitle())
                            .add("provider", existingAward.getProvider())
                            .add("issueDate", existingAward.getIssueDate())
                            .add("description", existingAward.getDescription())
                            .build())
                    .build();

            // Return the response with the update confirmation
            return Response.ok(jsonResponse).build();
        } else {
            // Return an internal server error response if the update fails
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Failed to update the award")
                    .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonError).build();
        }
    }

    // Other methods can be added similarly

    // Helper methods for token extraction and validation

    private String extractIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String tokenString = token.substring(7).trim();

            try {
                Jws<Claims> claims = Jwts.parser()
                                         .setSigningKey("your-secret-key")
                                         .parseClaimsJws(tokenString);
                String subject = claims.getBody().getSubject();
                // Split the subject to extract ID
                String[] parts = subject.split("_");
                return parts[0]; // First part is the ID
            } catch (JwtException e) {
                // Handle token parsing exception
                e.printStackTrace(); // Or log the exception
                return null; // Return null if unable to extract ID
            }
        }
        return null;
    }

    private boolean isValidToken(String token) {
      // Implement your token validation logic here
      // For demonstration purposes, we'll assume the token is always valid
      return true;
    }
}
