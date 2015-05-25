package ru.tds.start.db;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.common.base.Optional;
import ru.tds.start.core.Token;

public class TokenDB {
	//Токены храним в Map контейнере
	private static Map<UUID, Token> tokenTable = new HashMap<>(); 
	
	public static Optional<Token> findTokenById(final UUID tokenId) {
		Token token = tokenTable.get(tokenId);
		
		if (token == null) {
			System.out.println("tokenId " + tokenId.toString() + " not found");
			return Optional.absent();
		}
		return Optional.of(token);
	}
	
	public static Token createToken(final String userId) {
		Token token = new Token(UUID.randomUUID(), userId);
		
		System.out.println("===================  создали объект Token " + token.getUserId() + "  " + token.getTokenId().toString());
		tokenTable.put(token.getTokenId(), token);
		
		System.out.println("*************** в таблице есть такие токены ");
		for(Map.Entry<UUID, Token> entry : tokenTable.entrySet()) {
			System.out.println(entry.getKey() + "  " + entry.getValue().getUserId());
		}
		
		return token;
	}
}
