package me.ohtaeg.securitystudy.controller.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class TokenResponseDto {
    private String token;

    public TokenResponseDto(final String token) {
        this.token = token;
    }
}
