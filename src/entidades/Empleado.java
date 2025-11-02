package entidades;

public class Empleado {
	
	private String nombre;
	private int numeroLegajo;
	private boolean estaDisponible;
	private int cantRetrasos;

	
	public Empleado (String nombre, int numeroLegajo) {
		this.nombre = nombre;
		this.numeroLegajo = numeroLegajo;
		this.estaDisponible = true;
		this.cantRetrasos = 0;
	}
	
	public void registrarRetraso() {
		this.cantRetrasos++;
	}
	
	public double calcularCostoPorDia() {
		return 0.0;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNumeroLegajo() {
		return numeroLegajo;
	}

	public void setNumeroLegajo(int numeroLegajo) {
		this.numeroLegajo = numeroLegajo;
	}

	public boolean isEstaDisponible() {
		return estaDisponible;
	}

	public void setEstaDisponible(boolean estaDisponible) {
		this.estaDisponible = estaDisponible;
	}

	public int getCantRetrasos() {
		return cantRetrasos;
	}

	public void setCantRetrasos(int cantRetrasos) {
		this.cantRetrasos = cantRetrasos;
	}	
}
