package ctu.cit;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mindrot.jbcrypt.BCrypt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/users")
public class UserService {

    private UserResponsitory repository = new UserResponsitory();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(User user) {
        if (repository.getUserByEmail(user.getEmail()) != null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Email already taken")
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonError).build();
        }

     // Mã hóa mật khẩu
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        // Thay đổi mật khẩu của người dùng thành mật khẩu đã được mã hóa
        user.setPassword(hashedPassword);

        // Insert user
        repository.insertUser(user);

        // Retrieve the newly created user
        User newUser = repository.getUserByEmail(user.getEmail());

        // Insert profile
        Profile profile = new Profile();
        profile.setUser(newUser); // Set the user for the profile
        profile.setName(newUser.getName());
        profile.setEmail(newUser.getEmail());
        profile.setImage("default-image-url"); // You can set a default image URL here
        profile.setGender(true); // Assuming true represents male and false represents female
        profile.setLocation("Your default location"); // Set the default location
        profile.setWebsite("Your default website"); // Set the default website

        repository.insertProfile(profile);

        String token = Jwts.builder()
                .setSubject(newUser.getId() + "_" + newUser.getEmail()) // Combine ID and email into one string
                .signWith(SignatureAlgorithm.HS256, "your-secret-key")
                .compact();
        JsonObject userJson = Json.createObjectBuilder()
                .add("name", newUser.getName())
                .add("email", newUser.getEmail())
                .add("id", newUser.getId())
                .build();

        JsonObject jsonResponse = Json.createObjectBuilder()
        		.add("access_token", token)
                .add("token_type", "Bearer")
                .add("user", userJson)
                .add("status_code", 200)
                .add("message", "Registration successful.")
                .build();

        return Response.ok().entity(jsonResponse).build();
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(User user) {
        User existingUser = repository.getUserByEmail(user.getEmail());
        
        if (existingUser == null) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "User not found")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Verify password using BCrypt
        if (!BCrypt.checkpw(user.getPassword(), existingUser.getPassword())) {
            JsonObject jsonError = Json.createObjectBuilder()
                    .add("error", "Invalid password")
                    .build();
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
        }

        // Generate a token
        String token = generateToken(existingUser.getId(), existingUser.getEmail()); // Pass ID and email
        
        // Create JSON response with user ID included
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("success", true)
                .add("access_token", token)
                .add("user", Json.createObjectBuilder()
                        .add("id", existingUser.getId()) // Include user ID
                        .add("name", existingUser.getName())
                        .add("email", existingUser.getEmail())
                        .build())
                .add("status_code", 200)
                .add("token_type", "Bearer")
                .build();

        return Response.ok().entity(jsonResponse).build();
    }



    private static final String SECRET_KEY = "your-secret-key";

    public static String generateToken(long userId, String email) {
        String token = Jwts.builder()
                .setSubject(userId + "_" + email) // Combine ID and email into one string
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
        return token;
    }
    
    
  

        // Existing code

        @DELETE
        @Path("/logout")
        public Response logoutUser(@HeaderParam("Authorization") String token) {
            // You might want to implement token validation logic here
            // Check if the token is valid and exists in your system
            // If your token contains user information (like email), you might need to extract it

            // Example validation
            if (token == null || !token.startsWith("Bearer ")) {
                JsonObject jsonError = Json.createObjectBuilder()
                        .add("error", "Invalid token")
                        .build();
                return Response.status(Response.Status.UNAUTHORIZED).entity(jsonError).build();
            }

            // In a real scenario, you might want to add more validation
            // such as checking if the token is present in a blacklist, etc.

            // If the token is valid, you can consider the user logged out
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("success", true)
                    .add("message", "Logged out successfully")
                    .build();

            return Response.ok().entity(jsonResponse).build();
        }

        // Other methods remain unchanged
    
}
