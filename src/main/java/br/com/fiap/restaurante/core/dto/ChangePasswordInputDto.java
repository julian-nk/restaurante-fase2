package br.com.fiap.restaurante.core.dto;

public record ChangePasswordInputDto(Long appUserId, String currentPassword, String newPassword) {
}
