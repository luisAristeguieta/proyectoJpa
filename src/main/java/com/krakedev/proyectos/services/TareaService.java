package com.krakedev.proyectos.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.EmpleadoRepository;
import com.krakedev.proyectos.repositories.ProyectoRepository;
import com.krakedev.proyectos.repositories.TareaRepository;

@Service
public class TareaService {

	private final TareaRepository tareaRepository;
	private final ProyectoRepository proyectoRepository;
	private final EmpleadoRepository empleadoRepository;

	public TareaService(TareaRepository tareaRepository, ProyectoRepository proyectoRepository,
			EmpleadoRepository empleadoRepository) {
		this.tareaRepository = tareaRepository;
		this.proyectoRepository = proyectoRepository;
		this.empleadoRepository = empleadoRepository;
	}

	public Tarea guardar(Tarea tarea) {

		if (tarea == null) {
			throw new RuntimeException("La tarea no puede ser nula");
		}

		// Buscar y validar existencias con mettodos internos
		Proyecto proyecto = buscarProyecto(tarea.getProyecto());
		List<Empleado> empleadosValidos = buscarEmpleados(tarea.getEmpleados());

		tarea.setProyecto(proyecto);
		tarea.setEmpleados(empleadosValidos);

		return tareaRepository.save(tarea);
	}

	public List<Tarea> listar() {
		return tareaRepository.findAll();
	}

	public Tarea buscarPorId(int id) {
		return tareaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tarea con ID " + id + " no existe"));
	}

	public Tarea actualizar(int id, Tarea tarea) {
		Tarea tareaExistente = buscarPorId(id);

		// Actuliza parcial o total sino se envia los datos:
		if (tarea.getDescripcion() != null && !tarea.getDescripcion().isEmpty()) {
			tareaExistente.setDescripcion(tarea.getDescripcion());
		}

		if (tarea.getFechaLimite() != null) {
			tareaExistente.setFechaLimite(tarea.getFechaLimite());
		}

		if (tarea.getCostoEstimado() > 0) {
			tareaExistente.setCostoEstimado(tarea.getCostoEstimado());
		}

		if (tarea.getProyecto() != null && tarea.getProyecto().getId() > 0) {
			Proyecto proyecto = buscarProyecto(tarea.getProyecto());
			tareaExistente.setProyecto(proyecto);
		}

		if (tarea.getEmpleados() != null && !tarea.getEmpleados().isEmpty()) {
			List<Empleado> empleadosValidos = buscarEmpleados(tarea.getEmpleados());
			tareaExistente.setEmpleados(empleadosValidos);
		}

		return tareaRepository.save(tareaExistente);
	}

	public void eliminar(int id) {
		Tarea tareaExistente = buscarPorId(id);
		tareaRepository.delete(tareaExistente);
	}

	// valido con un metodo interno de validaciones que puedan generar error y
	// propago:
	// Que exista el proyecto y que exista el empleado
	private Proyecto buscarProyecto(Proyecto proyecto) {
		return proyectoRepository.findById(proyecto.getId())
				.orElseThrow(() -> new RuntimeException("Proyecto con ID " + proyecto.getId() + " no existe"));
	}

	private List<Empleado> buscarEmpleados(List<Empleado> empleados) {
		if (empleados == null || empleados.isEmpty()) {
			throw new RuntimeException("La tarea debe tener al menos un empleado asignado");
		}

		List<Empleado> empleadosExistentes = new ArrayList<>();

		for (Empleado empleado : empleados) {

			Empleado empleadoConsultado = empleadoRepository.findById(empleado.getId())
					.orElseThrow(() -> new RuntimeException("Empleado con ID " + empleado.getId() + " no existe"));
			empleadosExistentes.add(empleadoConsultado);
		}
		return empleadosExistentes;
	}
}