
package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("checkstyle:MagicNumber")
@Repository
public class ProductDaoI implements ProductDao {

     static List<Product>products=new ArrayList<>();
    static {
        products.add(new Product(1, new String("Ordinateur portable"), 350));
        products.add(new Product(2, new String("Aspirateur Robot"), 500));
        products.add(new Product(3, new String("Table de Ping Pong"), 750));
    }


    @Override
    public List<Product> findAll() {
        return products;
    }

    @Override
    public Product findById(int id) {
        for (Product product : products) {
            if(product.getId() ==id){
                return product;
            }
        }
        return null;
    }

    @Override
    public Product save(Product product) {
        products.add(product);
        return product;
    }

}