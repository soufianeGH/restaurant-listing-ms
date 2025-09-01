package com.codedecode.restaurantListing.service;

import com.codedecode.restaurantListing.dto.RestaurantDTO;
import com.codedecode.restaurantListing.entity.Restaurant;
import com.codedecode.restaurantListing.mapper.RestaurantMapper;
import com.codedecode.restaurantListing.repo.RestaurantRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RestaurantServiceTest {

    @InjectMocks
    RestaurantService restaurantService;

    @Mock
    RestaurantRepo restaurantRepo;


    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this); //in order for Mock and InjectMocks annotations to take effect, you need to call MockitoAnnotations.openMocks(this);
    }


    @Test
    void fetchAllRestaurants_withRealMapper() {
        // Arrange: create the entities and stub the repo
        Restaurant r1 = new Restaurant(1, "R1", "A1", "C1", "D1");
        Restaurant r2 = new Restaurant(2, "R2", "A2", "C2", "D2");
        when(restaurantRepo.findAll()).thenReturn(List.of(r1, r2));

        // Act: call the SUT (this is where streams/map/collect run)
        List<RestaurantDTO> result = restaurantService.fetchAllRestaurants();

        // Assert: check fields (no need to mock mapper or streams)
        assertEquals(2, result.size());
        assertEquals("R1", result.get(0).getName());
        assertEquals("R2", result.get(1).getName());
        verify(restaurantRepo).findAll();
        verifyNoMoreInteractions(restaurantRepo);
    }


    @Test
    public void testAddRestaurantInDB() {
        // Create a mock restaurant to be saved
        RestaurantDTO mockRestaurantDTO = new RestaurantDTO(1, "Restaurant 1", "Address 1", "city 1", "Desc 1");
        Restaurant mockRestaurant = RestaurantMapper.INSTANCE.mapRestaurantDTOToRestaurant(mockRestaurantDTO);

        // Mock the repository behavior
        when(restaurantRepo.save(mockRestaurant)).thenReturn(mockRestaurant);

        // Call the service method
        RestaurantDTO savedRestaurantDTO = restaurantService.addRestaurantInDB(mockRestaurantDTO);

        // Verify the result
        assertEquals(mockRestaurantDTO, savedRestaurantDTO);

        // Verify that the repository method was called
        verify(restaurantRepo, times(1)).save(mockRestaurant);
    }

    @Test
    public void testFetchRestaurantById_ExistingId() {
        // Create a mock restaurant ID
        Integer mockRestaurantId = 1;

        // Create a mock restaurant to be returned by the repository
        Restaurant mockRestaurant = new Restaurant(1, "Restaurant 1", "Address 1", "city 1", "Desc 1");

        // Mock the repository behavior
        when(restaurantRepo.findById(mockRestaurantId)).thenReturn(Optional.of(mockRestaurant));

        // Call the service method
        ResponseEntity<RestaurantDTO> response = restaurantService.fetchRestaurantById(mockRestaurantId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRestaurantId, response.getBody().getId());

        // Verify that the repository method was called
        verify(restaurantRepo, times(1)).findById(mockRestaurantId);
    }

    @Test
    public void testFetchRestaurantById_NonExistingId() {
        // Create a mock non-existing restaurant ID
        Integer mockRestaurantId = 1;

        // Mock the repository behavior
        when(restaurantRepo.findById(mockRestaurantId)).thenReturn(Optional.empty());

        // Call the service method
        ResponseEntity<RestaurantDTO> response = restaurantService.fetchRestaurantById(mockRestaurantId);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());

        // Verify that the repository method was called
        verify(restaurantRepo, times(1)).findById(mockRestaurantId);
    }



}
