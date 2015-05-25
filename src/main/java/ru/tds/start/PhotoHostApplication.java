package ru.tds.start;

import java.util.Set;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import ru.tds.auth.PhotoHostAuthFactory;
import ru.tds.auth.PhotoHostAuthenticator;
import ru.tds.start.core.User;
import ru.tds.start.resources.ImageResource;
import ru.tds.start.resources.UserResource;
import ru.tds.start.resources.TokenResource;
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
    	//чтобы вызвать \assets\index.html
        bootstrap.addBundle(new AssetsBundle());
    }

    @Override
    public void run(PhotoHostConfiguration configuration,
                    Environment environment) throws Exception {
    	
    	// регистрируем ресурсы
    	environment.jersey().register(new UserResource());
    	environment.jersey().register(new ImageResource());
        environment.jersey().register(
        		new TokenResource(configuration.getGrantTypes()));
        environment.jersey().register(MultiPartFeature.class);
        
        // регистрируем auth компонент
        environment.jersey().register(
        		AuthFactory.binder(new PhotoHostAuthFactory<User>(new PhotoHostAuthenticator(),
        		configuration.getRealmPhotoHost(),
                User.class)));
        }
    
    //@Override
    /*public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        //resources.add(UploadFileService.class);
        resources.add(MultiPartFeature.class);
        return resources;
     }*/
}
