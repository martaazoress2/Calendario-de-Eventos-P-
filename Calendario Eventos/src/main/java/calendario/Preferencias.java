package calendario;

import java.io.*;
import java.util.Set;

public class Preferencias implements Serializable{
    private String vistaPreferida; // "Diaria", "Semanal" o "Mensual"
    private Set<String> categoriasFrecuentes; // Ejemplo: "Trabajo", "Ocio", etc.


    public Preferencias(String vistaPreferida, Set<String> categoriasFrecuentes) {
        this.vistaPreferida = vistaPreferida;
        this.categoriasFrecuentes = categoriasFrecuentes;
    }

    public Preferencias() {

    }

    public String getVistaPreferida() {
        return vistaPreferida;
    }

    public void setVistaPreferida(String vistaPreferida) {
        this.vistaPreferida = vistaPreferida;
    }

    public Set<String> getCategoriasFrecuentes() {
        return categoriasFrecuentes;
    }

    public void setCategoriasFrecuentes(Set<String> categoriasFrecuentes) {
        this.categoriasFrecuentes = categoriasFrecuentes;
    }

    public void agregarCategoriaFrecuente(String categoria) {
        categoriasFrecuentes.add(categoria);
    }

    public void guardarPreferencias(String archivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(this);
            System.out.println("Preferencias guardadas correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar las preferencias: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static Preferencias cargarPreferencias(String archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Preferencias) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de preferencias no encontrado. Se usará la configuración predeterminada.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar las preferencias: " + e.getMessage());
        }
        return new Preferencias(); // Devuelve preferencias por defecto si falla la carga
    }
}
