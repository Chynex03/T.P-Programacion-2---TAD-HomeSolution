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
	    
	    // 1. Búsqueda del Proyecto y Tarea
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
	    
	    // Validaciones
	    if(tareaAFinalizar == null) {
	        throw new Exception("Error: La Tarea '" + titulo + "' no fue encontrada en Proyecto N° " + numero + ".");
	    }
	    
	    if (tareaAFinalizar.isTareaTerminada()) {
	        throw new Exception("Error: La tarea '" + titulo + "' ya fue hecha.");
	    }
	    
	    Empleado responsable = tareaAFinalizar.getResponsable();
	    if (responsable == null) {
	        throw new Exception ("Error: La tarea no tiene empleado responsable.");
	    }
	    
	    // 2. Acción de Finalización
	    tareaAFinalizar.establecerTareaFinalizada(); // Esto libera al responsable.
	    
	    // 3. Resultado de la Tarea (Se imprime SIEMPRE)
	    System.out.println("✅ Tarea '" + titulo + "' finalizada con éxito.");
	    System.out.println("El responsable " + responsable.getNombre() + " ya se encuentra disponible.");
	    
	    // 4. Lógica de Finalización de Proyecto
	    if (proyecto.verificarTareasCompletadas()) {
	        
	        // ¡PROBLEMA! No tenemos la fecha. Debes llamar a la versión sin fecha, O asumir una fecha,
	        // O requerir que el usuario llame a finalizarProyecto(numero, fecha) aparte.
	        
	        // Asumimos que la lógica de establecerProyectoFinalizado() que diseñamos
	        // solo verifica y cambia el estado, dejando la fecha al otro método.
	        
	        // Usamos la versión de tu código:
	        proyecto.establecerProyectoFinalizado(); 
	        
	        // Si usamos la versión de tu código, necesitamos que el método de Proyecto 
	        // no requiera la fecha como parámetro: public void establecerProyectoFinalizado() throws Exception { ... }
	        
	        System.out.println("🎉 Proyecto N°" + numero + " FINALIZADO con éxito.");
	    }
	}

	@Override
	public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {
		// 1. Validar y Parsear la Fecha de Finalización (String -> LocalDate)
	    LocalDate fechaRealFin;
	    try {
	        // Usamos el formato esperado (YYYY-MM-DD)
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
	        fechaRealFin = LocalDate.parse(fin, formatter);
	    } catch (Exception e) {
	        // Capturamos cualquier error de formato o parseo
	        throw new IllegalArgumentException("Formato de fecha de finalización inválido. Por favor, use YYYY-MM-DD.");
	    }
	    
	    // 2. Buscar el Proyecto (O(1))
	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    if (proyecto == null) {
	        throw new IllegalArgumentException("Error: Proyecto N° " + numero + " no encontrado.");
	    }

	    // 3. Delegar la finalización al objeto Proyecto
	    try {
	        // Llamamos al método actualizado de Proyecto, pasándole la fecha parseada.
	        // Este método se encarga de: verificar tareas, establecer el estado FINALIZADO,
	        // establecer la fecha real de fin, y calcular el costo final.
	        proyecto.establecerProyectoFinalizado(fechaRealFin);
	        
	        // 4. Resultado
	        System.out.println("🎉 ÉXITO! El Proyecto N°" + numero + " ha sido FINALIZADO el " + fin + ".");
	        System.out.println("   Costo Final Calculado: " + proyecto.getCostoFinal());
	        
	    } catch (Exception e) {
	        // Capturamos la excepción si la clase Proyecto informa que aún quedan tareas pendientes
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
	
	@Override
	public double costoProyecto() {
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
	}

	@Override
	public List<Tupla<Integer, String>> proyectosFinalizados() {
		
		List<Tupla<Integer, String>> listaProyectosFinalizados = new ArrayList<>();
		
		for (Proyecto p : proyectosPorNumero.values()){
			if (p.getEstado().equals(Estado.finalizado)) {
				
				listaProyectosFinalizados.add(new Tupla<>(p.getNumeroProyecto(), p.getDireccionVivienda()));
			}
		}
		return listaProyectosFinalizados;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosPendientes() {
		List<Tupla<Integer, String>> listaProyectosPendientes = new ArrayList<>();

	    // Iteramos sobre todos los proyectos del sistema (O(N))
	    for (Proyecto p : proyectosPorNumero.values()) {
	        
	        // Criterio de filtrado: Usamos la constante Estado.pendiente
	        if (p.getEstado().equals(Estado.pendiente)) {
	            
	            // Creamos una nueva Tupla con el número y la dirección del proyecto
	            listaProyectosPendientes.add(new Tupla<>(
	                p.getNumeroProyecto(), 
	                p.getDireccionVivienda() 
	            ));
	        }
	    }
	    
	    // Devolvemos el listado filtrado
	    return listaProyectosPendientes;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosActivos() {
		List<Tupla<Integer, String>> listaProyectosActivos = new ArrayList<>();

	    // Iteramos sobre todos los proyectos del sistema (O(N))
	    for (Proyecto p : proyectosPorNumero.values()) {
	        
	        // Criterio de filtrado: Usamos la constante Estado.activo
	        if (p.getEstado().equals(Estado.activo)) {
	            
	            // Creamos una nueva Tupla con el número y la dirección del proyecto
	            listaProyectosActivos.add(new Tupla<>(
	                p.getNumeroProyecto(), 
	                p.getDireccionVivienda() 
	            ));
	        }
	    }
	    
	    // Devolvemos el listado filtrado
	    return listaProyectosActivos;
	}

	@Override
	public Object[] empleadosNoAsignados() {
		// 1. Usamos una lista temporal para almacenar los empleados disponibles (O(N) para la iteración)
	    List<Empleado> empleadosDisponibles = new ArrayList<>();

	    // Iteramos sobre todos los empleados del sistema (O(N))
	    for (Empleado emp : empleadosPorLegajo.values()) {
	        
	        // Criterio de filtrado: El empleado debe estar disponible
	        if (emp.isEstaDisponible()) {
	            empleadosDisponibles.add(emp);
	        }
	    }
	    
	    // 2. Convertimos la lista filtrada al tipo de retorno solicitado (Object[])
	    // El método toArray() sin argumentos devuelve un Object[].
	    return empleadosDisponibles.toArray();
	}

	@Override
	public boolean estaFinalizado(Integer numero) {
		// 1. Acceso al Proyecto (O(1) usando el Map)
	    Proyecto proyecto = proyectosPorNumero.get(numero);

	    // 2. Validación de existencia
	    if (proyecto == null) {
	        // Si el proyecto no existe, asumimos que NO está finalizado.
	        // Opcionalmente, podrías lanzar una excepción si el número es inválido.
	        return false;
	    }
	    
	    // 3. Consulta del Estado (O(1))
	    // Comparamos el estado almacenado con la constante FINALIZADO
	    return proyecto.getEstado().equals(Estado.finalizado);
	}

	@Override
	public int consultarCantidadRetrasosEmpleado(Integer legajo) {
		// 1. Acceso al Empleado (O(1) usando el Map)
	    Empleado empleado = empleadosPorLegajo.get(legajo);

	    // 2. Validación de existencia
	    if (empleado == null) {
	        // La interfaz pide devolver un int. Devolver 0 o -1 es común
	        // para indicar que no hay retrasos o que el empleado no existe.
	        // Devolvemos 0 y notificamos, ya que es el resultado más seguro.
	        System.out.println("⚠️ Advertencia: Empleado con Legajo " + legajo + " no encontrado.");
	        return 0; 
	    }
	    
	    // 3. Consulta del Atributo (O(1))
	    // Devolvemos la cantidad de retrasos almacenada en el objeto Empleado.
	    return empleado.getCantRetrasos();
	}

	@Override
	public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {

		    // 1. Acceso al Proyecto (O(1))
		    Proyecto proyecto = proyectosPorNumero.get(numero);
		    if (proyecto == null) {
		        return null;
		    }
		    
		    // Lista final que se devolverá
		    List<Tupla<Integer, String>> reporteAsignados = new ArrayList<>();
		    
		    // HashSet para evitar agregar al mismo empleado dos veces si tiene múltiples tareas
		    Set<Integer> legajosYaAgregados = new HashSet<>(); 
		    
		    // 2. Iterar sobre las Tareas del Proyecto (O(N))
		    for (Tarea t : proyecto.getListaTareas()) {
		        
		        Empleado responsable = t.getResponsable();
		        
		        // Criterio: La tarea tiene un responsable asignado y no ha sido terminada
		        // Aunque la consigna pide los asignados al proyecto, se incluyen también los que
		        // estuvieron asignados y terminaron, pero el HashSet lo filtra.
		        
		        if (responsable != null) {
		            
		            Integer legajo = responsable.getNumeroLegajo();
		            
		            // 3. Verificar Duplicados y Agregar
		            if (!legajosYaAgregados.contains(legajo)) {
		                
		                // Creamos la Tupla con Legajo y Nombre
		                reporteAsignados.add(new Tupla<>(
		                    legajo, 
		                    responsable.getNombre() 
		                ));
		                
		                // Marcamos el legajo como agregado
		                legajosYaAgregados.add(legajo);
		            }
		        }
		    }
		    
		    // 4. Resultado
		    return reporteAsignados;
	}

	@Override
	public Object[] tareasProyectoNoAsignadas(Integer numero) {

	    // Usamos try-catch para manejar el caso de proyecto no encontrado sin lanzar Exception
	    try {
	        // 1. Acceso al Proyecto (O(1) usando el Map)
	        Proyecto proyecto = proyectosPorNumero.get(numero);
	        
	        if (proyecto == null) {
	            // Si el proyecto no existe, devolvemos un arreglo vacío inmediatamente.
	            return new Object[0]; 
	        }

	        // 2. Lista temporal para almacenar las tareas filtradas
	        List<Tarea> tareasNoAsignadas = new ArrayList<>();

	        // 3. Iterar sobre las Tareas del Proyecto (O(N))
	        for (Tarea t : proyecto.getListaTareas()) {
	            
	            // Criterio de filtrado: No tiene responsable Y no está terminada
	            if (t.getResponsable() == null && !t.isTareaTerminada()) {
	                tareasNoAsignadas.add(t);
	            }
	        }

	        // 4. Convertimos la lista filtrada al tipo de retorno solicitado (Object[])
	        return tareasNoAsignadas.toArray();
	        
	    } catch (Exception e) {
	        // En caso de cualquier error inesperado, devolvemos un arreglo vacío.
	        System.err.println("Error inesperado al buscar tareas no asignadas en el proyecto " + numero + ": " + e.getMessage());
	        return new Object[0];
	    }
	}

	@Override
	public String consultarDomicilioProyecto(Integer numero) {
		// 1. Acceso al Proyecto (O(1) usando el Map)
	    Proyecto proyecto = proyectosPorNumero.get(numero);

	    // 2. Validación de existencia
	    if (proyecto == null) {
	        return "";
	    }
	    
	    // 3. Consulta del Atributo (O(1))
	    // Devolvemos el domicilio almacenado en el objeto Proyecto.
	    return proyecto.getDireccionVivienda();
	}

	@Override
	public boolean tieneRestrasos(String legajo) {
		// 1. Acceso al Empleado (O(1) usando el Map)
	    Empleado empleado = empleadosPorLegajo.get(legajo);

	    // 2. Validación de existencia
	    if (empleado == null) {
	        // Lanzamos una excepción, ya que el legajo provisto es inválido.
	        return false;
	    }
	    
	    // 3. Consulta del Atributo (O(1))
	    // Devolvemos true si la cantidad de retrasos acumulados es mayor que cero.
	    return empleado.getCantRetrasos() > 0;
	}

	@Override
	public List<Tupla<Integer, String>> empleados() {
		// Lista final que contendrá todas las Tuplas (Legajo, Nombre)
	    List<Tupla<Integer, String>> reporteEmpleados = new ArrayList<>();

	    // Iteramos sobre todos los empleados del sistema (O(N))
	    for (Empleado emp : empleadosPorLegajo.values()) {
	        
	        // Creamos la Tupla con Legajo (Integer) y Nombre (String)
	        reporteEmpleados.add(new Tupla<>(
	            emp.getNumeroLegajo(), 
	            emp.getNombre() 
	        ));
	    }
	    
	    // 2. Resultado
	    return reporteEmpleados;
	}
	
	// Asumimos que la firma del método es algo como esto:
	public Tupla<String, Double> tareaMasLarga() throws Exception {

	    // Inicialización para la búsqueda de máximo
	    Tarea tareaMasLarga = null;
	    double maxDuracion = 0.0;

	    // Iteramos sobre todos los proyectos (O(N * M))
	    for (Proyecto p : proyectosPorNumero.values()) {
	        
	        // Iteramos sobre todas las tareas de cada proyecto
	        for (Tarea t : p.getListaTareas()) {
	            
	            double duracionActual = t.getCantDiasDuracion();
	            
	            // Criterio de Máximo: Si la duración actual es mayor al máximo encontrado
	            if (duracionActual > maxDuracion) {
	                maxDuracion = duracionActual;
	                tareaMasLarga = t;
	            }
	        }
	    }

	    // Validación del Resultado
	    if (tareaMasLarga == null) {
	        // Esto ocurriría si el sistema no tiene proyectos ni tareas.
	        throw new Exception("Error: No hay tareas registradas en el sistema para determinar la más larga.");
	    }

	    // Devolvemos los datos de la tarea más larga en una Tupla
	    return new Tupla<>(tareaMasLarga.getTitulo(), tareaMasLarga.getCantDiasDuracion());
	}

	@Override
	public Object[] tareasDeUnProyecto(Integer numero) {
		// 1. Buscar el Proyecto (O(1))
	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    
	    if (proyecto == null) {
	        // Si no se encuentra, devolvemos un arreglo vacío (o lanzamos excepción si lo exige el estándar)
	        return new Object[0]; 
	    }

	    // 2. Devolver la lista de tareas como un arreglo
	    // Asumimos que la lista listaTareas contiene todos los objetos Tarea.
	    return proyecto.getListaTareas().toArray();
	}

	@Override
	public String consultarProyecto(Integer numero) {
		// 1. Buscar el Proyecto (O(1))
	    Proyecto proyecto = proyectosPorNumero.get(numero);
	    
	    if (proyecto == null) {
	        return "Error: Proyecto N° " + numero + " no encontrado.";
	    }
	    
	    // 2. Devolver la representación en String
	    // Aquí se asume que la clase Proyecto tiene un método toString() detallado.
	    return proyecto.toString();
	}
}
