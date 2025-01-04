package calendario;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                    Evento evento = new Evento(titulo, fechaHora, descripcion, categoria, recordatorioMinutos, false);
                    calendario.agregarEvento(evento);
                }
            }
            System.out.println("Eventos importados correctamente desde el archivo: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al importar los eventos: " + e.getMessage());
        }
    }

    //Filtrar evento por rango de fechas
    public List<Evento> filtrarEventosPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return eventos.stream()
                .filter(evento -> !evento.getFechaHora().isBefore(inicio) && !evento.getFechaHora().isAfter(fin))
                .collect(Collectors.toList());
    }

    public void eliminarEventosPasados() {
        LocalDateTime ahora = LocalDateTime.now();
        int eventosEliminados = (int) eventos.stream()
                .filter(evento -> evento.getFechaHora().isBefore(ahora))
                .count();
        eventos.removeIf(evento -> evento.getFechaHora().isBefore(ahora));
        System.out.println(eventosEliminados + " evento(s) pasado(s) eliminado(s).");
    }

    public void exportarCalendarioCompleto(String nombreArchivoZip) {
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos para exportar.");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(nombreArchivoZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Crear archivo .txt
            ZipEntry txtEntry = new ZipEntry("calendario.txt");
            zos.putNextEntry(txtEntry);
            StringBuilder txtContent = new StringBuilder();
            for (Evento evento : eventos) {
                txtContent.append(evento.toString()).append("\n");
            }
            zos.write(txtContent.toString().getBytes());
            zos.closeEntry();

            // Crear archivo .csv
            ZipEntry csvEntry = new ZipEntry("calendario.csv");
            zos.putNextEntry(csvEntry);
            StringBuilder csvContent = new StringBuilder("Título,Fecha y Hora,Descripción,Categoría,Recordatorio,Completado\n");
            for (Evento evento : eventos) {
                csvContent.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%s\n",
                        evento.getTitulo(),
                        evento.getFechaHora(),
                        evento.getDescripcion(),
                        evento.getCategoria(),
                        evento.getRecordatorioMinutos(),
                        evento.isCompletado() ? "Sí" : "No"));
            }
            zos.write(csvContent.toString().getBytes());
            zos.closeEntry();

            // Crear archivo .json
            ZipEntry jsonEntry = new ZipEntry("calendario.json");
            zos.putNextEntry(jsonEntry);
            StringBuilder jsonContent = new StringBuilder("[\n");
            for (Evento evento : eventos) {
                jsonContent.append(String.format("{\"titulo\":\"%s\",\"fechaHora\":\"%s\",\"descripcion\":\"%s\",\"categoria\":\"%s\",\"recordatorio\":%d,\"completado\":%s},\n",
                        evento.getTitulo(),
                        evento.getFechaHora(),
                        evento.getDescripcion(),
                        evento.getCategoria(),
                        evento.getRecordatorioMinutos(),
                        evento.isCompletado()));
            }
            // Eliminar la última coma y cerrar el JSON
            if (jsonContent.length() > 2) {
                jsonContent.setLength(jsonContent.length() - 2);
            }
            jsonContent.append("\n]");
            zos.write(jsonContent.toString().getBytes());
            zos.closeEntry();

            System.out.println("Calendario exportado correctamente al archivo comprimido: " + nombreArchivoZip);

        } catch (IOException e) {
            System.out.println("Error al exportar el calendario: " + e.getMessage());
        }
    }


}