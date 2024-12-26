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
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Calendario calendario = new Calendario();
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n===========================");
            System.out.println("    CALENDARIO DE EVENTOS    ");
            System.out.println("\n===========================");
            System.out.println("1. Crear un evento");
            System.out.println("2. Ver eventos");
            System.out.println("3. Buscar un evento");
            System.out.println("4. Eliminar un evento");
            System.out.println("5. Salir");
            System.out.println("\n===========================");
            opcion = leerEntero(scanner, "Seleccione una opción: ");
            scanner.nextLine(); //Consumir el salto de línea

            switch (opcion){
                case 1:
                    crearEvento(calendario, scanner);
                    break;
                case 2:
                    verEventos(calendario);
                    break;
                case 3:
                    buscarEvento(calendario, scanner);
                    break;
                case 4:
                    eliminarEvento(calendario, scanner);
                    break;
                case 5:
                    System.out.println("Saliendo del calendario...");
                    break;
                default:
                    System.out.println("Opción NO válida");
            }
        } while (opcion != 5);
        scanner.close();
    }

    private static int leerEntero(Scanner scanner, String mensaje){
        while (true){
            try {
                System.out.println(mensaje);
                return Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("Por favorm introduzca un número válido");
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

        Evento evento = new Evento(titulo, fechaHora, descripcion, categoria);
        calendario.agregarEvento(evento);
    }

    private static void verEventos(Calendario calendario) {
        if (calendario.getEventos().isEmpty()) {
            System.out.println("No hay eventos registrados.");
        } else {
            System.out.println("\nEventos:");
            for (Evento e : calendario.getEventos()) {
                System.out.println(e);
            }
        }
    }

    private static void buscarEvento(Calendario calendario, Scanner scanner) {
        System.out.println("Título del evento a buscar: ");
        String buscarTitulo = scanner.nextLine();
        var resultados = calendario.buscarEventoPorTitulo(buscarTitulo);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron eventos con ese título.");
        } else {
            System.out.println("\nResultados de la búsqueda:");
            for (Evento e : resultados) {
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




/*
    private Calendario calendario = new Calendario();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("CALENDARIO DE EVENTOS");

        //Crear los elementos de la interfaz
        Label titulo = new Label("CALENDARIO DE EVENTOS");
        Button btnCrearEvento = new Button("Crear evento");
        Button btnVerEventos = new Button("Ver eventos");
        Button btnSalir = new Button("Salir");

        //Botón Salir
        btnSalir.setOnAction(e -> primaryStage.close());

        //Acción Ver eventos
        btnVerEventos.setOnAction(e -> mostrarEventos());

        //Acción Crear Evento
        btnCrearEvento.setOnAction(e -> crearEvento());

        //Organizar los elementos en un contenedor
        VBox layout = new VBox(10);//Espacio de 10px
        layout.getChildren().addAll(titulo, btnCrearEvento, btnVerEventos, btnSalir);

        //Crear la escena
        Scene scene = new Scene(layout, 300, 200);

        //Configurar la ventana principal
        primaryStage.setTitle("Calendario de Eventos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void crearEvento() {
        //Abre una ventana nueva para crear un evento
        Stage crearEventoStage = new Stage();
        crearEventoStage.setTitle("Crear Evento");

        Label tituloLabel = new Label("Título:");
        TextField tituloField = new TextField();
        Label fechaLabel = new Label("Fecha y Hora (AAAA-MM-DDTHH:MM):");
        TextField fechaField = new TextField();
        Label descripcionLabel = new Label("Descripción:");
        TextField descripcionField = new TextField();
        Label categoriaLabel = new Label("Categoría:");
        TextField categoriaField = new TextField();

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setOnAction(e -> {
            //Crear nuevo evento
            Evento evento = new Evento(
                    tituloField.getText(),
                    LocalDateTime.parse(fechaField.getText()),
                    descripcionField.getText(),
                    categoriaField.getText()
            );
            calendario.agregarEvento(evento);
            crearEventoStage.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(tituloLabel, tituloField, fechaLabel, fechaField, descripcionLabel, descripcionField, categoriaLabel, categoriaField, btnGuardar);

        Scene scene = new Scene(layout, 400, 400);
        crearEventoStage.setScene(scene);
        crearEventoStage.show();
    }

    private void mostrarEventos() {
        //Abre una ventana nueva con la lista de los eventos
        Stage eventosStage = new Stage();
        eventosStage.setTitle("Eventos");

        //Mostrar los eventos
        TextArea eventosArea = new TextArea();
        for (Evento evento : calendario.getEventos()){
            eventosArea.appendText(evento + "\n");
        }

        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Lista de Eventos"), eventosArea);

        Scene scene = new Scene(layout, 400, 300);
        eventosStage.setScene(scene);
        eventosStage.show();
    }
    public static void main(String[] args) {
        launch(args); // Inicia la aplicación JavaFX
    }

 */
}
