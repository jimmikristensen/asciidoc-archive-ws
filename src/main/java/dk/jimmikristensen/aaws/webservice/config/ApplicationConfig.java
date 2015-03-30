package dk.jimmikristensen.aaws.webservice.config;

import dk.jimmikristensen.aaws.webservice.exception.mapper.GeneralExceptionMapper;
import dk.jimmikristensen.aaws.webservice.service.AsciidocServiceImpl;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

@ApplicationPath("/")
public class ApplicationConfig extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new java.util.HashSet<>();
		addRestServiceClasses(resources);
		return resources;
	}

	/**
	 * populate with resources needed.
	 */
	private void addRestServiceClasses(Set<Class<?>> resources) {
            resources.add(AsciidocServiceImpl.class);
            resources.add(GeneralExceptionMapper.class);
            resources.add(MultiPartFeature.class);
	}

}
