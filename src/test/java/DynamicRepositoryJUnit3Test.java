import com.makarov.executor.QueryExecutor;
import com.makarov.factory.RepositoriesFactory;
import junit.framework.TestCase;
import resources.Product;
import resources.Seller;
import resources.repository.ProductRepository;
import resources.repository.SellerRepository;

import java.util.ArrayList;
import java.util.List;

public class DynamicRepositoryJUnit3Test extends TestCase {

    private ProductRepository productRepository = RepositoriesFactory.implement(ProductRepository.class);
    private SellerRepository sellerRepository = RepositoriesFactory.implement(SellerRepository.class);

    @Override
    public void setUp() throws Exception {
        QueryExecutor.executeQuery("INSERT INTO sellers VALUES (1, 'vasya')");
        QueryExecutor.executeQuery("INSERT INTO sellers VALUES (2, 'kolya')");
        QueryExecutor.executeQuery("INSERT INTO sellers VALUES (3, 'masha')");
    }

    @Override
    public void tearDown() throws Exception {
        QueryExecutor.executeQuery("DELETE FROM sellers");
        QueryExecutor.executeQuery("DELETE FROM products");
    }

    public void testSave() {
        Seller seller1 = QueryExecutor.findOne("SELECT * FROM seller WHERE s_id=1", Seller.class);
        Seller seller2 = QueryExecutor.findOne("SELECT * FROM seller WHERE s_id=2", Seller.class);
        Seller seller3 = QueryExecutor.findOne("SELECT * FROM seller WHERE s_id=3", Seller.class);

        List<Product> products = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(1);
        product1.setName("pasta");
        product1.setSeller(seller1);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("potato");
        product2.setSeller(seller1);
        products.add(product2);

        Product product3 = new Product();
        product3.setId(3);
        product3.setName("dog");
        product3.setSeller(seller1);
        products.add(product3);

        Product product4 = new Product();
        product4.setId(4);
        product4.setName("cat");
        product4.setSeller(seller2);
        products.add(product4);

        Product product5 = new Product();
        product5.setId(5);
        product5.setName("mouse");
        product5.setSeller(seller2);
        products.add(product5);

        Product product6 = new Product();
        product6.setId(6);
        product6.setName("horse");
        product6.setSeller(null);
        products.add(product6);

        Product product7 = new Product();
        product7.setId(7);
        product7.setName("pen");
        product7.setSeller(seller3);
        products.add(product7);

        Product product8 = new Product();
        product8.setId(8);
        product8.setName("cat");
        product8.setSeller(seller3);
        products.add(product8);

        productRepository.save(products);

        Seller seller4 = new Seller();
        seller4.setSid(5);
        seller4.setSname("ivan");

        Seller seller5 = new Seller();
        seller5.setSid(6);
        seller5.setSname("vova");

        Seller[] sellers = new Seller[2];
        sellers[0] = seller4;
        sellers[1] = seller5;

        sellerRepository.save(sellers);
    }

    public void testAnnotationQuery() {
        productRepository.selectOne(1);
        productRepository.deleteOne("dog");

        sellerRepository.getSeller(1, "kolya");
        sellerRepository.updateTable(2, 4);
    }

    public void testFind() {
        productRepository.findOneById(1);
        for (Product product : productRepository.findAll()) {
            product.getSeller();
        }

        sellerRepository.findOneByS_name("ivan");
        for (Seller seller : sellerRepository.findAll()) {
            seller.getProducts();
        }
    }

    public void testDelete() {
        productRepository.deleteFromProductsOneByName("cat");
        sellerRepository.deleteFromSellersByS_id(1);
    }

    public void testUpdate() {
        productRepository.updateProductsSetNameById("close", 5);
        sellerRepository.updateSellersSetS_nameByS_id("leha", 2);
    }
}
