package calendario;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Preferencias implements Serializable {
    private String vistaPreferida; // "Diaria", "Semanal" o "Mensual"
    private Set<String> categoriasFrecuentes; // Ejemplo: "Trabajo", "Ocio", etc.
    //private Set<String> categoriasFrecuentes = new HashSet<>();


    public Preferencias(String vistaPreferida, Set<String> categoriasFrecuentes) {
        this.vistaPreferida = vistaPreferida; // Permitir pasar valores personalizados
        //this.categoriasFrecuentes = new HashSet<>(); //Inicializa el conjunto
        this.categoriasFrecuentes = categoriasFrecuentes != null ? categoriasFrecuentes : new HashSet<>();
    }

    public Preferencias() {
        this.vistaPreferida = "Diaria"; // Valor predeterminado
        this.categoriasFrecuentes = new HashSet<>(); // Inicializar el conjunto
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
        //categoriasFrecuentes.add(categoria);
        if (categoria != null && !categoria.isEmpty()) {
            categoriasFrecuentes.add(categoria);
        } else {
            System.out.println("La categoría no puede estar vacía.");
        }

    }

    public void guardarPreferencias(String archivo) throws IOException {
        /*
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(this);
            System.out.println("Preferencias guardadas correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar las preferencias: " + e.getMessage());
            e.printStackTrace();
        }


        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(archivo))) {
            oos.writeObject(this);
        }

         */
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(this);
            System.out.println("Preferencias guardadas correctamente.");
        }


    }


    public static Preferencias cargarPreferencias(String archivo) {
        /*
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Preferencias) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de preferencias no encontrado. Se usará la configuración predeterminada.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar las preferencias: " + e.getMessage());
        }
        return new Preferencias(); // Devuelve preferencias por defecto si falla la carga
    }


        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(archivo))) {
            return (Preferencias) ois.readObject();
        }

         */
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
