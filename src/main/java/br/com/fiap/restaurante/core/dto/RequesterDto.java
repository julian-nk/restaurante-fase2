package br.com.fiap.restaurante.core.dto;

/**
 * Quem está chamando o caso de uso: o ID da pessoa do usuário logado e se ele é administrador.
 */
public record RequesterDto(Long personId, boolean admin) {
}
