package calendario;

import javafx.application.Application;
import javafx.scene.Scene;
//import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.awt.*;
import java.awt.desktop.AppEvent;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static calendario.Calendario.importarEventos;

public class Main{
    public static void main(String[] args){
        Calendario calendario = new Calendario();
        Scanner scanner = new Scanner(System.in);

        iniciarRecordatorios(calendario);
        int opcion;

        do {
            System.out.println("\033[1;34m===========================\033[0m");
            System.out.println("    CALENDARIO DE EVENTOS    ");
            System.out.println("\033[1;34m===========================\033[0m");
            System.out.println("1. Crear un evento");
            System.out.println("2. Ver eventos");
            System.out.println("3. Buscar un evento");
            System.out.println("4. Eliminar un evento");
            System.out.println("5. Editar evento");
            System.out.println("6. Exportar eventos");
            System.out.println("7. Importar eventos");
            System.out.println("8. Duplicar evento");
            System.out.println("9. Filtrar eventos por rango de fechas");
            System.out.println("10. Ver estadísticas del calendario");
            System.out.println("11. Marcar evento como completado");
            System.out.println("12. Ordenar eventos");
            System.out.println("13. Crear evento recurrente");
            System.out.println("14. Personalizar notificaciones");
            System.out.println("15. Eliminar eventos pasados");
            System.out.println("16. Exportar calendario completo");
            System.out.println("17. Mostrar ayuda");
            System.out.println("18. Ver eventos completados");
            System.out.println("10. Configurar preferencias");
            System.out.println("7. Importar eventos");
            System.out.println("20. Salir");
            System.out.println("\033[1;34m===========================\033[0m");
            //opcion = leerEntero(scanner, "Seleccione una opción: ");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); //Consumir el salto de línea

            switch (opcion){
                case 1:
                    crearEvento(calendario, scanner);
                    break;
                case 2:
                    verEventos(calendario, scanner);
                    break;
                case 3:
                    buscarEvento(calendario, scanner);
                    break;
                case 4:
                    eliminarEvento(calendario, scanner);
                    break;
                case 5:
                    editarEvento(calendario, scanner);
                    break;
                case 6:
                    System.out.println("Ingrese el nombre del archivo a exportar (ejemplo: eventos.csv): ");
                    String nombreArchivo = scanner.nextLine();
                    calendario.exportarEventos(nombreArchivo);
                    break;
                case 7:
                    System.out.println("Ingrese el nombre del archivo a importar (ruta completa, ejemplo: /Users/tuUsuario/Desktop/eventos.txt): ");
                    String nombreArchivoImportar = scanner.nextLine();
                    Calendario.importarEventos(calendario, nombreArchivoImportar);
                    break;
                case 8:
                    duplicarEvento(calendario, scanner);
                    break;
                case 9:
                    System.out.println("\n=== Filtrar eventos por rango de fechas ===");
                    LocalDateTime inicio = leerFechaHora(scanner, "Ingrese la fecha y hora de inicio (AAAA-MM-DDTHH:MM): ");
                    LocalDateTime fin = leerFechaHora(scanner, "Ingrese la fecha y hora de fin (AAAA-MM-DDTHH:MM): ");

                    if (inicio.isAfter(fin)) {
                        System.out.println("La fecha de inicio no puede ser posterior a la fecha de fin. Inténtelo nuevamente.");
                        break;
                    }

                    List<Evento> eventosFiltrados = calendario.filtrarEventosPorRango(inicio, fin);

                    if (eventosFiltrados.isEmpty()) {
                        System.out.println("No se encontraron eventos en el rango especificado.");
                    } else {
                        System.out.println("\nEventos encontrados:");
                        for (Evento evento : eventosFiltrados) {
                            System.out.println(evento);
                        }
                    }
                    break;
                case 11:
                    System.out.println("Ingrese el título del evento que desea marcar como completado: ");
                    String tituloCompletado = scanner.nextLine();
                    List<Evento> resultados = calendario.buscarEventoPorTitulo(tituloCompletado);
                    if (resultados.isEmpty()) {
                        System.out.println("No se encontró ningún evento con ese título.");
                    } else {
                        Evento evento = resultados.get(0); // Tomamos el primer resultado
                        evento.setCompletado(true); // Marcamos el evento como completado
                        System.out.println("El evento ha sido marcado como completado: " + evento);
                    }
                    break;
                case 18:
                    List<Evento> completados = calendario.getEventos().stream()
                            .filter(Evento::isCompletado)
                            .toList();
                    if (completados.isEmpty()) {
                        System.out.println("No hay eventos completados.");
                    } else {
                        System.out.println("Eventos completados:");
                        completados.forEach(System.out::println);
                    }
                    break;

                case 20:
                    System.out.println("Saliendo del calendario...");
                    break;
                default:
                    System.out.println("Opción NO válida");
            }
        } while (opcion != 20);
        scanner.close();
    }

