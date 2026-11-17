package modelo;

/**
 * Clase base abstracta de los usuarios del sistema.
 */
public abstract class Usuario {

    private String id;
    private String usuario;
    private String contrasena;
    private RolUsuario rol;

    protected Usuario(String id, String usuario, String contrasena, RolUsuario rol) {
        if (id == null || id.isBlank() || usuario == null || usuario.isBlank()
                || contrasena == null || contrasena.isBlank() || rol == null) {
            throw new IllegalArgumentException("Datos de usuario invalidos o incompletos");
        }
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    /** Verifica si las credenciales recibidas corresponden a este usuario. */
    public boolean autenticar(String usuario, String contrasena) {
        return this.usuario.equals(usuario) && this.contrasena.equals(contrasena);
    }

    public RolUsuario getRol() {
        return rol;
    }

    public String getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
}
