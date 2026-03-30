package utils;

import java.util.Base64;

public class PasswordUtil {

    public static String decode(String encodedPassword){
        try{
            byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
            return new String(decodedBytes);
        } catch (Exception e){
            System.err.println("Error while decoding password: " + e.getMessage());
            return "";
        }
    }

    // encode the password into Base64
    public static String encode(String plainPassword){
        return Base64.getEncoder().encodeToString(plainPassword.getBytes());
    }
}
