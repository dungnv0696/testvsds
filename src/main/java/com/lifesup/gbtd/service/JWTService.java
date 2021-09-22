package com.lifesup.gbtd.service;

import com.google.common.collect.ImmutableMap;
import com.lifesup.gbtd.service.inteface.IDateService;
import com.lifesup.gbtd.service.inteface.IJWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Service
@Slf4j
public class JWTService implements IJWTService, Clock {

    private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();
    private final IDateService dates;
    private final String issuer;
    private final int expirationSec;
    private final int clockSkewSec;
    private final String secretKey;

    @Autowired
    JWTService(final IDateService dates,
               @Value("${jwt.issuer:1}") final String issuer,
               @Value("${jwt.expiration-sec:1}") final int expirationSec,
               @Value("${jwt.clock-skew-sec:1}") final int clockSkewSec,
               @Value("${jwt.secret:1}") final String secret) {
        super();
        this.dates = Objects.requireNonNull(dates);
        this.issuer = Objects.requireNonNull(issuer);
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = TextCodec.BASE64.encode(Objects.requireNonNull(secret));
    }

    private String newToken(final Map<String, String> attributes, final int expiresInSec) {
        final DateTime now = dates.now();
        final Claims claims = Jwts.claims().setIssuer(issuer).setIssuedAt(now.toDate());

        if (expiresInSec > 0) {
            final DateTime expiresAt = now.plusSeconds(expiresInSec);
            claims.setExpiration(expiresAt.toDate());
        }
        claims.putAll(attributes);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compressWith(COMPRESSION_CODEC)
                .compact();
    }

    private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (final Map.Entry<String, Object> e : claims.entrySet()) {
                builder.put(e.getKey(), String.valueOf(e.getValue()));
            }
            return builder.build();
        } catch (final IllegalArgumentException | JwtException e) {
            log.error("parse error", e);
            return ImmutableMap.of();
        }
    }

    @Override
    public String permanent(Map<String, String> attributes) {
        return newToken(attributes, expirationSec);
    }

    @Override
    public String expiring(Map<String, String> attributes) {
        return newToken(attributes, expirationSec);
    }

    @Override
    public Map<String, String> untrusted(String token) {
        final JwtParser parser = Jwts
                .parser()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec);

        final String withoutSignature = StringUtils.substringBeforeLast(token, ".") + ".";
        return parseClaims(() -> parser.parseClaimsJwt(withoutSignature).getBody());
    }

    @Override
    public Map<String, String> verify(String token) {
        final JwtParser parser = Jwts
                .parser()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey);
        return parseClaims(() -> parser.parseClaimsJws(token).getBody());
    }

    @Override
    public Date now() {
        final DateTime now = dates.now();
        return now.toDate();
    }
}
