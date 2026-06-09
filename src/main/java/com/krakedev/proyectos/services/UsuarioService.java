package com.krakedev.proyectos.services;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario registrar(Usuario usuario) {
		if (usuarioRepository.existsByUsername(usuario.getUsername())) {
			throw new RuntimeException("El username '" + usuario.getUsername() + "' ya está registrado");
		}

		// Encriptar contraseña con BCrypt:
		String passwordEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
		usuario.setPassword(passwordEncriptada);
		return usuarioRepository.save(usuario);
	}

	// Autentica con BCrypt nada de texto plano con propagacion explicitas:
	public Usuario autenticar(String username, String password) {
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

		if (!BCrypt.checkpw(password, usuario.getPassword())) {
			throw new RuntimeException("Contraseña incorrecta");
		}
		return usuario;
	}

	public Usuario buscarPorUsername(String username) {
		return usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
	}
}