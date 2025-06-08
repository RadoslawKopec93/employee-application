package employee.application.services;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Service;

import employee.application.model.enums.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public String generateToken(String email, Set<RoleType> roleType) {
        long now = System.currentTimeMillis();
        Key key = Keys.hmacShaKeyFor("mySuperSecretKeyThatIsWayMoreThan64BytesLongForHS512Authentication!".getBytes());

        String token = Jwts.builder()
                .setSubject(email)
                .claim("roles", roleType)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3000000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return token;
    }

    public String generateRefreshToken(String email, Set<RoleType> roleType) {
        Key key = Keys.hmacShaKeyFor("mySuperSecretKeyThatIsAtLeast32BytesLong".getBytes());

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .claim("roles", roleType)
                .setIssuedAt(Date.from(Instant.now().plusMillis(60000)))
                .setExpiration(Date.from(Instant.now().plusMillis(3000000)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
        return refreshToken;
    }

    /*  public void refreshToken(String accessToken, ) */
}
