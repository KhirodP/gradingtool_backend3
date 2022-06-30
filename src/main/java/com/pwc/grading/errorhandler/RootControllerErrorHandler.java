package com.pwc.grading.errorhandler;

import java.util.Date;

import javax.inject.Singleton;

import com.pwc.grading.controller.exception.RootControllerException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

/**
 * A class for the exception handling in the controllers.
 *
 */
@Produces
@Singleton
@Requires(classes = { Exception.class, ExceptionHandler.class })
public class RootControllerErrorHandler implements ExceptionHandler<Exception, HttpResponse<Error>> {

	@Override
	public HttpResponse<Error> handle(@SuppressWarnings("rawtypes") HttpRequest request, Exception exception) {
		
		return HttpResponse.status(getResponseCodeStatus(exception)).body(buildErrorResponse(exception));
	}

	/**
	 * Building the error response.
	 */
	private Error buildErrorResponse(Exception ex) {
		Error error = new Error();
		error.setDate(new Date());
		if (ex instanceof RootControllerException) {
			RootControllerException expException = (RootControllerException) ex;
			error.setErrorCode(expException.getErrorCode());
			error.setDeveloperMessage(expException.getDeveloperMessage());
			error.setMessage(expException.getMessage());
		} else {
			error.setErrorCode(500);
			error.setDeveloperMessage("unknow expcetion :: " + ex.getMessage());
			error.setMessage("unknow error ");
		}
		return error;
	}

	/**
	 * Get the status code for the exception
	 */
	private HttpStatus getResponseCodeStatus(Exception ex) {
		int responseCode = 500;

		if (ex instanceof RootControllerException) {
			RootControllerException expException = (RootControllerException) ex;
			responseCode = expException.getErrorCode();
		}

		switch (responseCode) {
		case 200:
			return HttpStatus.OK;

		case 201:
			return HttpStatus.CREATED;
		case 400:
			return HttpStatus.BAD_REQUEST;
		case 401:
			return HttpStatus.UNAUTHORIZED;
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;

	}

}
