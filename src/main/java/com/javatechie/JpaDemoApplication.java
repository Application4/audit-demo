package com.javatechie;

import com.javatechie.common.AuditorAwareImpl;
import com.javatechie.entity.Product;
import com.javatechie.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/products")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class JpaDemoApplication {

    @Autowired
    private ProductRepository repository;

    //explain CRUD

    @PostMapping
    public Product addProduct(@RequestBody Product product){
        return repository.save(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product productRequest) {
        // get the product from DB by id
        // update with new value getting from request
        Product existingProduct = repository.findById(id).get(); // DB
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setProductType(existingProduct.getProductType());
        return repository.save(existingProduct);
    }

    //search by specific field
    @GetMapping("/{productType}")
    public List<Product> getProductsByType(@PathVariable String productType) {
        return repository.findByProductType(productType);
    }

    //search by specific field with query
    @GetMapping("/query/{productType}")
    public List<Product> getProductsByTypeQuery(@PathVariable String productType) {
        return repository.findByProduct(productType);
    }

    //	search by Multiple field
    @GetMapping("/{price}/{productType}")
    public List<Product> getProductByPriceAndType(@PathVariable double price, @PathVariable String productType) {
        return repository.findByPriceAndProductType(price, productType);
    }

    //Operators
    @PostMapping("/search")
    public List<Product> searchProductWithMultiplePrice(@RequestBody List<Double> price) {
        return repository.findByPriceIn(price);
    }

    @GetMapping("/min/{minPrice}/max/{maxPrice}")
    public List<Product> getProductWithPriceRange(@PathVariable double minPrice, @PathVariable double maxPrice) {
        return repository.findByPriceBetween(minPrice, maxPrice);
    }

    @GetMapping("/less/{priceRange}")
    public List<Product> getProductWithLessPrice(@PathVariable double priceRange) {
        return repository.findByPriceLessThan(priceRange);
    }

    @GetMapping("/greater/{priceRange}")
    public List<Product> getProductWithGreaterPrice(@PathVariable double priceRange) {
        return repository.findByPriceGreaterThan(priceRange);
    }

    @GetMapping("/like/{name}")
    public List<Product> getProductWithLike(@PathVariable String name) {
        return repository.findByNameIgnoreCaseContaining(name);
    }

    //pagination & sorting

    @GetMapping("/sort/{sortByField}")
    public List<Product> getProductsWithSorting(@PathVariable String sortByField) {
        return repository.findAll(Sort.by(Sort.Direction.ASC, sortByField));
    }

    @GetMapping("/page/{offset}/{pageSize}")
    public Page<Product> findProductsWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
        return repository.findAll(PageRequest.of(offset, pageSize));

    }

    @GetMapping("/pageWithSort/{offset}/{pageSize}/{field}")
    public Page<Product> findProductsWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        return repository.findAll(PageRequest.of(offset, pageSize)
                .withSort(Sort.by(field)));
    }


    public static void main(String[] args) {
        SpringApplication.run(JpaDemoApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }

}
