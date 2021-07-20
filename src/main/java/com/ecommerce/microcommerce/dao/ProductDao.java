
package com.ecommerce.microcommerce.dao;

import com.ecommerce.microcommerce.model.Product;
import java.util.List;

public interface ProductDao {
    /**
     * Retourne le level du zéro.
     * @return Une instance de SDZLevel, qui correspond à niveau du membre
     */

    public List<Product> findAll();
    public  Product findById(int id);
    public  Product save(Product product);
}
