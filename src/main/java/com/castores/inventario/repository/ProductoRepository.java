package com.castores.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.castores.inventario.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstatusTrue();
}
