package org.codigo.middleware.mwbooking.exceptions.handler;

import org.codigo.middleware.mwbooking.exceptions.ApiValidationException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;

@Aspect
@Configuration
public class ValidationResultAspect {
	
	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	public void apiMethod() {}

    @Before(value = "apiMethod() && args(.., result)", argNames = "result")
	public void handle(BindingResult result) {

		if(result.hasErrors()) {
			throw new ApiValidationException(result.getFieldErrors()
					.stream()
					.map(a -> a.getDefaultMessage()).toList());
		}
	}
}
