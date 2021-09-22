package com.lifesup.gbtd.exception;

import com.lifesup.gbtd.config.ErrorCode;
import com.lifesup.gbtd.dto.base.BaseResponse;
import com.lifesup.gbtd.dto.response.ActionAuditResponse;
import com.lifesup.gbtd.dto.response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ROOT = "gbtd";

//    @ExceptionHandler(value = {ServerException.class})
//    protected ResponseEntity<Object> handleServerError(ServerException ex, WebRequest request) {
//        logger.error("error: ", ex);
//        BaseResponse res = BaseResponse.exceptionResponse(ex);
//
//        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleError(Exception ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        BaseResponse res = BaseResponse.build(ex);

        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {MissingRequestHeaderException.class})
    protected ResponseEntity<Object> handleAuthorizedError(MissingRequestHeaderException ex, WebRequest request) {
        logger.error("Unexpected error: ", ex);
        ActionAuditResponse res = new ActionAuditResponse();
        res.setErrorCode("1");
        res.setDescription("Access denied");
        return handleExceptionInternal(ex, res, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        if (isApiLog(request)) {
            ActionAuditResponse res = new ActionAuditResponse();
            res.setDescription(errors);
            res.setErrorCode("1");
            return new ResponseEntity<>(res, headers, status);

        }

        return new ResponseEntity<>(BaseResponse.build(ex), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        if (isApiLog(request)) {
            ActionAuditResponse res = new ActionAuditResponse();
            res.setDescription("createDate or userIp malformed");
            res.setErrorCode("1");
            return new ResponseEntity<>(res, headers, status);
        }

        return new ResponseEntity<>(GenericResponse.build(ErrorCode.JSON_WRONG_FORMAT), headers, status);
    }

    private boolean isApiLog(WebRequest rq) {
        String url = ((ServletWebRequest) rq).getRequest().getRequestURI();
        if (url.contains(ROOT)) {
            url = url.replace(ROOT, "").replace("//", "/");
        }
        return "/api/logAction".equals(url);
    }
}
