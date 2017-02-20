package resources.repository;

import com.makarov.annotation.repository.Param;
import com.makarov.annotation.repository.Query;
import resources.Product;

import java.util.List;

public interface ProductRepository {

    @Query(query = "SELECT * FROM products WHERE id={id}")
    Product selectOne(@Param("id") int id);

    @Query(query = "DELETE FROM products WHERE name={name}")
    void deleteOne(@Param("name") String name);

    Product findOneById(int id);

    void deleteFromProductsOneByName(String name);

    List<Product> findAll();

    void save(List<Product> products);

    void updateProductsSetNameById(String name, int id);
}
