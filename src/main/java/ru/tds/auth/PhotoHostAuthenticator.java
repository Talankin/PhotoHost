package ru.tds.auth;

import java.util.UUID;
import ru.tds.start.core.Token;
import ru.tds.start.core.User;
import ru.tds.start.db.UserDB;
import ru.tds.start.db.TokenDB;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public final class PhotoHostAuthenticator implements
        Authenticator<String, User> {

    public Optional<User> authenticate(String tokenId)
            throws AuthenticationException {

        // check the UUID validation of the tokenId 
        UUID tokenUUID;
        try {
            tokenUUID = UUID.fromString(tokenId);
        } catch (IllegalArgumentException e) {
            return Optional.absent();
        }

        // to get token object from database 
        Optional<Token> token = TokenDB.findTokenById(tokenUUID);
        if (token == null || !token.isPresent()) {
            return Optional.absent();
        }

        // to get userId from the token object 
        String userId = token.get().getUserId();

        // to get user object by userId
        User user = UserDB.getUserById(userId);

        // return the user for working 
        return Optional.of(user);
    }
}
