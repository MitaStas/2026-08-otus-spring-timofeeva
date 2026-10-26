package ru.otus.hw.config;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedMongoConfig {

    @Bean("embeddedMongoVersion")
    public IFeatureAwareVersion embeddedMongoVersion(EmbeddedMongoProperties properties) {
        return new EmbeddedMongoAutoConfiguration().version(properties);
    }
}