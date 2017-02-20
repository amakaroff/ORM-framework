package resources;

import com.makarov.annotation.Column;
import com.makarov.annotation.Table;
import com.makarov.annotation.relation.OneToMany;
import com.makarov.mapper.fetch.FetchType;

import java.util.List;

@Table(name = "sellers")
public class Seller {

    @Column(name = "s_id")
    long sid;

    @Column(name = "s_name")
    String sname;

    @OneToMany( mappedBy = "seller", fetch = FetchType.LAZY)
    List<Product> products;

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
