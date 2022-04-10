package ro.unibuc.hello.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.ProductEntity;
import ro.unibuc.hello.data.ProductRepository;
import ro.unibuc.hello.dto.AddProductDto;
import ro.unibuc.hello.dto.ProductAddStockDto;
import ro.unibuc.hello.dto.ProductDto;
import ro.unibuc.hello.dto.ProductSellStockDto;
import ro.unibuc.hello.exception.BadRequestException;
import ro.unibuc.hello.exception.NoContentException;
import ro.unibuc.hello.exception.NotFoundException;


import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    MeterRegistry metricsRegistry;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product")
    @ResponseBody
    public ProductDto getProduct(@RequestParam(name="name") String name) {
        var entity = productRepository.findByTitle(name);
        if(entity == null) {
            throw new NotFoundException();
        }
        return new ProductDto(entity);
    }

    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Timed(value = "product.getall.time", description = "Time taken to return list of sorted and paged products")
    @Counted(value = "product.getall.count", description = "Times list of products was returned")
    public List<ProductDto> getAllProducts(@RequestParam(required = true) String sort, int page, int productsOnPage) {
        var entities = productRepository.findAll();
        if (entities.size() == 0) {
            throw new NoContentException();
        }
        var returnedEntities = entities.stream();
        if (sort != null && !sort.equals("")) {
            var split = sort.split("_");
            if(split.length != 2 || !List.of("title", "description", "quantity").contains(split[0]) || !List.of("asc", "desc").contains(split[1])) {
                throw new BadRequestException(new HashMap<>() {{
                    put("sort", "bad argument");
                }});
            }
            switch(split[0]) {
                case "title": switch(split[1]) {
                    case "asc": returnedEntities = returnedEntities.sorted((e1, e2) -> e1.title.compareToIgnoreCase(e2.title)); break;
                    case "desc": returnedEntities = returnedEntities.sorted((e1, e2) -> e2.title.compareToIgnoreCase(e1.title)); break;
                }
                break;
                case "description": switch(split[1]) {
                    case "asc": returnedEntities = returnedEntities.sorted((e1, e2) -> e1.description.compareToIgnoreCase(e2.description)); break;
                    case "desc": returnedEntities = returnedEntities.sorted((e1, e2) -> e2.description.compareToIgnoreCase(e1.description)); break;
                }
                break;
                case "quantity": switch(split[1]) {
                    case "asc": returnedEntities = returnedEntities.sorted((e1, e2) -> e1.quantity - e2.quantity); break;
                    case "desc": returnedEntities = returnedEntities.sorted((e1, e2) -> e2.quantity - e1.quantity); break;
                }
                break;
            }

        }

        returnedEntities = returnedEntities.skip(page*productsOnPage).limit(productsOnPage);

        return returnedEntities.map(ProductDto::new).collect(Collectors.toList());
    }

  
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/product/supply")
    @Timed(value = "product.addstock.time", description = "Time taken to add stock to an existing product")
    @Counted(value = "product.addstock.count", description = "Times stock was added to products")
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
    @Timed(value = "product.sellstock.time", description = "Time taken to sell stock from an existing product")
    @Counted(value = "product.sellstock.count", description = "Times stock was sold from products")
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

    @PostMapping("/product/add")
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "product.add.time", description = "Time taken to add a new product")
    @Counted(value = "product.add.count", description = "Times a new product was added")
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
