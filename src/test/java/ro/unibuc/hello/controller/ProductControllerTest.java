ackage ro.unibuc.hello.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ro.unibuc.hello.data.ProductEntity;
import ro.unibuc.hello.data.ProductRepository;
import ro.unibuc.hello.dto.ProductAddStockDto;
import ro.unibuc.hello.exception.BadRequestException;
import ro.unibuc.hello.exception.NoContentException;
import ro.unibuc.hello.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductControllerTest {

    @Mock
    ProductRepository mockRepository;

    @InjectMocks
    ProductController productController = new ProductController();

    @Test
    public void getProduct_Throws() {
        when(mockRepository.findByTitle(anyString())).thenReturn(null);

        try {
            productController.getProduct("Test");
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(NotFoundException.class, e.getClass());
            Assertions.assertEquals("Not Found", e.getMessage());
        }
    }

    @Test
    public void getProduct_Returns() {
        when(mockRepository.findByTitle("Test")).thenReturn(new ProductEntity("1", "2", 3));

        var res = productController.getProduct("Test");

        Assertions.assertEquals("1", res.title);
        Assertions.assertEquals("2", res.description);
        Assertions.assertEquals(3, res.quantity);
    }

    @Test
    void getAllProducts_ReturnsInPage() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        var res = productController.getAllProducts("title_asc", 0, 5);

        Assertions.assertEquals(3, res.size());
    }

    @Test
    void getAllProducts_ReturnsOutOfPage() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        var res = productController.getAllProducts("title_desc", 1, 5);

        Assertions.assertEquals(0, res.size());
    }

    @Test
    void getAllProducts_ReturnsWithinPage() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        var res = productController.getAllProducts("description_asc", 0, 1);

        Assertions.assertEquals(1, res.size());
    }

    @Test
    void getAllProducts_ReturnsSortedByDescription() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        var res = productController.getAllProducts("description_desc", 0, 5);

        Assertions.assertEquals("26", res.get(0).description);
        Assertions.assertEquals("25", res.get(1).description);
        Assertions.assertEquals("24", res.get(2).description);
    }

    @Test
    void getAllProducts_ReturnsSortedByQuantityAsc() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        var res = productController.getAllProducts("quantity_asc", 0, 5);

        Assertions.assertEquals("26", res.get(0).description);
        Assertions.assertEquals("25", res.get(1).description);
        Assertions.assertEquals("24", res.get(2).description);
    }

    @Test
    void getAllProducts_ReturnsSortedByQuantityDesc() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        var res = productController.getAllProducts("quantity_desc", 0, 5);

        Assertions.assertEquals("4", res.get(0).title);
        Assertions.assertEquals("1", res.get(1).title);
        Assertions.assertEquals("12", res.get(2).title);
    }

    @Test
    public void getAllProducts_ThrowsNoProducts() {
        when(mockRepository.findAll()).thenReturn(List.of());

        try {
            var res = productController.getAllProducts("quantity_desc", 0, 5);
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(NoContentException.class, e.getClass());
            Assertions.assertEquals("No Content", e.getMessage());
        }
    }

    @Test
    public void getAllProducts_ThrowsBadSort() {
        when(mockRepository.findAll()).thenReturn(Arrays.asList(new ProductEntity("4", "24", 3),
                new ProductEntity("1", "25", 2),
                new ProductEntity("12", "26", 1)));

        try {
            var res = productController.getAllProducts("quasc", 0, 5);
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            Assertions.assertEquals("Bad Request", e.getMessage());
        }
    }

    @Test
    void addProductStock_Saves() {
        when(mockRepository.findByTitle(anyString())).thenReturn(new ProductEntity("4", "24", 3));

        productController.addProductStock(new ProductAddStockDto("4", 1));

        verify(mockRepository, times(1)).save(any());
    }

    @Test
    void sellProductStock() {
        var product = new ProductEntity("title", "desc", 3);
        when(mockRepository.findByTitle(anyString())).thenReturn(product);
        productController.sellProductStock(new ProductSellStockDto("title", 1));
        verify(mockRepository, times(1)).save(any());
        Assertions.assertEquals(2, product.quantity);
    }

    @Test
    void addProduct() {
    }
}