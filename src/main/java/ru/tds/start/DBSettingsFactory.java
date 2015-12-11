package ru.tds.start;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("dbsettings")
public class DBSettingsFactory implements SettingsFactory {
    @JsonProperty
    private String dbServer;
    @JsonProperty
    private String dbName;


    @Override
    public DBSettings createSettings() {
        return new DBSettings(dbServer, dbName);
    }
    
}
