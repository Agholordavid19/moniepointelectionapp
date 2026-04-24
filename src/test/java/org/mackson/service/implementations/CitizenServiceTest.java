package org.mackson.service.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mackson.model.dtos.signup.CitizenRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceTest {
    @Mock
    private CitizenRequest citizenRequest;

    @InjectMocks
    private CitizenService citizenService;

    @BeforeEach
    void setUp(){
        citizenRequest = new CitizenRequest();
        citizenRequest.setName("John Doe");
        citizenRequest.setEmail("john.doe@example.com");
        citizenRequest.setYear(1990);
        citizenRequest.setPassword("password123");
        citizenRequest.setPhoneNumber("08012345678");
        citizenRequest.setLocalGovernmentArea("Ikeja");
    }
    @Test
    @DisplayName("add citizen")
    void registerCitizen_thenSave(){

    citizenService.createCitizenThenSave(citizenRequest);
    }

}
