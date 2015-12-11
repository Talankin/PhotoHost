package ru.tds.start.db;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import ru.tds.start.core.Token;

public class TokenDB {
    final static Logger logger = LoggerFactory.getLogger(TokenDB.class);
    
    // Tokens store in the Map container 
    private static Map<UUID, Token> tokenTable = new HashMap<>();

    public static Optional<Token> findTokenById(final UUID tokenId) {
        Token token = tokenTable.get(tokenId);

        if (token == null) {
            logger.error("===== tokenId {} not found", tokenId);
            return Optional.absent();
        }
        return Optional.of(token);
    }

    public static Token createToken(final String userId) {
        // to generate token and to put to the token table
        Token token = new Token(UUID.randomUUID(), userId);
        tokenTable.put(token.getTokenId(), token);

        /*
        for (Map.Entry<UUID, Token> entry : tokenTable.entrySet()) {
            System.out.println(entry.getKey() + "  "
                    + entry.getValue().getUserId());
        }*/

        return token;
    }
}
