package com.miapp.controlador;

import com.miapp.modelo.Estudiante;
import com.miapp.vista.EstudianteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * Controlador: gestiona la lógica entre la Vista y el Modelo.
 * Contiene el array de estudiantes y responde a las búsquedas.
 *
 * IMPORTANTE (MVC): el Controlador es el ÚNICO que conoce tanto la Vista
 * como el Modelo. Es el responsable de traducir objetos Estudiante
 * (Modelo) a Object[] / List<Object[]> (datos "neutros") antes de
 * entregárselos a la Vista. La Vista nunca recibe ni conoce la clase
 * Estudiante directamente.
 */
public class EstudianteController {

    // ── Vista ─────────────────────────────────────────────────────────────────
    private EstudianteView vista;
    private int cantidadEstudiantes;
    private boolean tipoAscendente = true;

    // ── Array de estudiantes (fuente de datos) ────────────────────────────────
    private ArrayList<Estudiante> estudiantes;
    private List<Estudiante> ultimosResultados;

    // ── Constructor ───────────────────────────────────────────────────────────

    public EstudianteController(EstudianteView vista) {
        this.vista = vista;
        this.vista.setControlador(this);
        cargarDatos();
    }
    
    // ── Obtener información ───────────────────────────────────────────────────────────
   
    public boolean puedeOrdenar(){
        return ultimosResultados != null && ultimosResultados.size() > 1;
    }
    
    public Boolean getTipoAscendente(){
        return tipoAscendente;
    }

    // ── Carga de datos iniciales ──────────────────────────────────────────────

    /**
     * Inicializa el array de estudiantes con datos de ejemplo.
     * En un proyecto real este array vendría de una base de datos o servicio.
     */
    private void cargarDatos() {
        this.estudiantes = new ArrayList();
        this.ultimosResultados = new ArrayList();
        
        estudiantes.add(new Estudiante(1,  "Ana García",        "Ingeniería de Sistemas", 4.5));
        estudiantes.add(new Estudiante(2,  "Carlos López",      "Ingeniería Civil",       3.8));
        estudiantes.add(new Estudiante(3,  "María Rodríguez",   "Medicina",               4.9));
        estudiantes.add(new Estudiante(4,  "José Martínez",     "Derecho",                3.5));
        estudiantes.add(new Estudiante(5,  "Laura Sánchez",     "Administración",         4.1));
        estudiantes.add(new Estudiante(6,  "Andrés Torres",     "Ingeniería de Sistemas", 3.9));
        estudiantes.add(new Estudiante(7,  "Valentina Gómez",   "Psicología",             4.3));
        estudiantes.add(new Estudiante(8,  "Luis Herrera",      "Economía",               3.7));
        estudiantes.add(new Estudiante(9,  "Sofía Díaz",        "Ingeniería Civil",       4.6));
        estudiantes.add(new Estudiante(10, "Juliana Morales",   "Medicina",               4.8));
        estudiantes.add(new Estudiante(11, "Ana Milena Ruiz",   "Derecho",                4.0));
        estudiantes.add(new Estudiante(12, "Carlos Andrés Paz", "Administración",         3.6));
        this.cantidadEstudiantes = 12;
    }
    
    // ── Lógica de búsqueda ────────────────────────────────────────────────────

    /**
     * Busca estudiantes cuyo nombre contenga el criterio (sin distinción de mayúsculas).
     * Luego llama a vista.mostrarEstudiante(fila) para una coincidencia,
     * o a vista.mostrarEstudiantes(filas) cuando hay varias.
     *
     * @param criterio texto ingresado por el usuario en la Vista
     */
    public void buscarEstudiante(String criterio) {

        // Validación básica
        if (criterio == null || criterio.isEmpty()) {
            vista.mostrarError("Por favor ingrese un nombre para buscar.");
            return;
        }

        List<Estudiante> resultados = new ArrayList<>();
        String criterioBajo = criterio.toLowerCase();

        for (Estudiante e : estudiantes) {
            if (e.getNombre().toLowerCase().contains(criterioBajo)) {
                resultados.add(e);
            }
        }

        if (resultados.isEmpty()) {
            vista.mostrarEstudiantes(new ArrayList<>()); // mostrará mensaje vacío
        } else if (resultados.size() == 1) {
            // Un solo resultado: se convierte a fila y se usa vista.mostrarEstudiante(fila)
            vista.mostrarEstudiante(convertirAFila(resultados.get(0)));
            this.ultimosResultados = resultados;
        } else {
            // Varios resultados: se convierte toda la lista antes de enviarla a la Vista
            vista.mostrarEstudiantes(convertirAFilas(resultados));
            this.ultimosResultados = resultados;
        }
    }
    
