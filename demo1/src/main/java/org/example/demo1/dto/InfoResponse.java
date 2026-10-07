package org.example.demo1.dto;

public record InfoResponse(
    String name,
    String version,
    String environment,
    String developer,
    String email
) {
}
