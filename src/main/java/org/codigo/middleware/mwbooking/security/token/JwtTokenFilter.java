package org.codigo.middleware.mwbooking.security.token;

import org.codigo.middleware.mwbooking.commons.enum_.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtTokenParser jwtTokenParser;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var jwtToken = request.getHeader("Authorization");

		if(StringUtils.hasLength(jwtToken) && jwtToken.startsWith("Bearer ")) {
			var authentication = jwtTokenParser.parse(TokenType.Access, jwtToken);

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	
}
