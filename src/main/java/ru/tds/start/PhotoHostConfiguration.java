package ru.tds.start;

import javax.validation.Valid;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.dropwizard.Configuration;

public class PhotoHostConfiguration extends Configuration {
	//not empty - если нет соответствующих значений в 
	//файле конфигурации (*.yml), то приложение не запустится
	
	@Valid
	@JsonProperty
    private ImmutableList<String> grantTypes;
    
	@Valid
	@JsonProperty
    @NotEmpty 
    private String realmPhotoHost;
    
    @JsonProperty
    public ImmutableList<String> getGrantTypes() {
    	return grantTypes;
    }
    
    @JsonProperty
    public String getRealmPhotoHost() {
    	return realmPhotoHost;
    }
}
