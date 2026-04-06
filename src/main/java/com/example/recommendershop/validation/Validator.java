package com.example.recommendershop.validation;

import com.example.recommendershop.exception.MasterException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class Validator {

    public <T> T checkEntityNotExists(Optional<T> entity, HttpStatus status, String message) {
        return entity.orElseThrow(() -> new MasterException(status, message));
    }
    public void checkEntityExists(Object entity, HttpStatus status, String message) {
        if (entity != null) {
            throw new MasterException(status, message);
        }
    }

}