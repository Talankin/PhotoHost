/**
 *     12.12.2015
 *     Dmitry Talankin
 *     For I P R
 */

package ru.tds.steps;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.assertTrue;

public class RestSteps {
    private static final String CONFIG_FILE = "configuration.properties";
    private static String baseUrl;
    private static Integer port;
    private static String user;
    private static String password;

    private static final String TOKEN_URL = "/token";
    private static final String TOKEN_NAME = "piq-token";

    private String token = "";

    private Response response;

    public RestSteps() throws ConfigurationException{
//        PropertiesConfiguration config = new PropertiesConfiguration(CONFIG_FILE);
//
//        baseUrl = config.getString("base.url");
//        port = config.getInt("base.port");
//        user = config.getString("ldap.user");
//        password = config.getString("ldap.password");
        user = "dima";
        password = "2";
    }


    /**
     * Get token for user.
     */
    @When("^send POST request for getting token$")
    public void getTokenResponse() {
        RestAssured.authentication = basic(user, password);
        response = post(TOKEN_URL);
    }

    /**
     * Verify response status for request
     *
     * @param code
     */
    @Then("^response has code (\\d+)$")
    public void checkResponseStatus(int code) {
        response.then().statusCode(code);
    }

    /**
     * Parse response for get token.
     */
    @Then("^response has valid token$")
    public void getTokenFromResponse() {
        token = "";
        token = from(response.asString()).get(TOKEN_NAME);
        assertTrue("There are no token in response", !token.equals(""));
    }


}
