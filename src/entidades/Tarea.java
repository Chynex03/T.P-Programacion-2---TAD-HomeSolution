package entidades;
import java.util.ArrayList;
import java.util.List;

public class Tarea {

	private String titulo;
	private String descripcion;
	private double cantDiasDuracion;
	private Empleado responsable;
	private double diasDeRetraso;
	private boolean tareaTerminada;
	private double costoTarea;
	private List<Empleado> historialEmpleados;

	public Tarea(String titulo, String descripcion, double duracionDias) {
		super();
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.cantDiasDuracion = duracionDias;
		this.responsable = null;
		this.diasDeRetraso = 0.0;
		this.tareaTerminada = false;
		this.costoTarea = 0.0;
		this.historialEmpleados = new ArrayList<>();
	}

	public void asignarEmpleado(Empleado empleado) {
		if (this.responsable != null) {
			this.responsable.setEstaDisponible(true);
		}
		this.responsable = empleado;
		empleado.setEstaDisponible(false);
		this.historialEmpleados.add(empleado);
	}
	
	public void desasignarEmpleado() {
		this.responsable = null;
	}

	public void registrarRetraso(double cantidadDias) {
		this.diasDeRetraso += cantidadDias;
		if (this.responsable != null) {
			this.responsable.registrarRetraso();
		}
	}

	public double calcularCosto() {
		if (this.responsable == null) {
			return 0.0;
		}

		double costoDiaEmpleado = this.responsable.calcularCostoPorDia();
		double diasACobrar;

		if (this.responsable instanceof EmpleadoPlanta && cantDiasDuracion == 0.5) {
			diasACobrar = 1.0;
		} else {
			diasACobrar = this.cantDiasDuracion;
		}
		return (diasACobrar * costoDiaEmpleado);
	}

	public void establecerTareaFinalizada() {
		this.tareaTerminada = true;
		this.costoTarea = this.calcularCosto();

		if (this.responsable != null) {

			this.responsable.setEstaDisponible(true);
			this.responsable = null;
		}
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getCantDiasDuracion() {
		return cantDiasDuracion;
	}

	public void setCantDiasDuracion(double cantDiasDuracion) {
		this.cantDiasDuracion = cantDiasDuracion;
	}

	public Empleado getResponsable() {
		return responsable;
	}

	public void setResponsable(Empleado responsable) {
		this.responsable = responsable;
	}

	public double getDiasDeRetraso() {
		return diasDeRetraso;
	}

	public void setDiasDeRetraso(double diasDeRetraso) {
		this.diasDeRetraso = diasDeRetraso;
	}

	public boolean isTareaTerminada() {
		return tareaTerminada;
	}

	public void setTareaTerminada(boolean tareaTerminada) {
		this.tareaTerminada = tareaTerminada;
	}

	public double getCostoTarea() {
		return costoTarea;
	}

	public void setCostoTarea(double costoTarea) {
		this.costoTarea = costoTarea;
	}

	public List<Empleado> getHistorialEmpleados() {
		return historialEmpleados;
	}

	public void setHistorialEmpleados(List<Empleado> historialEmpleados) {
		this.historialEmpleados = historialEmpleados;
	}

}
