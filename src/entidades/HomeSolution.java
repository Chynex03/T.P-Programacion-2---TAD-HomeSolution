package entidades;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomeSolution implements IHomeSolution {

	private Map<Integer, Empleado> empleadosPorLegajo;
	private List<Empleado> todosLosEmpleados;
	private int proximoLegajo = 1000;
	private Map<Integer, Proyecto> proyectosPorNumero;
	private List<Proyecto> todosLosProyectos;
	private int proximoNumeroProyecto = 5000;
	
	public HomeSolution() {
		
		this.proyectosPorNumero = new HashMap<>(); 
        this.todosLosProyectos = new ArrayList<>();
		this.empleadosPorLegajo = new HashMap<>();
		this.todosLosEmpleados = new ArrayList<>();
	}

	@Override
	public void registrarEmpleado(String nombre, double valor) throws IllegalArgumentException {
		if (nombre == null || nombre.trim().isEmpty() || valor < 0) {
			throw new IllegalArgumentException("El nombre o valor son invalidos.");
		}

		int nuevoLegajo = proximoLegajo++;

		Empleado nuevoEmpleado = new EmpleadoContratado(nombre, nuevoLegajo, valor);

		empleadosPorLegajo.put(nuevoLegajo, nuevoEmpleado);
		todosLosEmpleados.add(nuevoEmpleado);

		System.out.println("El empleado contratado ha sido registrado: " + nombre + "(Legajo: " + nuevoLegajo + ")");
	}

	@Override
	public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
		if (nombre == null || nombre.trim().isEmpty() || valor < 0 || categoria == null || categoria.trim().isEmpty()) {
			throw new IllegalArgumentException("Los parametros de Empleado Planta son invalidos. ");
		}

		int nuevoLegajo = proximoLegajo++;

		Empleado nuevoEmpleado = new EmpleadoPlanta(nombre, nuevoLegajo, valor, categoria);

		empleadosPorLegajo.put(nuevoLegajo, nuevoEmpleado);
		todosLosEmpleados.add(nuevoEmpleado);

		System.out.println("Empleado de planta registrado: " + nombre + " (Legajo: " + nuevoLegajo + ")");
	}

	@Override
	public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio,
			String[] cliente, String inicio, String fin) throws IllegalArgumentException {

		if (titulos == null || titulos.length == 0 || dias == null || dias.length != titulos.length || cliente == null
				|| cliente.length < 3) {
			throw new IllegalArgumentException("Datos de proyecto, tareas o cliente incompletos");

		}

		Cliente nuevoCliente = new Cliente(cliente[0], cliente[1], cliente[2]);

		LocalDate fechaInicio;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			fechaInicio = LocalDate.parse(inicio, formatter);
		} catch (Exception e) {
			throw new IllegalArgumentException("Formato de fecha de inicio invalido. Por favor, use YYYY-MM-DD");

		}

		int numProyecto = proximoNumeroProyecto++;

		Proyecto nuevoProyecto = new Proyecto(numProyecto, nuevoCliente, domicilio, fechaInicio);

		for (int i = 0; i < titulos.length; i++) {
			String titulo = titulos[i];
			String desc = (descripcion != null && i < descripcion.length) ? descripcion[i] : "Sin descripcion";
			double duracionDias = dias[i];

			if (duracionDias <= 0) {
				throw new IllegalArgumentException("La duracion de la tarea " + titulo + " deber ser positiva.");
			}

			Tarea nuevaTarea = new Tarea(titulo, desc, duracionDias);

			nuevoProyecto.agregarTarea(nuevaTarea);
		}

		proyectosPorNumero.put(numProyecto, nuevoProyecto);
		todosLosProyectos.add(nuevoProyecto);

		System.out.println("Proyecto " + numProyecto + " registrado con " + titulos.length + " tareas.");
	}

	@Override
	public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {

		Proyecto proyecto = proyectosPorNumero.get(numero);
		if (proyecto == null) {
			throw new Exception("El proyecto numero: " + numero + " no fue encontrado.");
		}

		Tarea tareaParaAsignar = null;

		for (Tarea t : proyecto.getListaTareas()) {
			if (t.getTitulo().equalsIgnoreCase(titulo)) {
				tareaParaAsignar = t;
				break;
			}
		}

		if (tareaParaAsignar == null) {
			throw new Exception("Error: Tarea: " + titulo + "no fue encontrada en el Proyecto " + numero + ".");
		}

		Empleado empleadoDisponible = null;

		Iterator<Empleado> iteradorEmpleados = todosLosEmpleados.iterator();

		while (iteradorEmpleados.hasNext()) {
			Empleado e = iteradorEmpleados.next();

			if (e.isEstaDisponible()) {
				empleadoDisponible = e;
				break;
			}
		}

		if (empleadoDisponible == null) {
			throw new Exception("No hay empleados disponibles en este momento");
		}

		tareaParaAsignar.asignarEmpleado(empleadoDisponible);

		System.out.println(
				empleadoDisponible.getNombre() + " asignado a la tarea: " + titulo + " del Proyecto " + numero + ".");
	}

	@Override
	public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectosPorNumero.get(numero);
		if (proyecto == null) {
			throw new Exception("Proyecto numero: " + numero + "no encontrado.");
		}

		Tarea tareaParaAsignar = null;
		for (Tarea t : proyecto.getListaTareas()) {
			if (t.getTitulo().equals(titulo)) {
				tareaParaAsignar = t;
				break;
			}
		}

		if (tareaParaAsignar == null) {
			throw new Exception("Error: La tarea ya tiene un empleado responsable.");
		}

		Empleado mejorCandidato = null;
		int minRetrasos = Integer.MAX_VALUE;

		for (Empleado empleado : todosLosEmpleados) {

			if (empleado.isEstaDisponible()) {

				int retrasosActuales = empleado.getCantRetrasos();

				if (retrasosActuales < minRetrasos) {

					minRetrasos = retrasosActuales;
					mejorCandidato = empleado;

					if (minRetrasos == 0) {
						break;
					}
				}
			}
		}

		if (mejorCandidato == null) {
			throw new Exception("Error: No hay empleados disponibles para asignar a la tarea.");
		}

		tareaParaAsignar.asignarEmpleado(mejorCandidato);

		System.out.println("Responsable " + mejorCandidato.getNombre() + "Retrasos: " + minRetrasos
				+ ") asignado a la tarea: " + titulo + "del Proyecto " + numero + ".");

	}

	@Override
	public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias) {
		if (cantidadDias <= 0) {
			throw new IllegalArgumentException("La cantidad de dias de retraso debe ser positivo");
		}

		Proyecto proyecto = proyectosPorNumero.get(numero);
		if (proyecto == null) {
			throw new IllegalArgumentException("Proyecto " + numero + " no encontrado.");
		}

		Tarea tareaAfectada = null;
		for (Tarea t : proyecto.getListaTareas()) {
			if (t.getTitulo().equalsIgnoreCase(titulo)) {
				tareaAfectada = t;
				break;
			}
		}
		if (tareaAfectada == null) {
			throw new IllegalArgumentException("Tarea " + titulo + " no encontrada en Proyecto " + numero + ".");
		}

		Empleado responsable = tareaAfectada.getResponsable();
		if (responsable == null) {
			throw new IllegalArgumentException(
					"No se puede registrar un retraso. La tarea no tiene un empleado asignado.");
		}
		tareaAfectada.registrarRetraso(cantidadDias);
		responsable.registrarRetraso();
		proyecto.actualizarFechaFinRealPorRetraso(cantidadDias);

		System.out.println("Retraso de " + cantidadDias + " dias registrado en la tarea " + titulo + ".");
		System.out.println("La nueva fecha de fin real del proyecto " + numero + " ha sido actualizada.");
	}

	@Override
	public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias)
			throws IllegalArgumentException {
		if (dias <= 0) {
			throw new IllegalArgumentException("La duracion de la nueva tarea debe ser un valor positivo");
		}

		Proyecto proyecto = proyectosPorNumero.get(numero);
		if (proyecto == null) {
			throw new IllegalArgumentException("Proyecto " + numero + " no encontrado.");
		}

		System.out.println("Tarea " + titulo + "agregada al Proyecto " + numero + ".");
		System.out.println("Fechas de finalizacion actualizadas.");
	}

	@Override
	public void finalizarTarea(Integer numero, String titulo) throws Exception {
	    
	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    if (proyecto == null) {
	        throw new Exception("Error: Proyecto N° " + numero + " no encontrado.");
	    }
	    
	    Tarea tareaAFinalizar = null;
	    for(Tarea t : proyecto.getListaTareas()) {
	        if (t.getTitulo().equalsIgnoreCase(titulo)) {
	            tareaAFinalizar = t;
	            break;
	        }
	    }
	    
	    if(tareaAFinalizar == null) {
	        throw new Exception("La Tarea '" + titulo + "' no fue encontrada en Proyecto N° " + numero + ".");
	    }
	    
	    if (tareaAFinalizar.isTareaTerminada()) {
	        throw new Exception("La tarea '" + titulo + "' ya fue hecha.");
	    }
	    
	    Empleado responsable = tareaAFinalizar.getResponsable();
	    if (responsable == null) {
	        throw new Exception ("La tarea no tiene empleado responsable.");
	    }
	    
	    tareaAFinalizar.establecerTareaFinalizada();
	    
	    System.out.println("Tarea '" + titulo + "' finalizada con éxito.");
	    System.out.println("El responsable " + responsable.getNombre() + " ya se encuentra disponible.");
	    
	    if (proyecto.verificarTareasCompletadas()) {
	        
	        proyecto.establecerProyectoFinalizado(); 
	     
	        
	        System.out.println("🎉 Proyecto N°" + numero + " FINALIZADO con éxito.");
	    }
	}

	@Override
	public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {

	    LocalDate fechaRealFin;
	    try {

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
	        fechaRealFin = LocalDate.parse(fin, formatter);
	    } catch (Exception e) {

	        throw new IllegalArgumentException("Formato de fecha de finalización inválido. Por favor, use YYYY-MM-DD.");
	    }
	    
	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    if (proyecto == null) {
	        throw new IllegalArgumentException("Error: Proyecto N° " + numero + " no encontrado.");
	    }

	    try {

	        proyecto.establecerProyectoFinalizado(fechaRealFin);
	        
	        // 4. Resultado
	        System.out.println("🎉 ÉXITO! El Proyecto N°" + numero + " ha sido FINALIZADO el " + fin + ".");
	        System.out.println("   Costo Final Calculado: " + proyecto.getCostoFinal());
	        
	    } catch (Exception e) {

	        throw new IllegalArgumentException("No se pudo finalizar el proyecto N°" + numero + ". " + e.getMessage());
	    }
	}

	@Override
	public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
		Proyecto proyecto = proyectosPorNumero.get(numero);
		if (proyecto == null) {
			throw new Exception("Proyecto " + numero + "no encontrado.");
		}

		Tarea tareaAfectada = proyecto.getTareaPorTitulo(titulo);
		
		if (tareaAfectada == null) {
			throw new Exception("Tarea " + titulo + " no encontrada.");
		}
		
		Empleado empleadoNuevo = empleadosPorLegajo.get(legajo);
		if (empleadoNuevo == null) {
			throw new Exception("Empleado con legajo " + legajo + " no encontrado.");
		}
		
		if(tareaAfectada.isTareaTerminada()) {
			throw new Exception ("No se puede reasignar un empleado a una tarea que ya fue finalizada.");
		}

		Empleado responsableAnterior = tareaAfectada.getResponsable();
		if (responsableAnterior == null) {
			throw new Exception("La tarea no tiene un empleado asignado previamente para reasignar.");
		}

		tareaAfectada.asignarEmpleado(empleadoNuevo);

		System.out.println("Reasignacion de Empleado completa");
		System.out.println("- Empleado Anterior: " + responsableAnterior.getNombre() + "(Liberado).");
		System.out.println("- Empleado Nuevo: " + empleadoNuevo.getNombre() + " (Asignado).");
	}

	@Override
	public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
		
		Proyecto proyecto = proyectosPorNumero.get(numero);
		if (proyecto == null) {
			throw new Exception("Proyecto " + numero + " no encontrado."); 
		}
		Tarea tareaAfectada = proyecto.getTareaPorTitulo(titulo);
		
		if (tareaAfectada == null) {
			throw new Exception("Tarea " + titulo + " no encontrada en proyecto " + numero + ".");
		}
		
		if (tareaAfectada.isTareaTerminada()) {
			throw new Exception("No se puede reasignar un empleado a una tarea finalizada");
		}
		
		Empleado responsableAnterior = tareaAfectada.getResponsable();
		if (responsableAnterior == null) {
			throw new Exception ("La tarea no tiene un empleado asignado previamente para reasignar.");
		}
		
		Empleado mejorCandidato = null;
		int minRetrasos = Integer.MAX_VALUE;
		
		for (Empleado emp : empleadosPorLegajo.values()) {
			
			if (emp.isEstaDisponible()) {
				int retrasosActuales = emp.getCantRetrasos();
				
				if (retrasosActuales < minRetrasos){
					minRetrasos = retrasosActuales;
					mejorCandidato = emp;
					
					if (minRetrasos == 0) {
						break;
					}
				}
			}
		}
		
		if (mejorCandidato == null) {
			throw new Exception("No hay empleados disponibles para reasignar la tarea.");
		}
		
		tareaAfectada.asignarEmpleado(mejorCandidato);
		
		System.out.println("Reasignacion de Empleado completa");
		System.out.println("- Empleado Anterior: " + responsableAnterior.getNombre() + "(Liberado).");
		System.out.println("- Empleado Nuevo: " + mejorCandidato.getNombre() + " (Retrasos: " + minRetrasos + ").");
		}
	
	/*@Override
	public double costoProyecto(Integer numero) {
		String numero = "";
		Proyecto proyecto = proyectosPorNumero.get(numero);
		
		if (proyecto == null) {
			return 0.0;
		}
		
		double costo = proyecto.getCostoFinal();
		
		if (costo == 0.0) {
			System.out.println("Advertencia: El proyecto" + numero + " aun no esta finalizado. El costo devuelto es 0.0.");
		}
		return costo;
	}*/

	@Override
	public List<Tupla<Integer, String>> proyectosFinalizados() {

	    List<Tupla<Integer, String>> listaProyectosFinalizados = new ArrayList<>();

	    for (Proyecto p : proyectosPorNumero.values()) {
	        
	        if (p.getEstado().equals(Estado.finalizado)) {
	            
	            listaProyectosFinalizados.add(new Tupla<>(
	                p.getNumeroProyecto(), 
	                p.getDireccionVivienda() 
	            ));
	        }
	    }
	    
	    return listaProyectosFinalizados;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosPendientes() {
	    
	    List<Tupla<Integer, String>> listaProyectosPendientes = new ArrayList<>();

	    for (Proyecto p : proyectosPorNumero.values()) {
	        
	        // Criterio de filtrado: Estado.pendiente
	        if (p.getEstado().equals(Estado.pendiente)) {
	            
	            listaProyectosPendientes.add(new Tupla<>(
	                p.getNumeroProyecto(), 
	                p.getDireccionVivienda() 
	            ));
	        }
	    }
	    
	    return listaProyectosPendientes;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosActivos() {
	    
	    List<Tupla<Integer, String>> listaProyectosActivos = new ArrayList<>();

	    for (Proyecto p : proyectosPorNumero.values()) {
	        
	        if (p.getEstado().equals(Estado.activo)) {
	            
	            listaProyectosActivos.add(new Tupla<>(
	                p.getNumeroProyecto(), 
	                p.getDireccionVivienda() 
	            ));
	        }
	    }
	    
	    return listaProyectosActivos;
	}
	
	@Override
	public Object[] empleadosNoAsignados() {

	    List<Empleado> empleadosDisponibles = new ArrayList<>();

	    for (Empleado emp : empleadosPorLegajo.values()) {
	        
	        if (emp.isEstaDisponible()) {
	            empleadosDisponibles.add(emp);
	        }
	    }
	    
	    return empleadosDisponibles.toArray();
	}

	@Override
	public boolean estaFinalizado(Integer numero) {

	    Proyecto proyecto = proyectosPorNumero.get(numero);

	    if (proyecto == null) {

	        return false;
	    }
	    
	    return proyecto.getEstado().equals(Estado.finalizado);
	}

	@Override
	public int consultarCantidadRetrasosEmpleado(Integer legajo) {

	    Empleado empleado = empleadosPorLegajo.get(legajo);

	    if (empleado == null) {

	        System.out.println("Empleado con Legajo " + legajo + " no encontrado.");
	        return 0; 
	    }

	    return empleado.getCantRetrasos();
	}

	@Override
	public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {

		    Proyecto proyecto = proyectosPorNumero.get(numero);
		    if (proyecto == null) {
		        return null;
		    }
		    
		    List<Tupla<Integer, String>> reporteAsignados = new ArrayList<>();
		    
		    Set<Integer> legajosYaAgregados = new HashSet<>(); 
		    
		    for (Tarea t : proyecto.getListaTareas()) {
		        
		        Empleado responsable = t.getResponsable();
		        
		        if (responsable != null) {
		            
		            Integer legajo = responsable.getNumeroLegajo();
		            
		            if (!legajosYaAgregados.contains(legajo)) {
		                
		                reporteAsignados.add(new Tupla<>(
		                    legajo, 
		                    responsable.getNombre() 
		                ));
		                
		                legajosYaAgregados.add(legajo);
		            }
		        }
		    }
		    
		    return reporteAsignados;
	}

	@Override
	public Object[] tareasProyectoNoAsignadas(Integer numero) {

	    try {

	        Proyecto proyecto = proyectosPorNumero.get(numero);
	        
	        if (proyecto == null) {

	            return new Object[0]; 
	        }

	        List<Tarea> tareasNoAsignadas = new ArrayList<>();

	        for (Tarea t : proyecto.getListaTareas()) {
	            
	            if (t.getResponsable() == null && !t.isTareaTerminada()) {
	                tareasNoAsignadas.add(t);
	            }
	        }

	        return tareasNoAsignadas.toArray();
	        
	    } catch (Exception e) {
	    	
	        System.err.println("Error inesperado al buscar tareas no asignadas en el proyecto " + numero + ": " + e.getMessage());
	        return new Object[0];
	    }
	}

	@Override
	public String consultarDomicilioProyecto(Integer numero) {

	    Proyecto proyecto = proyectosPorNumero.get(numero);

	    if (proyecto == null) {
	        return "";
	    }
	    
	    return proyecto.getDireccionVivienda();
	}

	@Override
	public boolean tieneRestrasos(String legajo) {
	    Integer legajoInt;
	    try {
	        legajoInt = Integer.parseInt(legajo);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    
	    Empleado empleado = empleadosPorLegajo.get(legajoInt); 

	    if (empleado == null) {
	        return false;
	    }
	    
	    return empleado.getCantRetrasos() > 0;
	}

	@Override
	public List<Tupla<Integer, String>> empleados() {

	    List<Tupla<Integer, String>> reporteEmpleados = new ArrayList<>();

	    for (Empleado emp : empleadosPorLegajo.values()) {
	        
	        reporteEmpleados.add(new Tupla<>(
	            emp.getNumeroLegajo(), 
	            emp.getNombre() 
	        ));
	    }
	    
	    return reporteEmpleados;
	}
	
	public Tupla<String, Double> tareaMasLarga() throws Exception {

	    Tarea tareaMasLarga = null;
	    double maxDuracion = 0.0;


	    for (Proyecto p : proyectosPorNumero.values()) {
	        

	        for (Tarea t : p.getListaTareas()) {
	            
	            double duracionActual = t.getCantDiasDuracion();
	            
	            if (duracionActual > maxDuracion) {
	                maxDuracion = duracionActual;
	                tareaMasLarga = t;
	            }
	        }
	    }


	    if (tareaMasLarga == null) {

	        throw new Exception("Error: No hay tareas registradas en el sistema para determinar la más larga.");
	    }

	    return new Tupla<>(tareaMasLarga.getTitulo(), tareaMasLarga.getCantDiasDuracion());
	}

	@Override
	public Object[] tareasDeUnProyecto(Integer numero) {

	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    
	    if (proyecto == null) {

	        return new Object[0]; 
	    }

	    return proyecto.getListaTareas().toArray();
	}

	@Override
	public String consultarProyecto(Integer numero) {

	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    
	    if (proyecto == null) {
	        return "Error: Proyecto N° " + numero + " no encontrado.";
	    }
	    
	    return proyecto.toString();
	}
}
