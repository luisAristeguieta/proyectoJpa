package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioService usuarioService;
	private final TokenBlacklistService blacklistService; // agrega inyeccion y en el constructor

	public AuthController(UsuarioService usuarioService, TokenBlacklistService blacklistService) {
		this.usuarioService = usuarioService;
		this.blacklistService = blacklistService;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario nuevo = usuarioService.registrar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar usuario");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
		String username = credenciales.get("username");
		String password = credenciales.get("password");

		try {
			Usuario usuario = usuarioService.autenticar(username, password);
			String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

			return ResponseEntity
					.ok(Map.of("token", token, "username", usuario.getUsername(), "rol", usuario.getRol()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			blacklistService.invalidarToken(token);
			return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada exitosamente  Token invalidado"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionado");
		}
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String usuario = auth.getName();
		String rol = auth.getAuthorities().iterator().next().getAuthority();
		String rolMostrar = rol.replace("ROLE_", "");

		return ResponseEntity.ok(Map.of("Mensaje", "Bienvenido al sistema protegido por Spring Security", "Usuario",
				usuario, "rol_detectado", rolMostrar, "status", "Autenticado Exitosamente"));
	}

}