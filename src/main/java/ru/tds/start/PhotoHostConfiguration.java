package ru.tds.start;

import javax.validation.Valid;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class PhotoHostConfiguration extends Configuration {
    // not empty - if have not its value in config (*.yml), 
    // then application not run

    @Valid
    @JsonProperty
    @NotEmpty
    private String realmPhotoHost;

    /*@Valid
    @JsonProperty
    @NotEmpty
    private SettingsFactory settings;
    */
    
    @JsonProperty
    public String getRealmPhotoHost() {
        return realmPhotoHost;
    }

    /*@JsonProperty
    public SettingsFactory getSettings() {
        return settings;
    }*/
}
