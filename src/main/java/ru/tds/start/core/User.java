package ru.tds.start.core;

import org.bson.Document;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @NotEmpty
    @JsonProperty
    private String _id;
    @NotEmpty
    @JsonProperty
    private String login;
    @NotEmpty
    @JsonProperty
    private String password;
    @NotEmpty
    @JsonProperty
    private String fullname;

    public User() {

    }

    public User(String _id, String login, String password, String fullname) {
        this._id = _id;
        this.login = login;
        this.password = password;
        this.fullname = fullname;
    }

    public String get_Id() {
        return _id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String toJson() {
        if (isNull() == true)
            return null;
        Document document = new Document("login", this.getLogin());
        document.append("fullname", this.getFullname());
        return document.toJson();
    }

    public boolean isNull() {
        if (this.get_Id() == null || this.getLogin() == null
                || this.getFullname() == null || this.getPassword() == null)
            return true;
        else
            return false;
    }
}
