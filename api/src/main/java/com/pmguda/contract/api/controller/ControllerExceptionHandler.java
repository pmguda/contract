package com.pmguda.contract.api.controller;

import com.pmguda.contract.api.common.MessageCode;
import com.pmguda.contract.api.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    //모든 예외를 핸들링하여 ErrorResponse 형식으로 반환
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDTO> handleException(Exception e) {
        log.error("handleException", e);

        ResponseDTO response = ResponseDTO.builder().message(e.getMessage()).build();

        return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
