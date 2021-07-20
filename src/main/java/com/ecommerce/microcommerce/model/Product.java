package com.ecommerce.microcommerce.model;

import java.util.List;

public class Product {

    /**
     * Domain classes used to produce .....
     * int id
     */
    private int id = 1;
    /**
     * Domain classes used to produce .....
     * String name
     */
    private String name = "Narjess";
    /**
     * Domain classes used to produce .....
     *int price
     */
    private int price = 540;
    /**
     * Domain classes used to produce .....
     *public product
     */
    public Product() {
    }
    /**
     * Domain classes used to produce .....
     * product list
     * @param id1 id The id
     * @param name1 name1 The name1
     * @param price1 price1 The price1
     */
    public Product(int id1, String name1,int price1)
    {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }
    /**
     * Domain classes used to produce .....
     *@param id id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString(){
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
