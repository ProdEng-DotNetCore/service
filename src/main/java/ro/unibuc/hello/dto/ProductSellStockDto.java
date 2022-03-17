package ro.unibuc.hello.dto;

public class ProductSellStockDto {
    public String title;
    public int quantity;

    public ProductSellStockDto(String title, int quantity) {
        this.title = title;
        this.quantity = quantity;
    }
}
