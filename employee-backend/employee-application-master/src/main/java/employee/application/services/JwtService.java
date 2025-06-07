package employee.application.services;

import java.security.Key;
import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Service;

import employee.application.model.enums.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public String generateToken(String subject, Set<RoleType> roleType) {
        long now = System.currentTimeMillis();
        Key key = Keys.hmacShaKeyFor("mySuperSecretKeyThatIsAtLeast32BytesLong".getBytes());

        String token = Jwts.builder()
                .setSubject(subject)
                .claim("roles", roleType)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 10000)) // 10 sekund ważności
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return token;
    }
}