    private static int leerEntero(Scanner scanner, String mensaje){
        while (true){
            try {
                System.out.println(mensaje);
                return Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Por favor introduzca un número válido");
            }
        }
    }

    private static LocalDateTime leerFechaHora(Scanner scanner, String mensaje){
        while (true){
            try {
                System.out.println(mensaje);
                return LocalDateTime.parse(scanner.nextLine());
            }catch (Exception e){
                System.out.println("Formato de Fecha y Hora incorrecto. Por favor, use el formato AAAA-MM-DDYHH:MM");
            }
        }
    }

    private static void crearEvento(Calendario calendario, Scanner scanner) {
        System.out.println("\nCREAR EVENTO");
        System.out.print("Título del evento: ");
        String titulo = scanner.nextLine();

        LocalDateTime fechaHora = leerFechaHora(scanner, "Fecha y Hora del evento (AAAA-MM-DDTHH:MM): ");

        System.out.print("Descripción del evento: ");
        String descripcion = scanner.nextLine();

        System.out.print("Categoría del evento (Trabajo/Personal/Ocio): ");
        String categoria = scanner.nextLine();

        System.out.println("¿Cuántos minutos antes desea recibir un recordatorio del evento? (Ingrese 0 para desactivar opción): ");
        int recordatorioMinutos = scanner.nextInt();
        scanner.nextLine();

        Evento evento = new Evento(titulo, fechaHora, descripcion, categoria, recordatorioMinutos, false);
        calendario.agregarEvento(evento);

        System.out.println("Evento creado correctamente: " + evento);
    }

