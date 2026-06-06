package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.repositories.ProyectoRepository;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    public Proyecto guardar(Proyecto proyecto) {
    	
    	// Se realizan validaciones acdionales para crear el proyecto con propagacion de excepcioenes:
        if (proyecto == null) {
            throw new RuntimeException("El proyecto no puede ser nulo");
        }
        if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proyecto es requerido");
        }
        if (proyecto.getFechaInicio() == null) {
            throw new RuntimeException("La fecha de inicio del proyecto es requerida");
        }
        return proyectoRepository.save(proyecto);
    }

    public List<Proyecto> listar() {
        return proyectoRepository.findAll();
    }

    public Proyecto buscarPorId(int id) {
    	// en vez de usar el optional, la manejo bajo propagacion: 
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto con ID " + id + " no existe"));
    }

    public Proyecto actualizar(int id, Proyecto proyecto) {
        Proyecto proyectoExistente = buscarPorId(id);
        
        // Validaciones para que dejen los datos si no se envia todos el objeto proyecto completo con propagacion de sus atributos: 
        if (proyecto == null) {
            throw new RuntimeException("Los datos del proyecto no pueden ser nulos");
        }
        
        if (proyecto.getNombre() != null && !proyecto.getNombre().isEmpty()) {
            proyectoExistente.setNombre(proyecto.getNombre());
        }
        
        if (proyecto.getDescripcion() != null) {
            proyectoExistente.setDescripcion(proyecto.getDescripcion());
        }
        
        if (proyecto.getFechaInicio() != null) {
            proyectoExistente.setFechaInicio(proyecto.getFechaInicio());
        }
        
        return proyectoRepository.save(proyectoExistente);
    }

    public void eliminar(int id) {
        Proyecto proyectoExistente = buscarPorId(id);
        proyectoRepository.delete(proyectoExistente);
    }
}