package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;

public class ProductEntity {

    @Id
    public String id;

    public String title;
    public String description;
    public int quantity;

    public ProductEntity(String title, String description, int quantity) {
        this.title = title;
        this.description = description;
        this.quantity = quantity;
    }
}
