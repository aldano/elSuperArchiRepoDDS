package edu.utn.dds.aterrizar.aerolineas;

import java.util.List;

import com.lanchita.AerolineaLanchita;
import com.lanchita.excepciones.EstadoErroneoException;

import edu.utn.dds.aterrizar.parser.*;
import edu.utn.dds.aterrizar.usuario.Usuario;
import edu.utn.dds.aterrizar.vuelo.*;
//TODO Inyectar dependencia de AerolineaLanchita

/**
 * Representa una implementación del pattern Adapter entre AerolineaLanchita y el model
 * @author clari, juani
 *
 */
public class AerolineaLanchitaWrapper implements Aerolinea {
   // private static final AerolineaLanchita aerolinea= AerolineaLanchita.getInstance();
	private static final double PORCENTAJE_DE_VENTA = 0.15;
	private Parser lanchitaParser;
	
	/**
	 * Constructor del wrapper con su correspondiente parser específico
	 * @param parser
	 */
	public AerolineaLanchitaWrapper(Parser parser){
		this.lanchitaParser= parser;
	}

	/**
	 * Dado un vuelo (origen, destino, fecha), hace una query a AerolineaLanchita para obtener los asientos disponibles
	 * (un array de arrays de String) y los parsea para obtener una lista de Asientos. 
	 *@throws ParseException si ocurre algún error durante el parseo.
	 */
	@Override
	public List<Asiento> buscarAsientos(Vuelo vuelo) {
		String[][] asientosDisponibles = AerolineaLanchita.getInstance().getAsientosDisponibles(vuelo.getOrigen(), vuelo.getDestino(), vuelo.getFecha());
		try{
		return this.lanchitaParser.parseDisponibles(asientosDisponibles, vuelo);
		}
		catch (RuntimeException e){
			throw new ParserException("No se pudo completar el parseo", e);
		}
	}

	/**
	 * Dada una lista de Asientos disponibles y un Usuario, usa la interfaz de compra de AerolinaLanchita.
	 * @throws AsientoLanchitaNoDisponibleException si el asiento no está disponible.
	 */
	@Override
	public void comprarAsiento(Asiento asientoDisponible,Usuario usuario) {
		try{
		AerolineaLanchita.getInstance().comprar(asientoDisponible.getCodigo(), usuario.getDni());
		}
		catch(EstadoErroneoException e){
			throw new AsientoLanchitaNoDisponibleException(e);
		}
	}

	@Override
	public Double getPorcentajeDeVenta() {
		return PORCENTAJE_DE_VENTA; 
	}

}
