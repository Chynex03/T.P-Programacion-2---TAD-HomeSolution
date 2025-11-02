package entidades;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Proyecto {

	// ============================================================
	// ATRIBUTOS (VARIABLES DE INSTANCIA)
	// ============================================================
	private int numeroProyecto;
	private Cliente cliente;
	private String direccionVivienda;
	private List<Tarea> listaTareas;
	private HashMap<String, Tarea> tareasPorTitulo;
	private LocalDate fechaInicio;
	private LocalDate fechaFinEstimada;
	private LocalDate fechaFinReal;

	private String estado;
	private double costoFinalCalculado;

	// ============================================================
	// CONSTRUCTOR
	// ============================================================
	public Proyecto(int numeroProyecto, Cliente cliente, String direccionVivienda, LocalDate fechaInicio) {
		this.numeroProyecto = numeroProyecto;
		this.cliente = cliente;
		this.direccionVivienda = direccionVivienda;
		this.listaTareas = new ArrayList<>();
		this.tareasPorTitulo = new HashMap<>();
		// Inicialización de estado y fechas
		this.estado = Estado.pendiente; // Usa la constante de la clase Estado
		this.fechaInicio = fechaInicio;
		this.fechaFinEstimada = fechaInicio;
		this.fechaFinReal = fechaInicio;
		this.costoFinalCalculado = 0.0;
	}

	// ============================================================
	// MÉTODOS DE GESTIÓN Y ESTADO
	// ============================================================

	/**
	 * Agrega una tarea y recalcula las fechas del proyecto. Actualiza el estado a
	 * ACTIVO si estaba PENDIENTE.
	 */
	public void agregarTarea(Tarea tarea) {
		this.tareasPorTitulo.put(tarea.getTitulo(), tarea);
		this.listaTareas.add(tarea);
		this.actualizarFechasDeFinalizacion();

		if (this.estado.equals(Estado.pendiente)) {
			this.estado = Estado.activo;
		}
	}

	/**
	 * Verifica si todas las tareas del proyecto han sido marcadas como terminadas.
	 * No cambia el estado del proyecto.
	 */
	public boolean verificarTareasCompletadas() {
		for (Tarea t : listaTareas) {
			if (!t.isTareaTerminada()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Marca el proyecto como FINALIZADO si todas las tareas están completadas.
	 * Llama a calcularCostoFinal().
	 */
	public void establecerProyectoFinalizado(LocalDate fechaReal) throws Exception {

		// 1. Validación de Tareas
		if (!verificarTareasCompletadas()) {
			throw new Exception("Error: No se puede finalizar el proyecto, aún quedan tareas pendientes.");
		}

		// 2. Validación de Estado
		if (this.estado.equals(Estado.finalizado)) {
			return; // Ya finalizó, no hacer nada
		}

		// 3. Establecer el estado y la fecha real
		this.estado = Estado.finalizado;
		this.fechaFinReal = fechaReal; // <--- SE ASIGNA LA FECHA RECIBIDA

		// 4. Calcular el Costo Final
		this.calcularCostoFinal();
	}

	/**
	 * Versión 1: Llama desde finalizarTarea. 
	 * Solo verifica, establece el estado FINALIZADO y calcula el costo.
	 * NO toca la fecha (asume que la fecha la dará el usuario después).
	 */
	public void establecerProyectoFinalizado() throws Exception {
	    if (!verificarTareasCompletadas()) {
	        throw new Exception("Error: No se puede finalizar el proyecto, aún quedan tareas pendientes.");
	    }
	    if (this.estado.equals(Estado.finalizado)) {
	        return;
	    }

	    this.estado = Estado.finalizado;
	    this.calcularCostoFinal();
	    
	    // Opcional: Podrías poner la fecha del sistema aquí si es necesario
	    // this.fechaFinReal = LocalDate.now(); 
	}
	
	/**
	 * Verifica si el proyecto tiene tareas sin empleado asignado y no terminadas.
	 */
	public boolean tieneTareasPendientes() {
		for (Tarea t : listaTareas) {
			if (t.getResponsable() == null && !t.isTareaTerminada()) {
				return true;
			}
		}
		return false;
	}

	// ============================================================
	// MÉTODOS DE CÁLCULO Y FECHAS
	// ============================================================

	/**
	 * Recalcula la fecha estimada y real basándose en la duración y retrasos de
	 * todas las tareas.
	 */
	public void actualizarFechasDeFinalizacion() {
		double duracionTotalDias = 0;
		double retrasoTotalDias = 0;

		for (Tarea t : listaTareas) {
			// Regla: Se considera al menos un día completo para la estimación (Math.ceil)
			duracionTotalDias += Math.ceil(t.getCantDiasDuracion());
			retrasoTotalDias += t.getDiasDeRetraso();
		}

		// FECHA ESTIMADA (Solo duración)
		this.fechaFinEstimada = fechaInicio.plusDays((long) Math.round(duracionTotalDias));

		// FECHA REAL (Duración + Retraso)
		this.fechaFinReal = fechaInicio.plusDays((long) Math.round(duracionTotalDias + retrasoTotalDias));
	}

	/**
	 * Llama a actualizarFechasDeFinalizacion(), que recalcula la fecha real con el
	 * nuevo retraso. (Llamado desde HomeSolution.registrarRetrasoEnTarea).
	 */
	public void actualizarFechaFinRealPorRetraso(double cantidadDias) {
		// La lógica del retraso ya está registrada en la Tarea.
		// Solo necesitamos que el Proyecto se sincronice.
		this.actualizarFechasDeFinalizacion();
	}

	/**
	 * Verifica si el proyecto tuvo algún retraso en alguna de sus tareas.
	 */
	private boolean huboRetrasoTotal() {
		for (Tarea t : listaTareas) {
			if (t.getDiasDeRetraso() > 0.0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calcula y guarda el costo final (con margen y bonificaciones).
	 */
	public void calcularCostoFinal() {
		double costoBaseTareas = 0.0;
		double bonificacionTotal = 0.0;
		boolean retrasoGeneral = this.huboRetrasoTotal();

		for (Tarea t : listaTareas) {
			costoBaseTareas += t.getCostoTarea();

			// Lógica de Bonificación
			if (t.getResponsable() instanceof EmpleadoPlanta) {
				EmpleadoPlanta ep = (EmpleadoPlanta) t.getResponsable();

				if (!retrasoGeneral) { // Solo si NO hubo retraso general
					bonificacionTotal += ep.calcularAdicional((float) t.getCostoTarea());
				}
			}
		}

		double totalConMargen = costoBaseTareas * 1.35; // +35% de margen

		if (retrasoGeneral) {
			totalConMargen = totalConMargen * 0.75; // -25% de reducción
		}

		this.costoFinalCalculado = totalConMargen + bonificacionTotal;
	}

	// ============================================================
	// GETTERS Y SETTERS
	// ============================================================

	public Tarea getTareaPorTitulo(String titulo) {
		return tareasPorTitulo.get(titulo);
	}

	public double getCostoFinal() {
		return this.costoFinalCalculado; // Cumple O(1)
	}

	public String getEstado() {
		return estado;
	}

	public List<Tarea> getListaTareas() {
		return listaTareas;
	}

	public int getNumeroProyecto() {
		return numeroProyecto;
	}

	public String getDireccionVivienda() {
		return direccionVivienda;
	}

	// ... otros getters (Cliente, fechas, etc.) si son necesarios
}