package resources.repository;

import com.makarov.annotation.repository.Param;
import com.makarov.annotation.repository.Query;
import resources.Seller;

import java.util.Set;

public interface SellerRepository {

    @Query(query = "SELECT * FROM sellers WHERE s_id={id} AND s_name={name}")
    Seller getSeller(@Param("id") long id, @Param("name") String name);

    @Query(query = "UPDATE sellers SET s_id={nid} WHERE s_id={id}")
    void updateTable(@Param("id") long id, @Param("nid") long nid);

    Seller findOneByS_name(String name);

    void deleteFromSellersByS_id(int id);

    Set<Seller> findAll();

    void save(Seller[] seller);

    void updateSellersSetS_nameByS_id(String name, int id);
}
