package resources;

import com.makarov.annotation.Column;
import com.makarov.annotation.JoinColumn;
import com.makarov.annotation.Table;
import com.makarov.annotation.relation.ManyToOne;
import com.makarov.mapper.fetch.FetchType;

@Table(name = "products")
public class Product {

    @Column(name = "id")
    int id;

    @Column(name = "name")
    String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "s_id")
    Seller seller;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }
}
