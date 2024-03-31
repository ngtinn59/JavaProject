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

    private UserResponsitory repository = new UserResponsitory();

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

        String userEmail = extractEmailFromToken(token);

        User user = repository.getUserByEmail(userEmail);

        if (user == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "User not found")
                    .add("message", "User with the provided email does not exist."+userEmail)
                    .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }


        Profile profile = repository.getProfileByUserId(user.getId());

        if (profile == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Profile not found")
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        JsonObject data = Json.createObjectBuilder()
                .add("id", profile.getId())
                .add("title", profile.getTitle() != null ? profile.getTitle() : "")
                .add("name", profile.getName() != null ? profile.getName() : "")
                .add("phone", profile.getPhone() != null ? profile.getPhone() : "")
                .add("image_url", profile.getImage() != null ? profile.getImage() : "")
                .add("gender", profile.getImage() != null ? profile.getGender() : false)
                .add("location", profile.getLocation() != null ? profile.getLocation() : "")
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

    
 // Phương thức POST để cập nhật thông tin profile
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProfile(@Context HttpHeaders headers, Profile updatedProfile) {
        // Lấy token từ header
        String token = headers.getHeaderString("Authorization");

        // Kiểm tra xem token có tồn tại không
        if (token == null || token.isEmpty()) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Token is missing")
                .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Lấy email của người dùng từ token
        String email = extractEmailFromToken(token);

        // Kiểm tra xem email có hợp lệ không
        if (email == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Invalid token")
                .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Kiểm tra xem profile có tồn tại không
        Profile existingProfile = repository.getProfileByEmail(email);

        if (existingProfile == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                .add("error", "Profile not found for email: " + email)
                .build();
            return Response.status(Response.Status.NOT_FOUND).entity(jsonError).build();
        }

        // Cập nhật thông tin profile
        if (updatedProfile.getName() != null) {
            existingProfile.setName(updatedProfile.getName());
        }
        if (updatedProfile.getTitle() != null) {
            existingProfile.setTitle(updatedProfile.getTitle());
        }
        // Handle other fields similarly

        // Lưu profile được cập nhật vào cơ sở dữ liệu
        repository.updateProfile(existingProfile);

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("message", "Profile updated successfully")
                .add("profile", Json.createObjectBuilder()
                        .add("id", existingProfile.getId())
                        .add("name", existingProfile.getName())
                        .add("title", existingProfile.getTitle())
                        // Add other profile fields as needed
                )
                .build();

        return Response.ok().entity(jsonResponse).build();
    }
    // Hàm lấy ID của người dùng từ token
//    private int getUserIdFromToken(String token) {
//        if (token == null || !token.startsWith("Bearer ")) {
//            System.out.println("Token is null or does not start with 'Bearer '");
//            return -1;
//        }
//
//        String tokenString = token.substring(7).trim();
//
//        try {
//            Jws<Claims> claims = Jwts.parser()
//                                     .setSigningKey("your-secret-key")
//                                     .parseClaimsJws(tokenString);
//            String userIdStr = claims.getBody().getSubject();
//            return Integer.parseInt(userIdStr);
//        } catch (JwtException e) {
//            System.out.println("JWT parsing failed: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            System.out.println("Failed to parse user ID from token: " + e.getMessage());
//        }
//
//        return -1;
//    }
//    


    
    
}
