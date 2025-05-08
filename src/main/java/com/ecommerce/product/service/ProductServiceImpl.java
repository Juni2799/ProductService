package com.ecommerce.product.service;

import com.ecommerce.product.dtos.ProductRequest;
import com.ecommerce.product.dtos.ProductResponse;
import com.ecommerce.product.models.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(this::productToProductResponseMapper)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(this::productToProductResponseMapper);
    }

    @Override
    public void deleteProductById(Long productId) {
        Product savedProduct = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));
        savedProduct.setActive(false);
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProductsByKeyword(keyword).stream()
                .map(this::productToProductResponseMapper)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse createNewProduct(ProductRequest productRequest) {
        Product product = productRequestToProductMapper(productRequest);
        Product savedProduct = productRepository.save(product);
        return productToProductResponseMapper(savedProduct);
    }

    @Override
    public ProductResponse updateExistingProduct(Long productId, ProductRequest productRequest) {
        Optional<Product> savedProduct = productRepository.findById(productId);
        if(savedProduct.isEmpty()){
            return null;
        }
        Product updatedProduct = updateProductWithProductRequest(productRequest, savedProduct.get());
        return productToProductResponseMapper(productRepository.save(updatedProduct));
    }

    private Product updateProductWithProductRequest(ProductRequest productRequest, Product product) {
        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(product.getStockQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        return product;
    }

    private ProductResponse productToProductResponseMapper(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setCategory(product.getCategory());
        productResponse.setDescription(product.getDescription());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setPrice(product.getPrice());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }

    private Product productRequestToProductMapper(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        return product;
    }
}
