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
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static calendario.Calendario.importarEventos;

public class Main{
    public static void main(String[] args){
        SistemaUsuarios sistemaUsuarios = new SistemaUsuarios();
        Calendario calendario = new Calendario();
        Scanner scanner = new Scanner(System.in);

        // Registro inicial de un usuario
        sistemaUsuarios.registrarUsuario("admin", "admin123");

        Usuario usuarioActual = null;
        Preferencias preferencias = new Preferencias();

        iniciarRecordatorios(calendario);

        // Menú de autenticación
        while (usuarioActual == null) {
            System.out.println("\n=== Bienvenido al Sistema de Calendario ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrar nuevo usuario");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    System.out.print("Nombre de usuario: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Contraseña: ");
                    String contraseña = scanner.nextLine();
                    usuarioActual = sistemaUsuarios.iniciarSesion(nombre, contraseña);
                    if (usuarioActual == null) {
                        System.out.println("Credenciales incorrectas. Intente nuevamente.");
                    } else {
                        System.out.println("Inicio de sesión exitoso. Bienvenido, " + usuarioActual.getNombre() + "!");
                    }
                    break;
                case 2:
                    System.out.print("Ingrese un nombre de usuario: ");
                    String nuevoNombre = scanner.nextLine();
                    System.out.print("Ingrese una contraseña: ");
                    String nuevaContraseña = scanner.nextLine();
                    if (sistemaUsuarios.registrarUsuario(nuevoNombre, nuevaContraseña)) {
                        System.out.println("Usuario registrado exitosamente.");
                    } else {
                        System.out.println("El usuario ya existe. Intente con otro nombre.");
                    }
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }

        int opcion;

        do {
            System.out.println("\033[1;34m===========================\033[0m");
            System.out.println("    CALENDARIO DE EVENTOS    ");
            System.out.println("\033[1;34m===========================\033[0m");
            System.out.println("1. Crear un evento");
            System.out.println("2. Crear Evento Especial");
            System.out.println("3. Editar evento");
            System.out.println("4. Duplicar evento");
            System.out.println("5. Crear evento recurrente");
            System.out.println("6. Ver eventos");
            System.out.println("7. Buscar un evento");
            System.out.println("8. Marcar evento como completado");
            System.out.println("9. Enviar recordatorios de eventos");
            System.out.println("10. Filtrar eventos por rango de fechas");
            System.out.println("11. Ver eventos completados");
            System.out.println("12. Ordenar eventos");
            System.out.println("13. Ver estadísticas del calendario");
            System.out.println("14. Eliminar un evento");
            System.out.println("15. Eliminar eventos pasados");
            System.out.println("16. Personalizar notificaciones");
            System.out.println("17. Importar eventos");
            System.out.println("18. Exportar eventos");
            System.out.println("19. Exportar calendario completo");
            System.out.println("20. Configurar preferencias");
            System.out.println("21. Mostrar ayuda");
            System.out.println("22. Salir");
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
                    crearEventoEspecial(calendario, scanner);
                    break;
                case 3:
                    editarEvento(calendario, scanner);
                    break;
                case 4:
                    duplicarEvento(calendario, scanner);
                    break;
                case 5:
                    crearEventoRecurrente(calendario, scanner);
                    break;
                case 6:
                    verEventos(calendario, scanner);
                    break;
                case 7:
                    buscarEvento(calendario, scanner);
                    break;
                case 8:
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
                case 9:
                    enviarRecordatorios(calendario);
                    break;
                case 10:
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
                case 12:
                    ordenarEventos(calendario, scanner);
                    break;
                case 13:
                    verEstadisticas(calendario);
                    break;
                case 14:
                    eliminarEvento(calendario, scanner);
                    break;
                case 15:
                    System.out.println("\n=== Eliminar Eventos Pasados ===");
                    calendario.eliminarEventosPasados();
                    break;
                case 16:
                    personalizarNotificaciones(calendario, scanner);
                    break;
                case 17:
                    System.out.println("Ingrese el nombre del archivo a importar (ruta completa, ejemplo: /Users/tuUsuario/Desktop/eventos.txt): ");
                    String nombreArchivoImportar = scanner.nextLine();
                    Calendario.importarEventos(calendario, nombreArchivoImportar);
                    break;
                case 18:
                    System.out.println("Ingrese el nombre del archivo a exportar (ejemplo: eventos.csv): ");
                    String nombreArchivo = scanner.nextLine();
                    calendario.exportarEventos(nombreArchivo);
                    break;
                case 19:
                    System.out.println("\n=== Exportar Calendario Completo ===");
                    System.out.print("Ingrese el nombre del archivo .zip (ejemplo: calendario.zip): ");
                    String nombreArchivoZip = scanner.nextLine();
                    calendario.exportarCalendarioCompleto(nombreArchivoZip);
                    break;
                case 20:
                    configurarPreferencias(preferencias, scanner);
                    break;
                case 21:
                    mostrarAyuda();
                    break;
                case 22:
                    System.out.println("Saliendo del calendario...");
                    break;
                default:
                    System.out.println("Opción NO válida");
            }
        } while (opcion != 22);
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
        System.out.println("\n=== Crear Evento ===");
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
        System.out.println("\n=== Ver Eventos ===");
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
        System.out.println("\n=== Mostrar Eventos ===");
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
        System.out.println("\n=== Buscar Evento ===");
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
        System.out.println("\n=== Eliminar Evento ===");
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
        System.out.println("\n=== Editar Evento ===");
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
        System.out.println("\n=== Iniciar un Recordatorio ===");
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

