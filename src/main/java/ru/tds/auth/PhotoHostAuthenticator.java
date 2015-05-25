package ru.tds.auth;

import java.util.UUID;
import ru.tds.start.core.Token;
import ru.tds.start.core.User;
import ru.tds.start.db.UserDB;
import ru.tds.start.db.TokenDB;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public final class PhotoHostAuthenticator 
	implements Authenticator<String, User>{
	
	public Optional<User> authenticate(String tokenId)
		throws AuthenticationException {
		
		// tokenId от клиента должен быть валидным UUID
		UUID tokenUUID;
		try {
			tokenUUID = UUID.fromString(tokenId);
		} catch (IllegalArgumentException e) {
			return Optional.absent();
		}
		
		// берем объект токен из БД
		Optional<Token> token = TokenDB.findTokenById(tokenUUID);
		if (token == null || !token.isPresent()) {
			return Optional.absent();
		}
		
		// берем userId из объекта токен
		String userId = token.get().getUserId();
		
		// берем user по userId
		User user = UserDB.getUserById(userId);
		
		System.out.println("жжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж auethnticator says");
		
		// возвращаем user для дальнейшей работы
		return Optional.of(user);
	}
}
