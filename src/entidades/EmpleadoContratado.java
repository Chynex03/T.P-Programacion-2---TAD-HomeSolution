package entidades;

public class EmpleadoContratado extends Empleado {

	private double valorHora;

	public EmpleadoContratado(String nombre, int numeroLegajo, double valorHora) {
		super(nombre, numeroLegajo);
		this.valorHora = valorHora;
	}

	@Override
	public double calcularCostoPorDia() {
		int horasPorDia = 8;
		return horasPorDia * this.valorHora;
	}

	public double getValorHora() {
		return valorHora;
	}

	public void setValorHora(double valorHora) {
		this.valorHora = valorHora;
	}
}
