package entidades;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Proyecto {

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


	public Proyecto(int numeroProyecto, Cliente cliente, String direccionVivienda, LocalDate fechaInicio) {
		this.numeroProyecto = numeroProyecto;
		this.cliente = cliente;
		this.direccionVivienda = direccionVivienda;
		this.listaTareas = new ArrayList<>();
		this.tareasPorTitulo = new HashMap<>();
		this.estado = Estado.pendiente;
		this.fechaInicio = fechaInicio;
		this.fechaFinEstimada = fechaInicio;
		this.fechaFinReal = fechaInicio;
		this.costoFinalCalculado = 0.0;
	}

	public void agregarTarea(Tarea tarea) {
		this.tareasPorTitulo.put(tarea.getTitulo(), tarea);
		this.listaTareas.add(tarea);
		this.actualizarFechasDeFinalizacion();

		if (this.estado.equals(Estado.pendiente)) {
			this.estado = Estado.activo;
		}
	}

	public boolean verificarTareasCompletadas() {
		for (Tarea t : listaTareas) {
			if (!t.isTareaTerminada()) {
				return false;
			}
		}
		return true;
	}

	public void establecerProyectoFinalizado(LocalDate fechaReal) throws Exception {

		if (!verificarTareasCompletadas()) {
			throw new Exception("Error: No se puede finalizar el proyecto, aún quedan tareas pendientes.");
		}


		if (this.estado.equals(Estado.finalizado)) {
			return;
		}

		this.estado = Estado.finalizado;
		this.fechaFinReal = fechaReal;

		this.calcularCostoFinal();
	}

	public void establecerProyectoFinalizado() throws Exception {
	    if (!verificarTareasCompletadas()) {
	        throw new Exception("Error: No se puede finalizar el proyecto, aún quedan tareas pendientes.");
	    }
	    if (this.estado.equals(Estado.finalizado)) {
	        return;
	    }

	    this.estado = Estado.finalizado;
	    this.calcularCostoFinal();
	}

	public boolean tieneTareasPendientes() {
		for (Tarea t : listaTareas) {
			if (t.getResponsable() == null && !t.isTareaTerminada()) {
				return true;
			}
		}
		return false;
	}

	public void actualizarFechasDeFinalizacion() {
		double duracionTotalDias = 0;
		double retrasoTotalDias = 0;

		for (Tarea t : listaTareas) {
			
			duracionTotalDias += Math.ceil(t.getCantDiasDuracion());
			retrasoTotalDias += t.getDiasDeRetraso();
		}

		this.fechaFinEstimada = fechaInicio.plusDays((long) Math.round(duracionTotalDias));

		this.fechaFinReal = fechaInicio.plusDays((long) Math.round(duracionTotalDias + retrasoTotalDias));
	}

	public void actualizarFechaFinRealPorRetraso(double cantidadDias) {

		this.actualizarFechasDeFinalizacion();
	}

	private boolean huboRetrasoTotal() {
		for (Tarea t : listaTareas) {
			if (t.getDiasDeRetraso() > 0.0) {
				return true;
			}
		}
		return false;
	}

	public void calcularCostoFinal() {
		double costoBaseTareas = 0.0;
		double bonificacionTotal = 0.0;
		boolean retrasoGeneral = this.huboRetrasoTotal();

		for (Tarea t : listaTareas) {
			costoBaseTareas += t.getCostoTarea();

			if (t.getResponsable() instanceof EmpleadoPlanta) {
				EmpleadoPlanta ep = (EmpleadoPlanta) t.getResponsable();

				if (!retrasoGeneral) {
					bonificacionTotal += ep.calcularAdicional(t.getCostoTarea());
				}
			}
		}

		double totalConMargen = costoBaseTareas * 1.35;

		if (retrasoGeneral) {
			totalConMargen = totalConMargen * 0.75;
		}

		this.costoFinalCalculado = totalConMargen + bonificacionTotal;
	}

	public Tarea getTareaPorTitulo(String titulo) {
		return tareasPorTitulo.get(titulo);
	}

	public double getCostoFinal() {
		return this.costoFinalCalculado;
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
}