package com.lifesup.gbtd.config;

import com.lifesup.gbtd.dto.object.ActionAuditDto;
import com.lifesup.gbtd.model.ActionAuditEntity;
import com.lifesup.gbtd.service.JodaDateService;
import com.lifesup.gbtd.service.inteface.IDateService;
import org.joda.time.DateTimeZone;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class ServerConfig implements WebMvcConfigurer {

    private Environment env;

    public ServerConfig(Environment env) {
        this.env = env;
    }

    @Bean
    IDateService dateService() {
        return new JodaDateService(defaultTimeZone());
    }

    @Bean
    DateTimeZone defaultTimeZone() {
        return DateTimeZone.forID(env.getProperty("timezone"));
    }

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.typeMap(ActionAuditDto.class, ActionAuditEntity.class)
                .addMappings(mapping -> {
                    mapping.map(ActionAuditDto::getUserIP, ActionAuditEntity::setIp);
                });
        return mapper;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("message");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");

        return source;
    }
}
