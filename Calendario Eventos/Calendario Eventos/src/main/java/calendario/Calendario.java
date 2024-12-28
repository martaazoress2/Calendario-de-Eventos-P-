package calendario;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calendario {
    private List<Evento> eventos;

    public Calendario() {
        this.eventos = new ArrayList<>();
    }

    //Filtramos los eventos por día/semana/mes
    public List<Evento> getEventosPorDia(LocalDate fecha){
        return eventos.stream()
                .filter(evento -> evento.getFechaHora().toLocalDate().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Evento> getEventosPorSemana(LocalDate fecha){
        return eventos.stream()
                .filter(evento -> {
                    LocalDate fechaEvento = evento.getFechaHora().toLocalDate();
                    return fechaEvento.isAfter(fecha.minusDays(fecha.getDayOfWeek().getValue()-1)) &&
                            fechaEvento.isBefore(fecha.plusDays(7 - fecha.getDayOfWeek().getValue()));
                })
                .collect(Collectors.toList());
    }

    public List<Evento> getEventosPorMes(int mes, int anno){
        return eventos.stream()
                .filter(evento -> {
                    LocalDate fechaEvento = evento.getFechaHora().toLocalDate();
                    return fechaEvento.getMonthValue() == mes && fechaEvento.getYear() == anno;
                })
                .collect(Collectors.toList());
    }

    //Agregar un Evento
    public void agregarEvento(Evento evento){
        eventos.add(evento);
        System.out.println("Evento añadido: " + evento.getTitulo());
    }

    //Eliminar un Evento
    public boolean eliminarEvento(String titulo){
        eventos.removeIf(evento -> evento.getTitulo().equalsIgnoreCase(titulo));
        System.out.println("Evento eliminado: " + titulo);
        return false;
    }

    //Buscar un Evento
    public List<Evento> buscarEventoPorTitulo(String titulo){
        List<Evento> resultados = new ArrayList<>();
        for (Evento evento : eventos){
            if (evento.getTitulo().toLowerCase().contains(titulo.toLowerCase())){
                resultados.add(evento);
            }
        }
        return resultados;
    }

    public List<Evento> getEventos(){
        return eventos;
    }

    public void exportarEventos(String nombreArchivo){
        if (eventos.isEmpty()){
            System.out.println("No hay eventos para exportar");
            return;
        }
        System.out.println("Eventos a exportar: ");
        for (Evento evento : eventos){
            System.out.println(evento);
        }

        //Ruta de la carpeta Descargas donde se guardará el archivo
        //String rutaDescargas = System.getProperty("user.home") + "/Downloads" + nombreArchivo;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            System.out.println("\033[1;34m===========================\033[0m");
            System.out.println("    CALENDARIO DE EVENTOS    ");
            System.out.println("\033[1;34m===========================\033[0m");

            for (Evento evento : eventos){
                writer.write("Título: " + evento.getTitulo() + "\n");
                writer.write("Fecha y Hora: " + evento.getFechaHora() + "\n");
                writer.write("Descripción: " + evento.getDescripcion() + "\n");
                writer.write("Categoría: " + evento.getCategoria() + "\n");
                writer.write("Recordatorio: " + evento.getRecordatorioMinutos() + " minutos antes: "+ "\n");
                System.out.println("\033[1;34m===========================\033[0m");
            }

            System.out.println("Eventos exportados correctamente al archivo: " + nombreArchivo);
        } catch (IOException e) {
            //System.out.println("Error al exportar los eventos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void importarEventos(Calendario calendario, String nombreArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("Título:")) {
                    String titulo = linea.split(":")[1].trim();

                    // Leer y validar Fecha y Hora
                    String fechaHoraLinea = reader.readLine();
                    LocalDateTime fechaHora;
                    try {
                        fechaHora = LocalDateTime.parse(fechaHoraLinea.split(":")[1].trim());
                    } catch (Exception e) {
                        System.out.println("Error al procesar la línea de Fecha y Hora: " + fechaHoraLinea + " - " + e.getMessage());
                        continue; // Salta este evento y continúa con el siguiente
                    }

                    // Leer Descripción
                    String descripcionLinea = reader.readLine();
                    String descripcion = descripcionLinea.split(":")[1].trim();

                    // Leer Categoría
                    String categoriaLinea = reader.readLine();
                    String categoria = categoriaLinea.split(":")[1].trim();

                    // Leer Recordatorio
                    String recordatorioLinea = reader.readLine();
                    int recordatorioMinutos;
                    try {
                        recordatorioMinutos = Integer.parseInt(recordatorioLinea.split(" ")[1].trim());
                    } catch (Exception e) {
                        System.out.println("Error al procesar el recordatorio: " + recordatorioLinea + " - " + e.getMessage());
                        continue; // Salta este evento y continúa con el siguiente
                    }

                    // Crear el evento y agregarlo al calendario
                    Evento evento = new Evento(titulo, fechaHora, descripcion, categoria, recordatorioMinutos);
                    calendario.agregarEvento(evento);
                }
            }
            System.out.println("Eventos importados correctamente desde el archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al importar los eventos: " + e.getMessage());
        }
        }
    }
