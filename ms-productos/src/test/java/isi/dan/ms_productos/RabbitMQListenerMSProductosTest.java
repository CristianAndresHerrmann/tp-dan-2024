package isi.dan.ms_productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import isi.dan.ms_productos.conf.RabbitMQConfig;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.ProductoService;

@SpringBootTest()
@Testcontainers
public class RabbitMQListenerMSProductosTest {

    /* 
        no se como hacer que funcione el test, ya que no se como hacer que el listener se ejecute
        arroja IllegalStateException: Failed to load ApplicationContext
        pero se levanta el contenedor de rabbitmq y postgres
        NO EJECUTAR ESTE TEST HASTA QUE SE SOLUCIONE EL PROBLEMA
    */

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"))
            .withExposedPorts(5672, 15672);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
        
    @Autowired
    private ProductoRepository productoRepository;
        
    @MockBean
    private ProductoService productoService;

    @DynamicPropertySource
    static void registerRabbitMQProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
    }
    
    @BeforeEach
    void setup() {
        productoRepository.deleteAll();
        // Asegurar de que la cola existe
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(RabbitMQConfig.STOCK_UPDATE_QUEUE, true, false, false, null);
            return null;
        });
    }

    @Test
    void testHandleStockUpdate_LlamaAProductoService() throws ProductoNotFoundException, InterruptedException {
        // Arrange: Creamos un producto en la base de datos
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto 1");
        producto.setStockActual(10);
        producto.setStockMinimo(5);
        productoRepository.save(producto);

        // Act: Publicamos el mensaje en RabbitMQ
        String mensaje = "1;2"; // ID producto = 1, cantidad = 2
        rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE, RabbitMQConfig.ROUTING_KEY, mensaje);

        // Esperamos un poco para que el mensaje se procese
        Thread.sleep(5000);

        // Assert: Verificamos que ProductoService (que es el listener) fue llamado
        verify(productoService, times(1)).handleStockUpdate(any(org.springframework.amqp.core.Message.class));

        // Verificamos que el stock se actualizó
        Producto actualizado = productoRepository.findById(1L).orElseThrow(() -> new ProductoNotFoundException(1L));
        assertEquals(8, actualizado.getStockActual()); // 10 - 2 = 8
    }

    @Test
    void testHandleStockUpdate_ProductoNoExiste() throws InterruptedException, ProductoNotFoundException {
        // Act: Enviamos un mensaje con un ID de producto inexistente
        String mensaje = "99;2";
        rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE, mensaje);

        // Esperamos que se procese el mensaje
        Thread.sleep(5000);

        // Assert: Verificamos que el servicio intentó buscar el producto pero no lo encontró
        verify(productoService, times(1)).handleStockUpdate(any(org.springframework.amqp.core.Message.class));
    }

    @Test
    void testHandleStockUpdate_MensajeFormatoIncorrecto() throws InterruptedException, ProductoNotFoundException {
       // Arrange: Añadimos un producto para verificar que no se modifica
       Producto producto = new Producto();
       producto.setId(1L);
       producto.setNombre("Producto 1");
       producto.setStockActual(10);
       producto.setStockMinimo(5);
       productoRepository.save(producto);
       
       // Act: Enviamos un mensaje con un formato incorrecto
       String mensaje = "mensaje incorrecto";
       rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, mensaje);

       // Esperamos que se procese el mensaje
       Thread.sleep(3000);

       // Assert: Verificamos que no se modificó el producto
       Producto noModificado = productoRepository.findById(1L).get();
       assertEquals(10, noModificado.getStockActual());
   }

}