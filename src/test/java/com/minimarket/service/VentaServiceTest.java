package com.minimarket.service;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Producto productoConStock;
    private Producto productoSinStock;
    private Usuario usuarioValido;
    private Venta ventaValida;

    @BeforeEach
    void setUp() {
        productoConStock = new Producto();
        productoConStock.setId(1L);
        productoConStock.setNombre("Producto Con Stock");
        productoConStock.setPrecio(2500.0);
        productoConStock.setStock(10);

        productoSinStock = new Producto();
        productoSinStock.setId(2L);
        productoSinStock.setNombre("Producto Sin Stock");
        productoSinStock.setPrecio(1000.0);
        productoSinStock.setStock(0);

        usuarioValido = new Usuario();
        usuarioValido.setId(1L);
        usuarioValido.setUsername("cajero1");

        ventaValida = new Venta();
        ventaValida.setUsuario(usuarioValido);
        ventaValida.setDetalles(List.of(
                crearDetalle(productoConStock, 1, 2500.0)
        ));
    }

    private DetalleVenta crearDetalle(Producto producto, int cantidad, double precio) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        return detalle;
    }

    @Test
    @DisplayName("Validar que la venta tenga un usuario asociado")
    void venta_TieneUsuarioAsociado() {
        assertNotNull(ventaValida.getUsuario());
        assertEquals("cajero1", ventaValida.getUsuario().getUsername());
    }

    @Test
    @DisplayName("Guardar venta - éxito")
    void guardarVenta_Exito() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaValida);

        Venta resultado = ventaService.save(ventaValida);

        assertNotNull(resultado);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    @DisplayName("FindById retorna venta cuando existe")
    void findById_VentaExistente_RetornaVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaValida));

        Venta resultado = ventaService.findById(1L);

        assertNotNull(resultado);
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("FindById retorna null cuando la venta no existe")
    void findById_VentaInexistente_RetornaNull() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        Venta resultado = ventaService.findById(99L);

        assertNull(resultado);
        verify(ventaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("FindAll retorna lista de ventas")
    void findAll_retornaLista() {
        when(ventaRepository.findAll()).thenReturn(List.of(ventaValida));

        List<Venta> ventas = ventaService.findAll();

        assertEquals(1, ventas.size());
        verify(ventaRepository, times(1)).findAll();
    }
}