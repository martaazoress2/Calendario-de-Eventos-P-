package calendario;

import java.util.HashMap;
import java.util.Map;

public class SistemaUsuarios {
    private Map<String, Usuario> usuarios;

    public SistemaUsuarios() {
        this.usuarios = new HashMap<>();
    }

    public boolean registrarUsuario(String nombre, String contraseña) {
        if (usuarios.containsKey(nombre)) {
            return false; // Usuario ya existe
        }
        usuarios.put(nombre, new Usuario(nombre, contraseña));
        return true;
    }

    public Usuario iniciarSesion(String nombre, String contraseña) {
        Usuario usuario = usuarios.get(nombre);
        if (usuario != null && usuario.validarContraseña(contraseña)) {
            return usuario; // Inicio de sesión exitoso
        }
        return null; // Credenciales incorrectas
    }
}
