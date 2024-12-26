package calendario;

import java.util.ArrayList;
import java.util.List;

public class Calendario {
    private List<Evento> eventos;

    public Calendario() {
        this.eventos = new ArrayList<>();
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
}
