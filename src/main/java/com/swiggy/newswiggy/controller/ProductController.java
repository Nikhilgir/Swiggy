package com.swiggy.newswiggy.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiggy.newswiggy.entity.Products;
import com.swiggy.newswiggy.request.ProductRequest;
import com.swiggy.newswiggy.response.ProductResponse;
import com.swiggy.newswiggy.service.ProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/product")
@Slf4j
public class ProductController {

	private ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping(value = "/add-product")
	public ResponseEntity<String> addProduct(@Valid @ModelAttribute ProductRequest product) {

		System.out.println(product.isVeg());
		System.out.println(product.getCategory());
		productService.saveProduct(product);
		System.out.println(product.getPrice());
		return new ResponseEntity<String>("Success", HttpStatus.CREATED);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/get-product/{id}")
	public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") int id) {
		Products product = productService.findProductById(id);
		ProductResponse productResponse = new ProductResponse();
		productResponse.setName(product.getName());
		productResponse.setPrice(product.getPrice());
		productResponse.setVeg(product.isVeg());
		productResponse.setDescription(product.getDescription());
		productResponse.setImage(product.getImage());
		return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
	}

	@GetMapping("/get-all-product/{page}/{size}")
	public CompletableFuture<ResponseEntity<Page<Products>>> getAllProduct(@PathVariable int page,
			@PathVariable int size) {
		return CompletableFuture.supplyAsync(() -> {
			Page<Products> products = productService.getProducts(page, size);
			return new ResponseEntity<>(products, HttpStatus.OK);
		});
	}

}
