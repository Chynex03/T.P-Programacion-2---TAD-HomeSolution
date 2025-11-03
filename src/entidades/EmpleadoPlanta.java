package entidades;
public class EmpleadoPlanta extends Empleado {

	private double valorDia;
	private String categoria;

	public EmpleadoPlanta(String nombre, int numeroLegajo, double valorDia, String categoria) {
		super(nombre, numeroLegajo);

		this.valorDia = valorDia;
		this.categoria = categoria;
	}

	@Override
	public double calcularCostoPorDia() {
		return this.valorDia;
	}
	
	public double calcularAdicional(double costoTarea) {
		return costoTarea * 0.02;
	}
	
	public double getValorDia() {
		return valorDia;
	}
	
	public String getCategoria() {
		return categoria;
	}
}
