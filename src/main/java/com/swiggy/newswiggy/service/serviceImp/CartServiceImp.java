package com.swiggy.newswiggy.service.serviceImp;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.swiggy.newswiggy.entity.Cart;
import com.swiggy.newswiggy.entity.Products;
import com.swiggy.newswiggy.entity.User;
import com.swiggy.newswiggy.exception.ResourceNotFound;
import com.swiggy.newswiggy.repository.CartRepository;
import com.swiggy.newswiggy.repository.ProductRepository;
import com.swiggy.newswiggy.repository.UserRepository;
import com.swiggy.newswiggy.repository.projections.CartProductProjection;
import com.swiggy.newswiggy.response.CartProductResponse;
import com.swiggy.newswiggy.service.CartService;

@Service
public class CartServiceImp implements CartService {

	private CartRepository cartRepository;
	private ProductRepository productRepository;
	private UserRepository userRepository;

	public CartServiceImp(CartRepository cartRepository, ProductRepository productRepository,
			UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<CartProductResponse> getToCartItemByUserId(int userId) {

		List<CartProductProjection> cartItem = cartRepository.findCartItemsByUserId(userId);
		return cartItem.stream()
				.map(e -> new CartProductResponse(e.getCartId(), e.getProductId(), e.getProductName(), e.getPrice(),
						e.getIsVej(), e.getProductDescription(), e.getProductCategory(), e.getProductRating(),
						e.getProductImage(), e.getQuantity()))
				.toList();

	}

	@Override
	public String addToCart(int userId, int productId) {
		Cart cart;
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("User Not Found"));
		Products product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFound("Product Not Found"));

		Optional<Cart> presentProduct = cartRepository.findByUserIdAndProductId(userId, productId);
		if (presentProduct.isPresent()) {
			cart = presentProduct.get();
			cart.setQuantity(cart.getQuantity() + 1);
			cartRepository.save(cart);
		} else {
			cart = new Cart();
			cart.setUser(user);
			cart.setProduct(product);
			cart.setQuantity(1);

			cartRepository.save(cart);
		}
		return "Product added to cart Successfully, productId= " + cart.getProduct().getId() + ", Quantity= "
				+ cart.getQuantity();

	}

	@Override
	public String reduceProductQuantity(int userId, int productId) {
		Optional<Cart> product = cartRepository.findByUserIdAndProductId(userId, productId);

		if (product.isPresent()) {
			Cart cartProduct = product.get();
			if (cartProduct.getQuantity() > 1) {
				cartProduct.setQuantity(cartProduct.getQuantity() - 1);
				cartRepository.save(cartProduct);
				return "Product quantity reduced successfully, productId= " + cartProduct.getProduct().getId()
						+ ", Quantity= " + cartProduct.getQuantity();
			} else {
				cartRepository.delete(cartProduct);
				return "product removed from cart";
			}
		} else {
			return "Product not found";
		}
	}

}