    public boolean validaciones(String nombre, String carrera, String strPromedio){
        if (nombre == null || nombre.isEmpty()) {
                vista.mostrarError("Por favor ingrese un nombre.");
                return false;
        }
        else if(carrera == null || carrera.isEmpty()) {
                vista.mostrarError("Por favor ingrese una carrera.");
                return false;
        }
        else if(strPromedio == null || strPromedio.isEmpty()) {
                vista.mostrarError("Por favor ingrese un promedio.");
                return false;
        } 
        else if (carrera.matches(".*\\d.*") || nombre.matches(".*\\d.*")) {
                vista.mostrarError("La carrera y/o nombre no puede contener números.");
                return false;
        }
        if (!strPromedio.matches("^-?\\d+(\\.\\d+)?$")) {
                vista.mostrarError("El promedio debe ser un número válido.");
                return false;
        } 
        else if(Double.parseDouble(strPromedio) < 0.0 || Double.parseDouble(strPromedio) > 5.0) {
                vista.mostrarError("Por favor ingrese un promedio válido (entre 0 y 5).");
                return false;
        }
            return true;
    }
    
    public void agregarEstudiante(String nombre, String carrera, String strPromedio){
       if (validaciones(nombre, carrera, strPromedio) == false) {return;}
        for (Estudiante est : estudiantes) {
        if (est.getNombre().equalsIgnoreCase(nombre.trim())) {
            vista.mostrarError("Ya existe un estudiante con ese nombre.");
            return;
        }
    }
    Estudiante est = new Estudiante(this.cantidadEstudiantes + 1, nombre, carrera, Double.parseDouble(strPromedio));
    estudiantes.add(est);
    this.cantidadEstudiantes++;
    vista.mostrarEstudiantes(convertirAFilas(estudiantes));
    vista.mostrarConfirmacion(est);
    }
    

    public void ordenarPor(String criterio) {
    if (ultimosResultados == null || ultimosResultados.isEmpty()){
        vista.mostrarError("No es posible ordenar sin una busqueda previa"); return;}
    else if(ultimosResultados.size() == 1){
        vista.mostrarError("No es posible ordenar una lista de una sola persona."); return;}

    Comparator<Estudiante> cmp = switch (criterio.trim().toLowerCase()) {
        case "id"       -> Comparator.comparingInt(Estudiante::getId);
        case "nombre"   -> Comparator.comparing(Estudiante::getNombre, String.CASE_INSENSITIVE_ORDER);
        case "carrera"  -> Comparator.comparing(Estudiante::getCarrera, String.CASE_INSENSITIVE_ORDER);
        case "promedio" -> Comparator.comparingDouble(Estudiante::getPromedio);
        default         -> null;
    };

    if (cmp == null) return;

    ultimosResultados.sort(tipoAscendente ? cmp : cmp.reversed());
    tipoAscendente = !tipoAscendente;
    vista.mostrarEstudiantes(convertirAFilas(ultimosResultados));
}   

    // ── Traducción Modelo → datos para la Vista ───────────────────────────────
    // Estos métodos son el "puente" que evita que la Vista dependa de Estudiante.

    /**
     * Convierte un Estudiante (Modelo) en un arreglo genérico que la Vista
     * puede pintar sin conocer la clase Estudiante.
     */
    private Object[] convertirAFila(Estudiante e) {
        return new Object[]{
            e.getId(),
            e.getNombre(),
            e.getCarrera(),
            String.format("%.2f", e.getPromedio())
        };
    }

    /**
     * Convierte una lista de Estudiante en una lista de filas genéricas.
     */
    private List<Object[]> convertirAFilas(List<Estudiante> lista) {
        List<Object[]> filas = new ArrayList<>();
        for (Estudiante e : lista) {
            filas.add(convertirAFila(e));
        }
        return filas;
    }

   
}