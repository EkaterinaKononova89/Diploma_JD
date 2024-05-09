package ru.netology.Diploma_JD.config;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.netology.Diploma_JD.resolver.JwtRequestParamHandlerMethodArgumentResolver;

import java.util.List;

public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JwtRequestParamHandlerMethodArgumentResolver());
    }
}
