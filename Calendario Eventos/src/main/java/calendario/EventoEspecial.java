package calendario;

public class EventoEspecial extends Evento{
    private String ubicacion;

    public EventoEspecial(String titulo,
                          java.time.LocalDateTime fechaHora,
                          String descripcion,
                          String categoria,
                          int recordatorioMinutos,
                          boolean completado,
                          String ubicacion) {
        super(titulo, fechaHora, descripcion, categoria, recordatorioMinutos, completado);
        this.ubicacion = ubicacion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return super.toString() + ", Ubicación='" + ubicacion + "'";
    }

}