    private static void ordenarEventos(Calendario calendario, Scanner scanner) {
        System.out.println("\n=== Ordenar Eventos ===");
        System.out.println("Seleccione el criterio de ordenación:");
        System.out.println("1. Ordenar por fecha");
        System.out.println("2. Ordenar por categoría");
        int criterio = leerEntero(scanner, "Ingrese su opción:");

        if (criterio == 1) {
            // Ordenar por fecha
            List<Evento> eventosOrdenados = calendario.getEventos().stream()
                    .sorted(Comparator.comparing(Evento::getFechaHora))
                    .collect(Collectors.toList());
            mostrarEventos(eventosOrdenados, "Eventos ordenados por fecha:");
        } else if (criterio == 2) {
            System.out.println("¿Desea priorizar alguna categoría? (Sí/No): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("sí")) {
                System.out.println("Ingrese la categoría a priorizar (Trabajo/Personal/Ocio): ");
                String categoriaPrioritaria = scanner.nextLine().trim();

                // Ordenar con prioridad a una categoría
                List<Evento> eventosOrdenados = calendario.getEventos().stream()
                        .sorted(Comparator.comparing((Evento e) -> !e.getCategoria().equalsIgnoreCase(categoriaPrioritaria))
                                .thenComparing(Evento::getCategoria)
                                .thenComparing(Evento::getFechaHora))
                        .collect(Collectors.toList());

                mostrarEventos(eventosOrdenados, "Eventos ordenados por categoría con prioridad a: " + categoriaPrioritaria);
            } else {
                // Ordenar solo por categoría
                List<Evento> eventosOrdenados = calendario.getEventos().stream()
                        .sorted(Comparator.comparing(Evento::getCategoria).thenComparing(Evento::getFechaHora))
                        .collect(Collectors.toList());

                mostrarEventos(eventosOrdenados, "Eventos ordenados por categoría:");
            }
        } else {
            System.out.println("Criterio no válido. Volviendo al menú principal...");
        }
    }

    private static void crearEventoRecurrente(Calendario calendario, Scanner scanner) {
        System.out.println("\n=== Crear Evento Recurrente ===");
        System.out.print("Título del evento: ");
        String titulo = scanner.nextLine();

        LocalDateTime fechaHora = leerFechaHora(scanner, "Fecha y Hora del evento inicial (AAAA-MM-DDTHH:MM): ");

        System.out.print("Descripción del evento: ");
        String descripcion = scanner.nextLine();

        System.out.print("Categoría del evento (Trabajo/Personal/Ocio): ");
        String categoria = scanner.nextLine();

        System.out.println("¿Cuántos minutos antes desea recibir un recordatorio del evento? (Ingrese 0 para desactivar opción): ");
        int recordatorioMinutos = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        System.out.println("Seleccione la frecuencia de repetición:");
        System.out.println("1. Diaria");
        System.out.println("2. Semanal");
        System.out.println("3. Mensual");
        int frecuencia = leerEntero(scanner, "Ingrese su opción: ");

        int cantidadEventos = leerEntero(scanner, "¿Cuántas veces desea repetir el evento?: ");

        // Crear los eventos recurrentes
        LocalDateTime fechaActual = fechaHora;
        for (int i = 0; i < cantidadEventos; i++) {
            // Crear evento con los valores correctos
            Evento evento = new Evento(
                    titulo + " (Recurrente " + (i + 1) + ")",
                    fechaActual,
                    descripcion,
                    categoria,
                    recordatorioMinutos,
                    false // Asumimos que los eventos recurrentes no están completados inicialmente
            );
            calendario.agregarEvento(evento);

            // Actualizar la fecha según la frecuencia
            switch (frecuencia) {
                case 1: // Diaria
                    fechaActual = fechaActual.plusDays(1);
                    break;
                case 2: // Semanal
                    fechaActual = fechaActual.plusWeeks(1);
                    break;
                case 3: // Mensual
                    fechaActual = fechaActual.plusMonths(1);
                    break;
                default:
                    System.out.println("Frecuencia no válida. No se generaron más eventos.");
                    return;
            }
        }

        System.out.println("Eventos recurrentes creados correctamente.");
    }