    private static void verEventos(Calendario calendario, Scanner scanner) {
        System.out.println("\nSeleccione el tipo de vista: ");
        System.out.println("1. Vista diaria");
        System.out.println("2. Vista semanal");
        System.out.println("3. Vista mensual");
        int vista = leerEntero(scanner, "Seleecione una opción: ");

        switch (vista){
            case 1:
                LocalDate fecha = leerFecha(scanner, "Ingrese la fecha (AAAA-MM-DD): ");
                //List<Evento> eventosDiarios = calendario.getEventosPorDia(fecha);
                mostrarEventos(calendario.getEventosPorDia(fecha), "Vista Diaria - " + fecha);
                break;
            case 2:
                LocalDate fechaSemana = leerFecha(scanner, "Ingrese una fecha de la semana (AAAA-MM-DD): ");
                mostrarEventos(calendario.getEventosPorSemana(fechaSemana), "Vista Semanal - Semana del " + fechaSemana.with(DayOfWeek.MONDAY));
                break;
            case 3:
                int mes = leerEntero(scanner, "Ingrese el mes (1-12): ");
                int anno = leerEntero(scanner, "Ingrese el año: ");
                if (mes<1 || mes > 12){
                    System.out.println("Mes no válido. Debe estar entre 1 y 12");
                    break;
                }
                if (anno<2024){
                    System.out.println("Año no válido, debe ser mayor o igual de 2024");
                    break;
                }
                mostrarEventos(calendario.getEventosPorMes(mes, anno), "Vista Mensual - Mes " + mes + "/" + anno);
                break;
            default:
                System.out.println("Opción no válida");
        }

        /*
        if (calendario.getEventos().isEmpty()) {
            System.out.println("No hay eventos registrados.");
        } else {
            System.out.println("\nEventos:");
            for (Evento e : calendario.getEventos()) {
                System.out.println(e);
            }
        }
         */
    }
    private static void mostrarEventos(List<Evento> eventos, String tituloVista) {
        System.out.println("\n" + tituloVista);
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos para esta vista");
        } else {
            for (Evento evento : eventos){
                System.out.println(evento);
            }
        }
    }

    private static LocalDate leerFecha(Scanner scanner, String mensaje){
        while (true){
            try {
                System.out.println(mensaje);
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e){
                System.out.println("Formato de fecha no válido. Intente de nuevo (AAAA-MM-DD)");
            }
        }
    }

    private static void buscarEvento(Calendario calendario, Scanner scanner) {
        System.out.println("Título del evento a buscar: ");
        String buscarTitulo = scanner.nextLine();

        List<Evento> resultados = calendario.buscarEventoPorTitulo(buscarTitulo);
        if (resultados.isEmpty()){
            System.out.println("No se encontraron eventos con ese título");
        } else {
            System.out.println("\n Resultado de la búsqueda: ");
            for (Evento e : resultados){
                System.out.println(e);
            }
        }
    }

    private static void eliminarEvento(Calendario calendario, Scanner scanner) {
        System.out.println("Ingrese el título del evento a eliminar: ");
        String eliminarTitulo = scanner.nextLine();
        boolean eliminado = calendario.eliminarEvento(eliminarTitulo);
        /*
        if (!eliminado) {
            System.out.println("No se encontró un evento con ese título.");
        }
         */
    }


    private static void editarEvento(Calendario calendario, Scanner scanner){
        System.out.println("Introduce el título del evento que desea editar: ");
        String titulo = scanner.nextLine();

        //Primero hay que buscar el evento para ver si existe o no
        List<Evento> resultados = calendario.buscarEventoPorTitulo(titulo);
        //Si el título ingresado no se encuentra en la lista de eventos:
        if (resultados.isEmpty()){
            System.out.println("No se encontró ningun evento con ese título");
            return;
        }

        //Mostrar el evento si se encuentra
        Evento evento = resultados.get(0); //para usar el primer resultado que aparece
        System.out.println("Evento encontrado: " + evento);

        //Empezamos a editar
        System.out.println("Nuevo título (dejar vacío si no se desea cambiar): ");
        String nuevoTitulo = scanner.nextLine();
        if (!nuevoTitulo.isEmpty()){
            evento.setTitulo(nuevoTitulo);
        }

        System.out.println("Nueva fecha y hora (AAAA-MM-DDTHH:MM) - (dejar en vacío si no se desea cambiar): ");
        String nuevaFecha = scanner.nextLine();
        if (!nuevaFecha.isEmpty()){
            try {
                evento.setFechaHora(LocalDateTime.parse(nuevaFecha));
            } catch (Exception e){
                System.out.println("Formato de fecha y hora no válido. No se realizó el cambio");
            }
        }

        System.out.println("Nueva descripción (dejar vacío si no se desea cambiar): ");
        String nuevaDescripcion = scanner.nextLine();
        if (!nuevaDescripcion.isEmpty()){
            evento.setDescripcion(nuevaDescripcion);
        }

        System.out.println("Nueva categoría (dejar vacío si no se desea cambiar): ");
        String nuevaCategoria = scanner.nextLine();
        if (!nuevaCategoria.isEmpty()){
            evento.setCategoria(nuevaCategoria);
        }

        System.out.println("Evento actualizaco: " + evento);

    }

    private static void iniciarRecordatorios(Calendario calendario){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(( ) -> {
            LocalDateTime ahora = LocalDateTime.now();

            for (Evento evento : calendario.getEventos()){
                LocalDateTime recordatorioHora = evento.getFechaHora().minusMinutes(evento.getRecordatorioMinutos());
                if (recordatorioHora.isBefore(ahora) && evento.getFechaHora().isAfter(ahora)){
                    System.out.println("Ahora: " + ahora);
                    System.out.println("Evento: " + evento.getTitulo() + "Hora del evento: " + evento.getFechaHora());
                    System.out.println("¡¡!! Recordatorio: El evento '" + evento.getTitulo() + "' está programado para " + evento.getFechaHora());
                }
            }
        }, 0, 1, TimeUnit.MINUTES); //Revisa los eventos cada minuto
    }

    private static void duplicarEvento(Calendario calendario, Scanner scanner) {
        System.out.println("\n=== Duplicar Evento ===");
        System.out.println("Ingrese el título del evento que desea duplicar: ");
        String titulo = scanner.nextLine();

        // Buscar el evento por título
        List<Evento> resultados = calendario.buscarEventoPorTitulo(titulo);
        if (resultados.isEmpty()) {
            System.out.println("No se encontró ningún evento con ese título.");
            return;
        }

        // Mostrar el evento encontrado
        Evento eventoOriginal = resultados.get(0);
        System.out.println("Evento encontrado: " + eventoOriginal);

        // Pedir nueva fecha y hora para el duplicado
        System.out.println("Ingrese la nueva fecha y hora para el evento duplicado (AAAA-MM-DDTHH:MM) o presione Enter para mantener la misma:");
        String nuevaFechaHoraInput = scanner.nextLine();
        LocalDateTime nuevaFechaHora = nuevaFechaHoraInput.isEmpty()
                ? eventoOriginal.getFechaHora()
                : LocalDateTime.parse(nuevaFechaHoraInput);

        // Crear el evento duplicado
        Evento eventoDuplicado = new Evento(
                eventoOriginal.getTitulo(), // Usar el mismo título
                nuevaFechaHora, // Usar la nueva fecha y hora
                eventoOriginal.getDescripcion(), // Usar la misma descripción
                eventoOriginal.getCategoria(), // Usar la misma categoría
                eventoOriginal.getRecordatorioMinutos(), // Usar el mismo recordatorio
                false // Nuevo evento no completado
        );

        // Agregar el duplicado al calendario
        calendario.agregarEvento(eventoDuplicado);
        System.out.println("Evento duplicado correctamente: " + eventoDuplicado);
    }


}
