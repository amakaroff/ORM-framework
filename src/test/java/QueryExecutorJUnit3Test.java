import com.makarov.executor.QueryExecutor;
import junit.framework.TestCase;
import org.junit.Assert;
import resources.Product;
import resources.Seller;

import java.util.ArrayList;
import java.util.List;

public class QueryExecutorJUnit3Test extends TestCase {

    @Override
    protected void setUp() throws Exception {
        QueryExecutor.executeQuery("INSERT INTO sellers VALUES (1, 'vasya')");
        QueryExecutor.executeQuery("INSERT INTO sellers VALUES (2, 'kolya')");
        QueryExecutor.executeQuery("INSERT INTO sellers VALUES (3, 'masha')");
    }

    @Override
    public void tearDown() throws Exception {
        QueryExecutor.executeQuery("DELETE FROM sellers");
        QueryExecutor.executeQuery("DELETE FROM products");
    }

    public void testSaveObject() {
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
        product6.setSeller(seller3);
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

        for (Product product : products) {
            QueryExecutor.save(product);
            Assert.assertNotNull(QueryExecutor.findOne("SELECT * FROM product WHERE id=" + product.getId(), Product.class));
        }
    }

    public void testSelectObject() {
        Assert.assertNotNull(QueryExecutor.findOne("SELECT * FROM seller WHERE s_id=1", Seller.class));
        Assert.assertNotNull(QueryExecutor.findOne("SELECT * FROM seller WHERE s_id=2", Seller.class));
        Assert.assertNotNull(QueryExecutor.findOne("SELECT * FROM seller WHERE s_id=3", Seller.class));

        for (Product product : QueryExecutor.findSome("SELECT * FROM products", Product.class)) {
            Assert.assertNotNull(product.getSeller());
        }
    }

    public void testExecuteMethod() {
        QueryExecutor.executeQuery("DELETE FROM products WHERE id=3");
        Assert.assertNull(QueryExecutor.findOne("SELECT * FROM products WHERE id=3", Product.class));
        QueryExecutor.executeQuery("UPDATE products SET name='magic' WHERE id=2");
        Product product = QueryExecutor.findOne("SELECT * FROM products WHERE id=2", Product.class);
        Assert.assertNotNull(product);
        Assert.assertEquals("magic", product.getName());
    }
}
