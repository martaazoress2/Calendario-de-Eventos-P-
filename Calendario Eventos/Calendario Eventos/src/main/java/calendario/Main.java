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
            System.out.println("8. Salir");
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
                    System.out.println("Ingrese el nombre del archivo a importar (ejemplo: eventos.csv): ");
                    String nombreArchivoImportar = scanner.nextLine();
                    Calendario.importarEventos(calendario, nombreArchivoImportar);
                    break;
                case 8:
                    System.out.println("Saliendo del calendario...");
                    break;
                default:
                    System.out.println("Opción NO válida");
            }
        } while (opcion != 8);
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

        Evento evento = new Evento(titulo, fechaHora, descripcion, categoria, recordatorioMinutos);
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
        if (eliminado) {
            System.out.println("Evento eliminado: " + eliminarTitulo);
        } else {
            System.out.println("No se encontró un evento con ese título.");
        }
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
}
