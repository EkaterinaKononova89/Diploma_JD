package ru.netology.Diploma_JD.handler;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.Diploma_JD.dto.Response;
import ru.netology.Diploma_JD.error.*;


@CommonsLog
@Controller
public class ExceptionController {

    @ExceptionHandler(CloudBadCredentials.class)
    public ResponseEntity<Response> cloudBadCredentialsHandler(CloudBadCredentials ex) {
        log.error(ex.getMessage());
        Response response = new Response(ex.getMessage(), ex.getId());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(UnauthorizedError.class)
    public ResponseEntity<Response> unauthorizedErrorHandler(UnauthorizedError ex) {
        log.error(ex.getMessage());
        Response response = new Response(ex.getMessage(), ex.getId());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
    }

    @ExceptionHandler(ErrorInputData.class)
    public ResponseEntity<Response> errorInputDataHandler(ErrorInputData ex) {
        log.error(ex.getMessage());
        Response response = new Response(ex.getMessage(), ex.getId());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(ErrorDeleteFile.class)
    public ResponseEntity<Response> errorDeleteFileHandler(ErrorDeleteFile ex) {
        log.error(ex.getMessage());
        Response response = new Response(ex.getMessage(), ex.getId());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    @ExceptionHandler(ErrorUploadFile.class)
    public ResponseEntity<Response> errorUploadFileHandler(ErrorUploadFile ex) {
        log.error(ex.getMessage());
        Response response = new Response(ex.getMessage(), ex.getId());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}