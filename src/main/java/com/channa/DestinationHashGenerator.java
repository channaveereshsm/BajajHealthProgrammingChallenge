package com.channa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <RollNumber> <PathToJsonFile>");
            return;
        }

        String rollNumber = args[0].toLowerCase();
        String filePath = args[1];

        try {
            // Read and parse the JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(filePath));

            // Find the first instance of "destination"
            String destinationValue = findDestination(rootNode);
            if (destinationValue == null) {
                System.out.println("No destination key found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString();

            // Concatenate the Roll Number, destination value, and random string
            String concatenatedString = rollNumber + destinationValue + randomString;

            // Generate the MD5 hash of the concatenated string
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result in the required format
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String findDestination(JsonNode node) {
        if (node.isObject()) {
            if (node.has("destination")) {
                return node.get("destination").asText();
            }
            for (JsonNode childNode : node) {
                String result = findDestination(childNode);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                String result = findDestination(arrayElement);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            randomString.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
