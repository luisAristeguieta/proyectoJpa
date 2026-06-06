package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.repositories.EmpleadoRepository;

@Service
public class EmpleadoService {

	// Inyeccion a trves del constructor: 
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado guardar(Empleado empleado) {
    
    	// Al no permitir nulos en las columnas las valida antes de q se guarden con propagacion: 
        if (empleado == null) {
            throw new RuntimeException("El empleado no puede ser nulo");
        }
        if (empleado.getNombre() == null || empleado.getNombre().isEmpty()) {
            throw new RuntimeException("El nombre del empleado es requerido");
        }
        if (empleado.getCargo() == null || empleado.getCargo().isEmpty()) {
            throw new RuntimeException("El cargo del empleado es requerido");
        }
        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    public Empleado buscarPorId(int id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado con ID " + id + " no existe"));
    }

    public Empleado actualizar(int id, Empleado empleado) {
        Empleado empleadoExistente = buscarPorId(id);
        
        // Sno se propago actualizar parte o todo: 
        
        if (empleado == null) {
            throw new RuntimeException("Los datos del empleado no pueden ser nulos");
        }
        
        if (empleado.getNombre() != null && !empleado.getNombre().isEmpty()) {
            empleadoExistente.setNombre(empleado.getNombre());
        }
        
        if (empleado.getCargo() != null && !empleado.getCargo().isEmpty()) {
            empleadoExistente.setCargo(empleado.getCargo());
        }
        
        return empleadoRepository.save(empleadoExistente);
    }

    public void eliminar(int id) {
        Empleado empleadoExistente = buscarPorId(id);
        empleadoRepository.delete(empleadoExistente);
    }
}