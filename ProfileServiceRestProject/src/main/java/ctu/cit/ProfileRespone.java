package ctu.cit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

@Path("/profiles")
public class ProfileRespone {

    private ProfileResponsitory repository = new ProfileResponsitory();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showProfile(@Context HttpHeaders headers) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || !isValidToken(token)) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Unauthorized access")
                .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Profile existingProfile = repository.getProfileById(profileId);
        if (existingProfile == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Profile not found for ID: " + profileId)
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        if (existingProfile == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Profile not found")
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        JsonObject data = Json.createObjectBuilder()
                .add("id", existingProfile.getId())
                .add("title", existingProfile.getTitle() != null ? existingProfile.getTitle() : "")
                .add("name", existingProfile.getName() != null ? existingProfile.getName() : "")
                .add("phone", existingProfile.getPhone() != null ? existingProfile.getPhone() : "")
                .add("email", existingProfile.getEmail() != null ? existingProfile.getEmail() : "")

                .add("image_url", existingProfile.getImage() != null ? existingProfile.getImage() : "")
                .add("gender", existingProfile.getGender() ? "Female" : "Male") // Chuyển đổi giới tính thành chuỗi Female hoặc Male
                .add("location", existingProfile.getLocation() != null ? existingProfile.getLocation() : "")
                .add("website", existingProfile.getWebsite() != null ? existingProfile.getWebsite(): "")
                .add("birthday", existingProfile.getBirthday() != null ? existingProfile.getBirthday() : "")
                .build();
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("message", true)
                .add("data", data)
                .add("status_code", 200)
                .add("message", "success")
                .build();

        return Response.ok(jsonResponse).build();
    }

    private boolean isValidToken(String token) {
        // Implement your token validation logic here
        return true; // For demonstration purposes, assuming token is always valid
    }

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

    private String extractEmailFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String tokenString = token.substring(7).trim();

            try {
                Jws<Claims> claims = Jwts.parser()
                                         .setSigningKey("your-secret-key")
                                         .parseClaimsJws(tokenString);
                String subject = claims.getBody().getSubject();
                // Split the subject to extract email
                String[] parts = subject.split("_");
                return parts[1]; // Second part is the email
            } catch (JwtException e) {
                // Handle token parsing exception
                e.printStackTrace(); // Or log the exception
                return null; // Return null if unable to extract email
            }
        }
        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProfile(@Context HttpHeaders headers, Profile updatedProfile) {
        String token = headers.getHeaderString("Authorization");

        if (token == null || token.isEmpty()) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Token is missing")
                .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }
        
        int profileId = extractProfileIdFromToken(token);
        if (profileId <= 0) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token: No profile ID found")
                .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

        Profile existingProfile = repository.getProfileById(profileId);
        if (existingProfile == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Profile not found for ID: " + profileId)
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        existingProfile.setName(updatedProfile.getName());
        existingProfile.setTitle(updatedProfile.getTitle());
        existingProfile.setPhone(updatedProfile.getPhone());
        existingProfile.setEmail(updatedProfile.getEmail());
        existingProfile.setImage(updatedProfile.getImage());
        existingProfile.setGender(updatedProfile.getGender());
        existingProfile.setLocation(updatedProfile.getLocation());
        existingProfile.setWebsite(updatedProfile.getWebsite());
        existingProfile.setBirthday(updatedProfile.getBirthday());

        repository.updateProfile(existingProfile);

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("message", "Profile updated successfully")
                .add("profile", Json.createObjectBuilder()
                        .add("id", existingProfile.getId())
                        .add("name", existingProfile.getName())
                        .add("title", existingProfile.getTitle())
                        .add("phone", existingProfile.getPhone())
                        .add("email", existingProfile.getEmail())
                        .add("gender", existingProfile.getGender())
                        .add("location", existingProfile.getLocation())
                        .add("website", existingProfile.getWebsite())
                        .add("birthday", existingProfile.getBirthday()))
                .build();

        return Response.ok().entity(jsonResponse).build();
    }
    
    
    
    private int extractProfileIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String tokenString = token.substring(7).trim();
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey("your-secret-key").parseClaimsJws(tokenString);
                String subject = claims.getBody().getSubject();
                String[] parts = subject.split("_");
                return Integer.parseInt(parts[0]); // First part is the profile ID
            } catch (JwtException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
    
    
    
    
}
