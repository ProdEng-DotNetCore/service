package ro.unibuc.hello.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.unibuc.hello.data.ProductEntity;
import ro.unibuc.hello.data.ProductRepository;
import ro.unibuc.hello.dto.AddProductDto;
import ro.unibuc.hello.dto.ProductSellStockDto;
import ro.unibuc.hello.exception.NotFoundException;

@SpringBootTest
public class ProductControllerTestIT {

    @Autowired
    ProductController productController;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        var x = productRepository.findByTitle("Nu Este");
        if(x != null)
            productRepository.delete(x);

        var y = productRepository.findByTitle("Este");
        if(y != null)
            productRepository.delete(y);

        productRepository.save(new ProductEntity("Este", "Sigur ca este", 50));
    }

    @Test
    @Order(1)
    public void getProduct_Throws() {
        try {
            productController.getProduct("Nu Este");
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(NotFoundException.class, e.getClass());
            Assertions.assertEquals("Not Found", e.getMessage());
        }
    }

    @Test
    @Order(2)
    void addProduct() {
        var product = new AddProductDto("Nu Este", "desc", 10);

        productController.addProduct(product);

        Assertions.assertNotNull(productRepository.findByTitle("Nu Este"));
    }

    @Test
    @Order(3)
    void sellProductStock() {

        productController.sellProductStock(new ProductSellStockDto("Este", 49));

        Assertions.assertEquals(1, productRepository.findByTitle("Este").quantity);
    }

    @Test
    @Order(4)
    void addProductStock() {

        productController.addProductStock(new ProductAddStockDto("Este", 1));

        Assertions.assertEquals(51, productRepository.findByTitle("Este").quantity);
    }

    @Test
    @Order(5)
    void addProductStock_Throws() {
        try{
            productController.addProductStock(new ProductAddStockDto("Este", 1));
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            Assertions.assertEquals("Bad Request", e.getMessage());
        }
    }

    @Test
    @Order(6)
    void sellProductStock_Throws() {
        try{
            productController.sellProductStock(new ProductSellStockDto("Este", 49));
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(BadRequestException.class, e.getClass());
            Assertions.assertEquals("Bad Request", e.getMessage());
        }
    }

}
