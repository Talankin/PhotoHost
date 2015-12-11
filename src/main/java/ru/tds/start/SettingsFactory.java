package ru.tds.start;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.dropwizard.jackson.Discoverable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface SettingsFactory extends Discoverable {

    @JsonProperty
    DBSettings createSettings();
    
}
