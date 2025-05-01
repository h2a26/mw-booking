package org.codigo.middleware.mwbooking.security.token;

import org.codigo.middleware.mwbooking.commons.enum_.TokenType;
import org.codigo.middleware.mwbooking.exceptions.ApiJwtTokenExpirationException;
import org.codigo.middleware.mwbooking.exceptions.ApiJwtTokenInvalidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Arrays;

public class JwtTokenParser {
	
	@Value("${app.token.secret}")
	private String secretKeyValue;
	@Value("${app.token.issuer}")
	private String issuer;
	@Value("${app.token.role.key}")
	private String roleKey;
	@Value("${app.token.type.key}")
	private String typeKey;
	
	private SecretKey secretKey;
	
	public void initBean() {
		this.secretKey = SecretKeys.stringToKey(secretKeyValue);
	}
	
	public Authentication parse(TokenType type, String jwtToken) {

		if(StringUtils.hasLength(jwtToken) && jwtToken.startsWith("Bearer ")) {
			var token = jwtToken.replace("Bearer ", "");

			try {
				var jwt = Jwts.parser()
						.requireIssuer(issuer)
						.verifyWith(secretKey)
						.build()
						.parseSignedClaims(token);
				
				var typeValue = jwt.getPayload().get(typeKey);
				
				if(null == typeValue 
						|| null == type 
						|| !type.name().equals(typeValue)) {
					throw new ApiJwtTokenInvalidationException("Invalid Token type");
				}
				
				var username = jwt.getPayload().getSubject();
				var roleString = jwt.getPayload().get(roleKey).toString();
				
				var authorities = Arrays.stream(roleString.split(","))
						.map(a -> new SimpleGrantedAuthority(a)).toList();
				
				return UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);
				
			} catch (ExpiredJwtException e) {
				if(type == TokenType.Access) {
					throw new ApiJwtTokenExpirationException(e.getMessage());
				} else {
					throw new ApiJwtTokenInvalidationException("Expired refresh token", e);
				}
			} catch (JwtException e) {
				throw new ApiJwtTokenInvalidationException("Invalid Token", e);
			}
		}
		
		return null;
	}

}
