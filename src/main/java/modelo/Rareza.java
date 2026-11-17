package modelo;

/**
 * Rareza de una carta. Es el UNICO lugar donde se definen las probabilidades
 * de aparicion (expresadas en porcentaje, suman 100).
 */
public enum Rareza {
    NORMAL("N", 50.0),
    POCO_COMUN("UC", 25.0),
    RARO("R", 15.0),
    SUPER_RARO("SR", 7.0),
    ULTRA_RARO("UR", 3.0);

    private final String codigo;
    private final double probabilidad;

    Rareza(String codigo, double probabilidad) {
        this.codigo = codigo;
        this.probabilidad = probabilidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getProbabilidad() {
        return probabilidad;
    }

    /** Rareza inmediatamente inferior, o null si es la mas baja. */
    public Rareza inferior() {
        return ordinal() == 0 ? null : values()[ordinal() - 1];
    }

    /** Rareza inmediatamente superior, o null si es la mas alta. */
    public Rareza superior() {
        Rareza[] todas = values();
        return ordinal() == todas.length - 1 ? null : todas[ordinal() + 1];
    }
}
