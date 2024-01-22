package com.tomavelev.payment.model.response;

public record LoginResponse(String token, String role, BusinessCode code) {
}

