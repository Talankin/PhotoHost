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
	
	/*
	@NotNull
	@JsonProperty("time_last_access")
	private DateTime timeLastAccess;
	*/
	
	public Token(UUID tokenId, String userId) {
		this.tokenId = tokenId;
		this.userId = userId;
		//this.timeLastAccess = timeLastAccess; 
	}
	
	public UUID getTokenId(){
		return tokenId;
	}
	
	public String getUserId(){
		return userId;
	}
	
	/*
	public DateTime getTimeLastAccess(){
		return timeLastAccess;
	}
	*/
}
