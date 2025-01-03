package calendario;

import java.time.LocalDateTime;

public class Evento {
    private String titulo;
    private LocalDateTime fechaHora;
    private String descripcion;
    private String categoria;
    private int recordatorioMinutos;
    private boolean completado;


    public Evento(String titulo, LocalDateTime fechaHora, String descripcion, String categoria, int RrcordatorioMinutos, boolean completado) {
        this.titulo = titulo;
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.recordatorioMinutos = recordatorioMinutos;
        this.completado = false;
    }

    public Evento(String titulo, LocalDateTime fechaHora, String descripcion, String categoria, int recordatorioMinutos) {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getRecordatorioMinutos() {
        return recordatorioMinutos;
    }

    public void setRecordatorioMinutos(int recordatorioMinutos) {
        this.recordatorioMinutos = recordatorioMinutos;
    }

    public String toString(){
        return "Evento{" +
                "Título='" + titulo + '\'' +
                ", Fecha y Hora=" + fechaHora +
                ", Descripción='" + descripcion + '\'' +
                ", Categoría='" + categoria + '\'' +
                ", Recordatorio=" + recordatorioMinutos + " minutos antes" +
                ", Completado=" + (completado ? "Sí" : "No") +
                '}';
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
