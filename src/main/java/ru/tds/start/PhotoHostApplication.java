package ru.tds.start;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import ru.tds.auth.PhotoHostAuthFactory;
import ru.tds.auth.PhotoHostAuthenticator;
import ru.tds.start.core.User;
import ru.tds.start.resources.ImageResource;
import ru.tds.start.resources.TokenResource;
import ru.tds.start.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthFactory;

public class PhotoHostApplication extends Application<PhotoHostConfiguration> {
    
    public static void main(final String[] args) throws Exception {
        new PhotoHostApplication().run(args);
    }

    @Override
    public String getName() {
        return "PhotoHost";
    }

    @Override
    public void initialize(final Bootstrap<PhotoHostConfiguration> bootstrap) {
        // for calling html pages in the path \assets\index.html
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(PhotoHostConfiguration configuration,
            Environment environment) throws Exception {
        //environment.jersey().register(new ImageDB(configuration.getDBServer(), configuration.getDBName()));
        
        // to register the resource
        environment.jersey().register(new UserResource());
        environment.jersey().register(new ImageResource());
        environment.jersey().register(new TokenResource());
        environment.jersey().register(MultiPartFeature.class);

        // register authenticate component 
        environment.jersey().register(AuthFactory.binder(new PhotoHostAuthFactory<User>(
                        new PhotoHostAuthenticator(), configuration.getRealmPhotoHost(), User.class)));
    }
}
