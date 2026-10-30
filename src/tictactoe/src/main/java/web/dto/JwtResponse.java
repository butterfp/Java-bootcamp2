package web.dto;

public record JwtResponse(String type, String accessToken, String refreshToken) {}