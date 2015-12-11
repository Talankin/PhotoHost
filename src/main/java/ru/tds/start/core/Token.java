package ru.tds.start.core;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {
    @NotNull
    @JsonProperty("token_id")
    private UUID tokenId;

    @NotNull
    @JsonProperty("user_id")
    private String userId;

    public Token(UUID tokenId, String userId) {
        this.tokenId = tokenId;
        this.userId = userId;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public String getUserId() {
        return userId;
    }
}
