package com.castores.inventario.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.castores.inventario.entity.Usuario;
import com.castores.inventario.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con el correo: " + correo));

        String authority = "ROLE_" + usuario.getRol().getNombre();

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena())
                .disabled(!usuario.isEstatus())
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .build();
    }
}
