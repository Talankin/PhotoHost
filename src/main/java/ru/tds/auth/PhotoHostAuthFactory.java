package ru.tds.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class PhotoHostAuthFactory<T> extends AuthFactory<String, T> {
    final static Logger logger = LoggerFactory.getLogger(PhotoHostAuthFactory.class);
    private static final String CHALLENGE_FORMAT = "PhotoHost realm = \"%s\"";

    private final boolean required;
    private final Class<T> generatedClass;
    private final String realm;

    @Context
    private HttpServletRequest request;

    public PhotoHostAuthFactory(final Authenticator<String, T> authenticator,
            final String realm, final Class<T> generatedClass) {
        super(authenticator);
        this.required = false;
        this.realm = realm;
        this.generatedClass = generatedClass;
    }

    private PhotoHostAuthFactory(final boolean required,
            final Authenticator<String, T> authenticator, final String realm,
            final Class<T> generatedClass) {
        super(authenticator);
        this.required = required;
        this.realm = realm;
        this.generatedClass = generatedClass;
    }

    @Override
    public AuthFactory<String, T> clone(boolean required) {
        return new PhotoHostAuthFactory<>(required, authenticator(),
                this.realm, this.generatedClass);
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public T provide() {
        if (request != null) {
            // final String header =
            // request.getHeader(HttpHeaders.AUTHORIZATION);
            final Cookie[] cookies = request.getCookies();
            String tokenId = null;
            try {
                if (cookies != null) {
                    // to get cookie
                    if (cookies.length == 1) {
                        for (Cookie cookie : cookies) {
                            tokenId = cookie.getValue();
                        }
                    } else {
                        logger.error("===== out of range of array. It should be only 1 element");
                    }
                    // not clearly. decoding is off 
                    /*
                     * final String decoded = new String (
                     * BaseEncoding.base64().decode(tokenId),
                     * StandardCharsets.UTF_8);
                     */

                    // send the unparsed cookie to the authenticator through method of interface
                    final Optional<T> result = authenticator().authenticate(tokenId);

                    if (result.isPresent()) {
                        return result.get();
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.warn("===== Error decoding credentials ", e);
            } catch (AuthenticationException e) {
                logger.warn("===== Error authenticating token ", e);
                throw new InternalServerErrorException();
            }
        }

        if (required) {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .header(HttpHeaders.WWW_AUTHENTICATE,
                                    String.format(CHALLENGE_FORMAT, realm))
                            .type(MediaType.TEXT_PLAIN_TYPE)
                            .entity("Credentials are required to access this resource. Sign in with login/password")
                            .build());
        }

        return null;
    }

    @Override
    public Class<T> getGeneratedClass() {
        return generatedClass;
    }
}
