package ma.octo.environment.abstraction.config;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("dev")
@PropertySource("classpath:application-dev.properties")
public class DevConfigProperties {
}