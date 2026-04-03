package com.financecontrol.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

/**
 * Catches code blowups so the app returns a error instead of a stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Catches dumb input mistakes and tells the client they messed up.
     *
     * @param ex the error saying what went wrong
     * @return a clean error package with bad request status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setType(URI.create("https://api.financecontrol.com/errors/invalid-argument"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    /**
     * Catches absolutely anything else we forgot to handle.
     *
     * @param ex the wild error that got thrown
     * @return a generic server error string so we hide the real problem
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ex.printStackTrace(); // TEMP: SO WE CAN DEBUG!
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.financecontrol.com/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
