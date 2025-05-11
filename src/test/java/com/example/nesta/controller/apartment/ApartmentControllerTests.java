//package com.example.nesta.controller.apartment;
//
//import com.example.nesta.exception.apartment.ApartmentNotFoundException;
//import com.example.nesta.fixtures.ApartmentFixtures;
//import com.example.nesta.model.Apartment;
//import com.example.nesta.service.apartment.ApartmentService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.doThrow;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ApartmentController.class)
//public class ApartmentControllerTests {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private ApartmentService apartmentService;
//
//
//    @Test
//    public void getApartmentById_shouldReturnOk() throws Exception {
//        // Arrange
//        Apartment apartment = new Apartment();
//        apartment.setId(1L);
//
//        when(apartmentService.getApartmentById(1L)).thenReturn(Optional.of(apartment));
//
//        // Act & Assert
//        mockMvc.perform(get("/api/apartments/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    public void shouldReturnNotFoundWhenApartmentDoesNotExist() throws Exception {
//        // Arrange
//        when(apartmentService.getApartmentById(999L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        mockMvc.perform(get("/api/apartments/999"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void createApartment_shouldReturnOk() throws Exception {
//        // Arrange
//        var apartment = ApartmentFixtures.validApartment();
//
//        when(apartmentService.createApartment(any())).thenReturn(apartment);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/apartments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(apartment)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.numberOfRooms").value(2))
//                .andExpect(jsonPath("$.furnished").value(true));
//    }
//
//    @Test
//    public void createApartment_shouldReturnBadRequest_whenApartmentIsInvalid() throws Exception {
//        // Arrange
//        Apartment apartment = new Apartment(); // Missing required fields
//
//        // Act & Assert
//        mockMvc.perform(post("/api/apartments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(apartment)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void getAllApartments_shouldReturnOk() throws Exception {
//        // Arrange
//        Apartment apartment = new Apartment();
//        apartment.setId(1L);
//        apartment.setNumberOfRooms(2);
//
//        when(apartmentService.getAllApartments()).thenReturn(List.of(apartment));
//
//        // Act & Assert
//        mockMvc.perform(get("/api/apartments"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].numberOfRooms").value(2));
//    }
//
//    @Test
//    public void updateApartment_shouldReturnBadRequest_whenApartmentIsInvalid() throws Exception {
//        // Arrange
//        Apartment apartment = new Apartment(); // Missing required fields
//
//        // Act & Assert
//        mockMvc.perform(put("/api/apartments/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(apartment)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void updateApartment_shouldReturnNotFound_whenApartmentDoesNotExist() throws Exception {
//        // Arrange
//        Long nonExistingId = 999L;
//        var updatedApartment = ApartmentFixtures.validApartment();
//
//        when(apartmentService.updateApartment(eq(nonExistingId), any(Apartment.class)))
//                .thenThrow(new ApartmentNotFoundException(nonExistingId));
//
//        // Act & Assert
//        mockMvc.perform(put("/api/apartments/" + nonExistingId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedApartment)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void updateApartment_shouldReturnOk() throws Exception {
//        // Arrange
//        var updatedApartment = ApartmentFixtures.validApartment();
//
//        when(apartmentService.updateApartment(eq(1L), any(Apartment.class))).thenReturn(updatedApartment);
//
//        // Act & Assert
//        mockMvc.perform(put("/api/apartments/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(updatedApartment)))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    public void deleteApartment_shouldReturnNotFound_whenApartmentDoesNotExist() throws Exception {
//        // Arrange
//        Long nonExistingId = 999L;
//
//        doThrow(new ApartmentNotFoundException(nonExistingId))
//                .when(apartmentService)
//                .deleteApartment(nonExistingId);
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/apartments/" + nonExistingId))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void deleteApartment_shouldReturnNoContent() throws Exception {
//        // Arrange
//        long apartmentId = 1L;
//
//        // No need to mock anything — method is void and does nothing on success
//
//        // Act & Assert
//        mockMvc.perform(delete("/api/apartments/" + apartmentId))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void searchApartments_shouldReturn400_whenUnknownQueryParamProvided() throws Exception {
//        // Arrange
//        String invalidParam = "notAFilterParam";
//
//        // Act & Assert
//        mockMvc.perform(get("/api/apartments/search")
//                        .param(invalidParam, "value"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void searchApartments_shouldReturnOk() throws Exception {
//        // Arrange
//        Apartment apartment = ApartmentFixtures.validApartment();
//        when(apartmentService.searchApartments(any())).thenReturn(List.of(apartment));
//
//        // Act & Assert
//        mockMvc.perform(get("/api/apartments/search")
//                        .param("numberOfRooms", "1"))
//                .andExpect(status().isOk());
//    }
//}
