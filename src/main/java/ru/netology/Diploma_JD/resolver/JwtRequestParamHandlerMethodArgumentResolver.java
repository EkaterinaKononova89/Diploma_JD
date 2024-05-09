package ru.netology.Diploma_JD.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.netology.Diploma_JD.dto.JwtRequest;

public class JwtRequestParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    // Определяем, поддерживается ли тип параметра для преобразования
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JwtRequestParam.class); //Возвращает, объявлен ли параметр с заданным типом аннотации.
    }

    // Если поддерживается, выполнить соответствующее преобразование
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String login = webRequest.getParameter("login");
        String password = webRequest.getParameter("password");

        return new JwtRequest(login, password);
    }
}
