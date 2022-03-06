package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.ProductRepository;
import ro.unibuc.hello.data.ProductEntity;
import ro.unibuc.hello.dto.ProductAddStockDto;
import ro.unibuc.hello.dto.ProductDto;
import ro.unibuc.hello.dto.AddProductDto;
import ro.unibuc.hello.dto.ProductSellStockDto;
import ro.unibuc.hello.exception.BadRequestException;
import ro.unibuc.hello.exception.NoContentException;
import ro.unibuc.hello.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product")
    @ResponseBody
    public ProductDto sayHello(@RequestParam(name="name") String name) {
        var entity = productRepository.findByTitle(name);
        if(entity == null) {
            throw new NotFoundException();
        }
        return new ProductDto(entity);
    }

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ProductDto> getAllProducts() {
        var entities = productRepository.findAll();
        if (entities.size() == 0) {
            throw new NoContentException();
        }
        return entities.stream().map(ProductDto::new).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/product/supply")
    public void addProductStock(@RequestBody ProductAddStockDto model) {

        if (model == null) {
            throw new BadRequestException(new HashMap<>() {{
                put("error", "body is missing");
            }});
        }

        if (model.quantity <= 0) {
            throw new BadRequestException(new HashMap<>() {{
                put("quantity", "negative");
            }});
        }
        var product = productRepository.findByTitle(model.title);
        if (product == null) {
            throw new BadRequestException(new HashMap<>() {{
                put("product", "not found");
            }});
        }

        product.quantity += model.quantity;
        productRepository.save(product);
    }

    @PostMapping("/product/sell")
    @ResponseStatus(HttpStatus.OK)
    public void sellProductStock(@RequestBody ProductSellStockDto model) {

        if (model == null) {
            throw new BadRequestException(new HashMap<>() {{
                put("error", "body is missing");
            }});
        }
        if (model.quantity <= 0) {
            throw new BadRequestException(new HashMap<>() {{
                put("quantity", "negative");
            }});
        }
        var product = productRepository.findByTitle(model.title);
        if (product == null) {
            throw new BadRequestException(new HashMap<>() {{
                put("product", "not found");
            }});
        }

        product.quantity -= model.quantity;
        productRepository.save(product);
    }

    @PostMapping("/products/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody AddProductDto model) {

        if (model.quantity <= 0) {
            throw new BadRequestException(new HashMap<>() {{
                put("quantity", "negative");
            }});
        }
        ProductEntity product = new ProductEntity(model.title, model.description, model.quantity);
        productRepository.save(product);
    }
}
