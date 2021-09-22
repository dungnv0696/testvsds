package com.lifesup.gbtd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.response.GenericResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (IllegalArgumentException | JwtException e) {
            log.error("authen error", e);
            GenericResponse<Object> res = new GenericResponse<>();
            res.withErrorCode(ErrorCode.AUTHEN_ERROR);

            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpServletResponse.setHeader("Content-type", "application/json; charset=utf-8");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(res));
        }
    }
}
