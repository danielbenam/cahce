package com.danielbenami.cahce.config;

import com.danielbenami.cahce.model.BondDto;
import com.danielbenami.cahce.service.CacheService;
import com.danielbenami.cahce.service.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapConfig {

    @Bean
    public Map<String, BondDto> getMap(){
        return new CacheService<>(16, 0.75F);

    }


}
