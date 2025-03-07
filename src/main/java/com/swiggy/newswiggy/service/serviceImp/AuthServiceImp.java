package com.swiggy.newswiggy.service.serviceImp;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.swiggy.newswiggy.entity.Addresses;
import com.swiggy.newswiggy.entity.Country;
import com.swiggy.newswiggy.entity.User;
import com.swiggy.newswiggy.entity.User.Gender;
import com.swiggy.newswiggy.exception.SwiggyException;
import com.swiggy.newswiggy.exception.UserNameNotFoundException;
import com.swiggy.newswiggy.repository.AddressesRepository;
import com.swiggy.newswiggy.repository.CountryRepository;
import com.swiggy.newswiggy.repository.UserRepository;
import com.swiggy.newswiggy.request.AddressRequest;
import com.swiggy.newswiggy.request.LoginRequest;
import com.swiggy.newswiggy.request.SignupRequest;
import com.swiggy.newswiggy.service.AuthService;
import com.swiggy.newswiggy.utils.ExceptionMessages;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImp implements AuthService {

	private UserRepository userRepository;
	private AddressesRepository addressesRepository;
	private CountryRepository countryRepository;
	private final PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;

	@Autowired
	public AuthServiceImp(UserRepository userRepository, AddressesRepository addressesRepository,
			CountryRepository countryRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.addressesRepository = addressesRepository;
		this.countryRepository = countryRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public User saveSignupDetails(SignupRequest signupRequest) {
		if (signupRequest == null)
			throw new IllegalArgumentException("User cannot be null");

		User user = new User();
		user.setFirstName(signupRequest.getFirstName());
		user.setLastName(signupRequest.getLastName());
		user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		user.setEmail(signupRequest.getEmail());
		user.setPhoneNumber(signupRequest.getPhoneNumber());

		if (Period.between(signupRequest.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
			throw new SwiggyException(ExceptionMessages.INVALID_AGE_EXCEPTION);
		}
		user.setAge(Period.between(signupRequest.getDateOfBirth(), LocalDate.now()).getYears());
		user.setDateOfBirth(signupRequest.getDateOfBirth());
		user.setGender(Gender.valueOf(signupRequest.getGender().toUpperCase()));

		User u = userRepository.save(user);

		// Table Addresses
		List<Addresses> addressesList = new ArrayList<Addresses>();

		for (AddressRequest addresses : signupRequest.getAddress()) {
			Addresses b = new Addresses();
			b.setHouseNo(addresses.getHouseNo());
			b.setLandMark(addresses.getLandMark());
			b.setCity(addresses.getCity());
			b.setState(addresses.getState());
			b.setZipCode(addresses.getZipCode());
			Country c = new Country();
			c.setCountryName(addresses.getCountry());

			Optional<Country> country = countryRepository.findBycountryName(addresses.getCountry());
			if (!(country.isPresent())) {
				countryRepository.findBycountryName(addresses.getCountry());
				b.setCountry(countryRepository.save(c));
			} else
				b.setCountry(country.get());

			addressesList.add(b);
			b.setUser(user);
			addressesRepository.save(b);
		}
		return u;
	}

	// Login
	public User authenticate(LoginRequest input) {

		boolean b = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getUserName(), input.getPassword()))
				.isAuthenticated();

		log.info("Authentication status is :" + b);

		return userRepository.findByEmail(input.getUserName())
				.orElseThrow(() -> new UserNameNotFoundException(ExceptionMessages.EMAIL_NOT_FOUND_EXCEPTION));
	}
}