    private static void personalizarNotificaciones(Calendario calendario, Scanner scanner) {
        System.out.println("\n=== Personalizar Notificaciones ===");
        System.out.println("1. Cambiar el recordatorio de todos los eventos");
        System.out.println("2. Cambiar el recordatorio de un evento específico");
        System.out.print("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        switch (opcion) {
            case 1:
                System.out.print("Ingrese los minutos antes del evento para todas las notificaciones: ");
                int minutosTodos = scanner.nextInt();
                scanner.nextLine();
                for (Evento evento : calendario.getEventos()) {
                    evento.setRecordatorioMinutos(minutosTodos);
                }
                System.out.println("Notificaciones actualizadas para todos los eventos.");
                break;

            case 2:
                System.out.print("Ingrese el título del evento al que desea cambiar la notificación: ");
                String titulo = scanner.nextLine();
                List<Evento> eventosEncontrados = calendario.buscarEventoPorTitulo(titulo);
                if (eventosEncontrados.isEmpty()) {
                    System.out.println("No se encontró ningún evento con ese título.");
                } else {
                    Evento evento = eventosEncontrados.get(0); // Tomar el primer resultado
                    System.out.println("Evento encontrado: " + evento);
                    System.out.print("Ingrese los minutos antes del evento para la notificación: ");
                    int minutosEspecifico = scanner.nextInt();
                    scanner.nextLine();
                    evento.setRecordatorioMinutos(minutosEspecifico);
                    System.out.println("Notificación actualizada para el evento: " + evento.getTitulo());
                }
                break;

            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void mostrarAyuda() {
        System.out.println("\n=== Ayuda o Instrucciones del Sistema ===");
        System.out.println("1. Crear un evento: Permite añadir un evento nuevo proporcionando título, fecha, descripción, categoría y recordatorio.");
        System.out.println("2. Ver eventos: Muestra los eventos del calendario según vistas diaria, semanal o mensual.");
        System.out.println("3. Buscar un evento: Busca eventos en el calendario por título.");
        System.out.println("4. Eliminar un evento: Elimina un evento existente proporcionando su título exacto.");
        System.out.println("5. Editar evento: Permite modificar los detalles de un evento existente.");
        System.out.println("6. Exportar eventos: Guarda los eventos actuales en un archivo .txt.");
        System.out.println("7. Importar eventos: Carga eventos desde un archivo .txt con formato correcto.");
        System.out.println("8. Duplicar evento: Duplica un evento existente con una nueva fecha.");
        System.out.println("9. Filtrar eventos por rango de fechas: Muestra eventos dentro de un rango de fechas especificado.");
        System.out.println("10. Ver estadísticas del calendario: Muestra estadísticas como la cantidad de eventos por categoría.");
        System.out.println("11. Marcar evento como completado: Marca un evento como 'Completado'.");
        System.out.println("12. Ordenar eventos: Ordena eventos por fecha o categoría.");
        System.out.println("13. Crear evento recurrente: Crea varios eventos que se repiten (diaria, semanal o mensualmente).");
        System.out.println("14. Personalizar notificaciones: Configura recordatorios personalizados para tus eventos.");
        System.out.println("15. Eliminar eventos pasados: Elimina eventos cuya fecha ya haya pasado.");
        System.out.println("16. Exportar calendario completo: Exporta el calendario a múltiples formatos (.txt, .csv, .json) en un archivo comprimido.");
        System.out.println("17. Mostrar ayuda: Muestra estas instrucciones.");
        System.out.println("18. Ver eventos completados: Muestra todos los eventos marcados como completados.");
        System.out.println("20. Salir: Finaliza la ejecución del programa.");
        System.out.println("=========================================");
    }

    private static void configurarPreferencias(Preferencias preferencias, Scanner scanner) {
        System.out.println("\n=== Configurar Preferencias de Usuario ===");
        System.out.println("1. Cambiar vista preferida (Diaria, Semanal, Mensual)");
        System.out.println("2. Agregar una categoría frecuente");
        System.out.println("3. Ver preferencias actuales");
        System.out.println("Seleccione una opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        switch (opcion) {
            case 1:
                System.out.println("Seleccione la nueva vista preferida (Diaria, Semanal, Mensual): ");
                String nuevaVista = scanner.nextLine();
                if (nuevaVista.equalsIgnoreCase("Diaria") || nuevaVista.equalsIgnoreCase("Semanal") || nuevaVista.equalsIgnoreCase("Mensual")) {
                    preferencias.setVistaPreferida(nuevaVista);
                    System.out.println("Vista preferida actualizada a: " + nuevaVista);
                } else {
                    System.out.println("Vista no válida.");
                }
                break;

            case 2:
                System.out.println("Ingrese el nombre de la nueva categoría frecuente: ");
                String nuevaCategoria = scanner.nextLine();
                preferencias.agregarCategoriaFrecuente(nuevaCategoria);
                System.out.println("Categoría agregada: " + nuevaCategoria);
                break;

            case 3:
                System.out.println("\n=== Preferencias Actuales ===");
                System.out.println("Vista preferida: " + preferencias.getVistaPreferida());
                System.out.println("Categorías frecuentes: " + preferencias.getCategoriasFrecuentes());
                break;

            default:
                System.out.println("Opción no válida.");
        }

        // Guardar preferencias después de los cambios
        try {
            preferencias.guardarPreferencias("preferencias.ser");
        } catch (Exception e) {
            System.out.println("Error al guardar las preferencias: " + e.getMessage());
            e.printStackTrace(); // Mostrar detalles del error
        }
    }

    private static void verEstadisticas(Calendario calendario) {
        System.out.println("\n=== Estadísticas del Calendario ===");

        // Número total de eventos
        int totalEventos = calendario.getEventos().size();
        System.out.println("Número total de eventos: " + totalEventos);

        // Número de eventos completados
        long eventosCompletados = calendario.getEventos().stream()
                .filter(Evento::isCompletado)
                .count();
        System.out.println("Número de eventos completados: " + eventosCompletados);

        // Categorías más usadas
        System.out.println("\nCategorías más usadas:");
        calendario.getEventos().stream()
                .collect(Collectors.groupingBy(Evento::getCategoria, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Ordenar de mayor a menor
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " eventos"));

        // Si no hay eventos
        if (totalEventos == 0) {
            System.out.println("\nNo hay eventos en el calendario.");
        }
    }

    private static void crearEventoEspecial(Calendario calendario, Scanner scanner) {
        System.out.println("\n=== Crear Evento Especial1" +
                " ===");
        System.out.print("Título del evento: ");
        String titulo = scanner.nextLine();

        LocalDateTime fechaHora = leerFechaHora(scanner, "Fecha y Hora del evento (AAAA-MM-DDTHH:MM): ");

        System.out.print("Descripción del evento: ");
        String descripcion = scanner.nextLine();

        System.out.print("Categoría del evento (Trabajo/Personal/Ocio): ");
        String categoria = scanner.nextLine();

        System.out.println("¿Cuántos minutos antes desea recibir un recordatorio del evento? (Ingrese 0 para desactivar opción): ");
        int recordatorioMinutos = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        System.out.print("Ubicación del evento: ");
        String ubicacion = scanner.nextLine();

        EventoEspecial eventoEspecial = new EventoEspecial(
                titulo, fechaHora, descripcion, categoria, recordatorioMinutos, false, ubicacion
        );

        calendario.agregarEvento(eventoEspecial);

        System.out.println("Evento especial creado correctamente: " + eventoEspecial);
    }

    private static void enviarRecordatorios(Calendario calendario) {
        System.out.println("\n=== Enviar Recordatorios ===");
        if (calendario.getEventos().isEmpty()) {
            System.out.println("No hay eventos en el calendario.");
        } else {
            for (Evento evento : calendario.getEventos()) {
                evento.enviarRecordatorio();
            }
        }
    }


}
