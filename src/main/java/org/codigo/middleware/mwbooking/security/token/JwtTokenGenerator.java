package org.codigo.middleware.mwbooking.security.token;

import org.codigo.middleware.mwbooking.commons.enum_.TokenType;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtTokenGenerator {

	@Value("${app.token.secret}")
	private String secretKeyValue;
	
	@Value("${app.token.issuer}")
	private String issuer;
	
	@Value("${app.token.role.key}")
	private String roleKey;

	@Value("${app.token.type.key}")
	private String typeKey;
	
	@Value("${app.token.expiration.refresh}")
	private int refreshLife;

	@Value("${app.token.expiration.access}")
	private int accessLife;
	
	private SecretKey secretKey;
	
	public void initBean() {
		this.secretKey = SecretKeys.stringToKey(secretKeyValue);
	}

	public String generate(TokenType type, Authentication auth) {
		
		var roles = auth.getAuthorities().stream()
				.map(a -> a.getAuthority())
				.collect(Collectors.joining(","));
		
		var issueAt = new Date();
		var life = (type == TokenType.Access) ? accessLife : refreshLife;
		var calendar = Calendar.getInstance();
		calendar.setTime(issueAt);
		calendar.add(Calendar.MINUTE, life);
		
		var expiredAt = calendar.getTime();
		
		return Jwts.builder()
				.subject(auth.getName())
				.issuer(issuer)
				.issuedAt(issueAt)
				.expiration(expiredAt)
				.signWith(secretKey)
				.claim(roleKey, roles)
				.claim(typeKey, type.name())
				.compact();
	}
}
