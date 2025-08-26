//package com.example.nesta.controller.apartment;
//
//import com.example.nesta.exception.apartment.ApartmentNotFoundException;
//import com.example.nesta.fixtures.ApartmentFixtures;
//import com.example.nesta.model.Apartment;
//import com.example.nesta.service.apartment.ApartmentService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.RequestPostProcessor;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ApartmentController.class)
//public class ApartmentControllerTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private ApartmentService apartmentService;
//
//    @Test
//    public void getApartmentById_shouldReturnOk() throws Exception {
//        // given
//        Apartment apartment = new Apartment();
//        apartment.setId(1L);
//        when(apartmentService.getApartmentById(1L)).thenReturn(Optional.of(apartment));
//
//        // when & then
//        mockMvc.perform(get("/api/apartments/1").with(landlordJwt()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    public void getApartmentById_shouldReturnNotFound_whenApartmentDoesNotExist() throws Exception {
//        // given
//        when(apartmentService.getApartmentById(999L)).thenReturn(Optional.empty());
//
//        // when & then
//        mockMvc.perform(get("/api/apartments/999").with(landlordJwt()))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void getAllApartmentsByLandlordId_shouldReturnOk() throws Exception {
//        // given
//        var expected = List.of(ApartmentFixtures.apartment());
//        when(apartmentService.getAllApartmentsByLandlordId(any(Jwt.class))).thenReturn(expected);
//
//        // when & then
//        mockMvc.perform(get("/api/apartments").with(landlordJwt()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void createApartment_shouldReturnOk() throws Exception {
//        // given
//        var apartment = ApartmentFixtures.apartment();
//        when(apartmentService.createApartment(any(Apartment.class), any(Jwt.class))).thenReturn(apartment);
//
//        // when & then
//        mockMvc.perform(post("/api/apartments")
//                        .with(landlordJwt())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(apartment)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void createApartment_shouldReturnBadRequest_whenApartmentIsInvalid() throws Exception {
//        // given
//        Apartment apartment = new Apartment(); // missing required fields
//
//        // when & then
//        mockMvc.perform(post("/api/apartments")
//                        .with(landlordJwt())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(apartment)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void updateApartment_shouldReturnBadRequest_whenApartmentIsInvalid() throws Exception {
//        // given
//        Apartment apartment = new Apartment(); // missing required fields
//
//        // when & then
//        mockMvc.perform(put("/api/apartments/1")
//                        .with(landlordJwt())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(apartment)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void updateApartment_shouldReturnNotFound_whenApartmentDoesNotExist() throws Exception {
//        // given
//        Long nonExistingId = 999L;
//        var updatedApartment = ApartmentFixtures.apartment();
//        when(apartmentService.updateApartment(eq(nonExistingId), any(Apartment.class)))
//                .thenThrow(new ApartmentNotFoundException(nonExistingId));
//
//        // when & then
//        mockMvc.perform(put("/api/apartments/" + nonExistingId)
//                        .with(landlordJwt())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedApartment)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void updateApartment_shouldReturnOk() throws Exception {
//        // given
//        var updatedApartment = ApartmentFixtures.apartment();
//        when(apartmentService.updateApartment(eq(1L), any(Apartment.class))).thenReturn(updatedApartment);
//
//        // when & then
//        mockMvc.perform(put("/api/apartments/1")
//                        .with(landlordJwt())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedApartment)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void deleteApartment_shouldReturnNotFound_whenApartmentDoesNotExist() throws Exception {
//        // given
//        Long nonExistingId = 999L;
//        doThrow(new ApartmentNotFoundException(nonExistingId))
//                .when(apartmentService)
//                .deleteApartment(nonExistingId);
//
//        // when & then
//        mockMvc.perform(delete("/api/apartments/" + nonExistingId).with(landlordJwt()))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void deleteApartment_shouldReturnNoContent() throws Exception {
//        // given
//        long apartmentId = 1L;
//
//        // when & then
//        mockMvc.perform(delete("/api/apartments/" + apartmentId).with(landlordJwt()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void searchApartments_shouldReturn400_whenUnknownQueryParamProvided() throws Exception {
//        // given
//        String invalidParam = "notAFilterParam";
//
//        // when & then
//        mockMvc.perform(get("/api/apartments/search")
//                        .with(landlordJwt())
//                        .param(invalidParam, "value"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void searchApartments_shouldReturnOk() throws Exception {
//        // given
//        Apartment apartment = ApartmentFixtures.apartment();
//        when(apartmentService.searchApartments(any())).thenReturn(List.of(apartment));
//
//        // when & then
//        mockMvc.perform(get("/api/apartments/search")
//                        .with(landlordJwt())
//                        .param("numberOfRooms", "1"))
//                .andExpect(status().isOk());
//    }
//
//    private RequestPostProcessor landlordJwt() {
//        return jwt().authorities(new SimpleGrantedAuthority("ROLE_LANDLORD"));
//    }
//}
