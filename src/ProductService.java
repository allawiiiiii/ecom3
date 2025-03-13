import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductRepo productRepo;

    public ProductService() {
        // Instantiate or inject (if you use a DI framework) the repository
        this.productRepo = new ProductRepo();
    }

    /**
     * Get all products with name, price, stock count, and stock status.
     * Example return:
     *  "Name: iPhone | Price: 799.00 | Stock: 10 (In Stock)"
     */
    public List<String> getAllProducts() {
        return productRepo.getAllProducts();
    }

    /**
     * Search products by keyword (partial match on product name).
     * Returns same format as getAllProducts().
     */
    public List<String> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }


    //Uppdaterar lagret för en produkt som identifieras av productName.
    public void updateStock(String productName, int newStock) {
        productRepo.updateStock(productName, newStock);
    }

}
