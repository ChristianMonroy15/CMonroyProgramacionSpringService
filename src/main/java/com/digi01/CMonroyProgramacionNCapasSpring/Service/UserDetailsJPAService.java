package com.digi01.CMonroyProgramacionNCapasSpring.Service;

import com.digi01.CMonroyProgramacionNCapasSpring.DAO.IUsuarioRepositoryDAO;
import com.digi01.CMonroyProgramacionNCapasSpring.JPA.UsuarioJPA;
import com.digi01.CMonroyProgramacionNCapasSpring.Security.CustomUserDetails;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsJPAService implements UserDetailsService {

    private final IUsuarioRepositoryDAO iUsuarioRepositoryDAO;

    public UserDetailsJPAService(IUsuarioRepositoryDAO iUsuarioRepositoryDAO) {
        this.iUsuarioRepositoryDAO = iUsuarioRepositoryDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UsuarioJPA usuario = iUsuarioRepositoryDAO.findByUserName(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        String nombreRol = usuario.getRol().getNombre();

        String springRole = nombreRol;
        //borrar cuando termine
        System.out.println("🔍 loadUserByUsername() llamado con: " + username);
        System.out.println("🔍 Usuario encontrado: " + (usuario != null));
        if (usuario != null) {
            System.out.println("🔍 Rol del usuario: " + usuario.Rol.getNombre());
            System.out.println("🔍 Password (en BD): " + usuario.getPassword());
        }

        List<GrantedAuthority> authorities
                = List.of(new SimpleGrantedAuthority("ROLE_" + springRole));

        return new CustomUserDetails(
                usuario.getIdUsuario(), 
                usuario.getUserName(),
                usuario.getPassword(),
                authorities
        );

    }
}
