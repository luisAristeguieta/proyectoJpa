package com.krakedev.proyectos.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	private static final String CLAVE_SECRETA = "Proyectos2024_SeguridadJWT_ClaveSuperSegura!";
	private static final String EMISOR = "KrakeDevProyectos";
	private static final long TIEMPO_EXPIRACION = 3600000; // 1 hora

	public static String generarToken(String username, String rol) {
		Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);
		long tiempoActual = System.currentTimeMillis();
		Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

		return JWT.create().withIssuer(EMISOR).withSubject(username).withIssuedAt(new Date(tiempoActual))
				.withExpiresAt(fechaExpiracion).withClaim("rol", rol).sign(algoritmo);
	}

	public static DecodedJWT validarToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(CLAVE_SECRETA);
			JWTVerifier verificador = JWT.require(algorithm).withIssuer(EMISOR).build();
			return verificador.verify(token);
		} catch (JWTVerificationException e) {
			logger.error("Error de validación del Token: " + e.getMessage());
			return null;
		}
	}
}