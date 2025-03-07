package com.swiggy.newswiggy.service.serviceImp;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.swiggy.newswiggy.entity.Restaurant;
import com.swiggy.newswiggy.exception.ResourceNotFound;
import com.swiggy.newswiggy.repository.RestaurantRepository;
import com.swiggy.newswiggy.request.RestaurantRequest;
import com.swiggy.newswiggy.response.RestaurantResponse;
import com.swiggy.newswiggy.service.RestaurantService;

@Service
public class RestaurantServiceImp implements RestaurantService {

	public RestaurantRepository restaurantRepository;

	public RestaurantServiceImp(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	@Override
	public String addRestaurant(RestaurantRequest restaurantRequest) {

		Restaurant restaurant = new Restaurant();

		restaurant.setName(restaurantRequest.getName());
		restaurant.setDescription(restaurantRequest.getDescription());
		restaurant.setAddress(restaurantRequest.getAddress());
		restaurant.setCity(restaurantRequest.getCity());
		restaurant.setPhone(restaurantRequest.getPhone());
		restaurant.setRating(restaurantRequest.getRating());
		restaurant.setOpeningTime(restaurantRequest.getOpeningTime());
		restaurant.setClosingTime(restaurantRequest.getClosingTime());
		restaurantRepository.save(restaurant);
		return "Restaurant added successfully";
	}

	@Override
	public RestaurantResponse getRestaurantById(int restaurantId) {
		Optional<Restaurant> confirmation = restaurantRepository.findById(restaurantId);
		if (!confirmation.isPresent()) {
			throw new ResourceNotFound("Restaurant Not Found By ID:" + restaurantId);
		}
		Restaurant restaurant = confirmation.get();
		RestaurantResponse response = new RestaurantResponse();
		response.setRestaurantId(restaurant.getRestaurantId());
		response.setName(restaurant.getName());
		response.setDescription(restaurant.getDescription());
		response.setAddress(restaurant.getAddress());
		response.setCity(restaurant.getCity());
		response.setPhone(restaurant.getPhone());
		response.setRating(restaurant.getRating());
		response.setOpeningTime(restaurant.getOpeningTime());
		response.setClosingTime(restaurant.getClosingTime());
		return response;

	}

	@Override
	public List<Restaurant> findRestaurantByCity(String city) {
		return restaurantRepository.findByCity(city);
	}

}
