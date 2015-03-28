package dk.jimmikristensen.aaws.config;

import dk.jimmikristensen.aaws.exception.mapper.GeneralExceptionMapper;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

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
		resources.add(GeneralExceptionMapper.class);
	}

}
