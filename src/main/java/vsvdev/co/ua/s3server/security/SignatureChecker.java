package vsvdev.co.ua.s3server.security;

import jakarta.servlet.http.HttpServletRequest;
import vsvdev.co.ua.s3server.config.AWSConfig;

public class SignatureChecker {

    private static void getSignature(String input, String signature) {
        int signatureIndex = input.indexOf("Signature=");

        if (signatureIndex != -1) {
            signature = input.substring(signatureIndex + 10);
        } else {
            System.out.println("Error: String does not contain 'Signature='");
        }
    }

    private static String getCred(String input) {
        int signatureIndex = input.indexOf("Credential=");

        if (signatureIndex == -1) {
            return "";
        }
        String credential = input.substring(signatureIndex + 11);
        int only = credential.indexOf("SignedHeaders=");
        String onlyCred = credential.substring(0, only);
        int dash = onlyCred.indexOf("/");

        return onlyCred.substring(0, dash);

    }

    public static boolean validRequest(HttpServletRequest req, AWSConfig config) {
        String authorization = req.getHeader("authorization");
        String signature = "";
        getSignature(authorization, signature);
        String cred = getCred(authorization);

        if (!cred.equals(config.getAccessKey())) {
            return false;
        }
        if (!authorization.contains(config.getRegion())) {
            return false;
        }
        return authorization.contains("AWS4-HMAC-SHA256");
    }
}
