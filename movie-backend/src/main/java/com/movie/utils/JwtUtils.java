package com.movie.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtils {

    private static final String SECRET = "4k-movie-secret-key-2024-graduation-project";
    private static final long EXPIRE = 7 * 24 * 3600 * 1000L; // 7天

    public static String generateToken(Long userId, String username, Integer role) {
        long now = System.currentTimeMillis();
        long exp = now + EXPIRE;
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"userId\":" + userId + ",\"username\":\"" + username + "\",\"role\":" + role + ",\"iat\":" + now + ",\"exp\":" + exp + "}").getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha256(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public static Long getUserId(String token) {
        validateToken(token);
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String userIdStr = extractJsonValue(payload, "userId");
        return Long.parseLong(userIdStr);
    }

    public static String getUsername(String token) {
        validateToken(token);
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return extractJsonValue(payload, "username");
    }

    public static Integer getRole(String token) {
        validateToken(token);
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String roleStr = extractJsonValue(payload, "role");
        return Integer.parseInt(roleStr);
    }

    public static void validateToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new RuntimeException("无效token");
        String expectedSignature = hmacSha256(parts[0] + "." + parts[1]);
        if (!expectedSignature.equals(parts[2])) throw new RuntimeException("token签名错误");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String expStr = extractJsonValue(payload, "exp");
        long exp = Long.parseLong(expStr);
        if (System.currentTimeMillis() > exp) throw new RuntimeException("token已过期");
    }

    private static String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("签名失败", e);
        }
    }

    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) throw new RuntimeException("key not found: " + key);
        start += searchKey.length();
        // skip whitespace
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (json.charAt(start) == '"') {
            // string value
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            // number value
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }
}
