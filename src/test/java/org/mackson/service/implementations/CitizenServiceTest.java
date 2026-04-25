package org.mackson.service.implementations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mackson.model.dtos.citizendto.citizenvotingdetails.VotingIdDetails;
import org.mackson.model.dtos.citizendto.login.CitizenLoginRequest;
import org.mackson.model.dtos.citizendto.login.CitizenLoginResponse;
import org.mackson.model.dtos.citizendto.signup.CitizenRequest;
import org.mackson.model.dtos.citizendto.signup.CitizenResponse;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateRequest;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdatedResponse;
import org.mackson.model.entity.Citizen;
import org.mackson.repository.CitizenRepository;
import org.mackson.service.utils.CitizenInputValidator;
import org.mackson.service.utils.MapperTool;
import org.mackson.exceptions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceTest {

    private CitizenRequest citizenRequest;

    @Spy
    private MapperTool mapper;

    private CitizenInputValidator inputValidator;

    @Mock
    private CitizenRepository citizenRepository;

    @InjectMocks
    private CitizenService citizenService;

    private Citizen savedCitizen;

    private CitizenLoginRequest citizenLoginRequest;

    private CitizenUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {

        inputValidator = Mockito.spy(new CitizenInputValidator(mapper));
        ReflectionTestUtils.setField(citizenService, "citizenInputValidator", inputValidator);
        citizenRequest = new CitizenRequest();
        citizenRequest.setName("John Doe");
        citizenRequest.setEmail("john.doe@example.com");
        citizenRequest.setYear(1990);
        citizenRequest.setPassword("password123");
        citizenRequest.setPhoneNumber("+2348012345678");
        citizenRequest.setLocalGovernmentArea("OKPE");

        savedCitizen = new Citizen();
        savedCitizen.setId("w2qeftrge3gfrfhrnh");
        savedCitizen.setEmail(citizenRequest.getEmail());
        savedCitizen.setPassword(citizenRequest.getPassword());
        savedCitizen.setLoggedIn(true);
        savedCitizen.setYearOfBirth(2004);


        citizenLoginRequest = new CitizenLoginRequest();
        citizenLoginRequest.setEmail("john.doe@example.com");
        citizenLoginRequest.setPassword("password123");



        updateRequest = new CitizenUpdateRequest();
        updateRequest.setLocalGovernment("IKA_SOUTH");
        updateRequest.setName("mackson david");
        updateRequest.setPhoneNumber("+2347055793407");
        updateRequest.setYear(1994);


    }

    @Test
    @DisplayName("add citizen")
    void registerCitizen_thenSave() {
        when(citizenRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(false);
        when(citizenRepository.existsByPhoneNumber(citizenRequest.getPhoneNumber())).thenReturn(false);
        doReturn(savedCitizen).when(citizenRepository).save(any(Citizen.class));
        CitizenResponse response = citizenService.createCitizenThenSave(citizenRequest);
        assertThat(response.getEmail()).isEqualTo(citizenRequest.getEmail());
    }

    @Test
    @DisplayName("log citizen in")
    void citizenLogsIn_ComparePasswordAndEmailTest(){
        when(citizenRepository.findByEmailOrPhoneNumber(citizenLoginRequest.getEmail())).thenReturn(Optional.ofNullable(savedCitizen));
        when(citizenRepository.save(any(Citizen.class))).thenReturn(savedCitizen);
       CitizenLoginResponse response = citizenService.logUserWithEmailAndPassword(citizenLoginRequest);
       assertThat(response.getEmail()).isEqualTo(citizenLoginRequest.getEmail());
       assertTrue(response.isLoggedIn());
    }


    @Test
    @DisplayName("update citizen")
    void updateCitizen_thenSave(){
        String email = "john.doe@example.com";
        when(citizenRepository.findByEmailOrPhoneNumber(email)).thenReturn(Optional.ofNullable(savedCitizen));
        when(citizenRepository.save(savedCitizen)).thenReturn(savedCitizen);
        CitizenUpdatedResponse response = citizenService.updateCitizenDetails(updateRequest, email);
        assertNotNull(response);
        assertThat(response.getYear()).isEqualTo(updateRequest.getYear());
    }

    @Test
    @DisplayName("")
    void generateCitizen_votingId(){
        String email = "john.doe@example.com";
        when(citizenRepository.findByEmailOrPhoneNumber(email)).thenReturn(Optional.ofNullable(savedCitizen));
        when(citizenRepository.save(any(Citizen.class))).thenReturn(savedCitizen);
        when(citizenRepository.existsByVotingId(any(String.class))).thenReturn(false);
       VotingIdDetails votingIdDetails = citizenService.validateUserThenGenerateVotingId(email);
       assertThat(votingIdDetails).isNotNull();
    }

    @Test
    @DisplayName("Log out")
    void LogUserOut(){
        String email = "agholordavid1@gmail.com";
        when(citizenRepository.findByEmailOrPhoneNumber(email)).thenReturn(Optional.ofNullable(savedCitizen));
        when(citizenRepository.save(any(Citizen.class))).thenReturn(savedCitizen);
        citizenService.logCitizenOut(email);
        assertThat(savedCitizen.isLoggedIn()).isFalse();
    }

    @Test
    @DisplayName("register citizen fails - email already exists")
    void registerCitizen_emailAlreadyExists_throwsException() {
        when(citizenRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(EmailExistsAlreadyException.class)
                .hasMessageContaining(citizenRequest.getEmail());

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - phone number already exists")
    void registerCitizen_phoneNumberAlreadyExists_throwsException() {
        when(citizenRepository.existsByEmail(citizenRequest.getEmail())).thenReturn(false);
        when(citizenRepository.existsByPhoneNumber(citizenRequest.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(PhoneNumberException.class)
                .hasMessageContaining("Phone Number Exists Already");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - invalid email format")
    void registerCitizen_invalidEmail_throwsException() {
        citizenRequest.setEmail("invalidemail");

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("@");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - null email")
    void registerCitizen_nullEmail_throwsException() {
        citizenRequest.setEmail(null);

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("cannot be null");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - name contains digit")
    void registerCitizen_nameWithDigit_throwsException() {
        citizenRequest.setName("John123");

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(InvalidNameException.class)
                .hasMessageContaining("valid name");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - phone number wrong length")
    void registerCitizen_phoneNumberWrongLength_throwsException() {
        citizenRequest.setPhoneNumber("+23480123");

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(PhoneNumberException.class)
                .hasMessageContaining("14 characters");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - phone number wrong prefix")
    void registerCitizen_phoneNumberWrongPrefix_throwsException() {
        citizenRequest.setPhoneNumber("+441234567890");

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(PhoneNumberException.class)
                .hasMessageContaining("Phone number must be exactly 14 characters");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("register citizen fails - invalid local government area")
    void registerCitizen_invalidLocalGovernment_throwsException() {
        citizenRequest.setLocalGovernmentArea("FAKE_LGA");

        assertThatThrownBy(() -> citizenService.createCitizenThenSave(citizenRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("valid Local Government Area");

        verify(citizenRepository, never()).save(any());
    }


    @Test
    @DisplayName("login fails - email not found")
    void citizenLogin_emailNotFound_throwsException() {
        when(citizenRepository.findByEmailOrPhoneNumber(citizenLoginRequest.getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> citizenService.logUserWithEmailAndPassword(citizenLoginRequest))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Invalid user");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("login fails - wrong password")
    void citizenLogin_wrongPassword_throwsException() {
        citizenLoginRequest.setPassword("wrongpassword");
        when(citizenRepository.findByEmailOrPhoneNumber(citizenLoginRequest.getEmail()))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.logUserWithEmailAndPassword(citizenLoginRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("wrong password");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("update citizen fails - citizen not logged in")
    void updateCitizen_notLoggedIn_throwsException() {
        savedCitizen.setLoggedIn(false);
        String email = "john.doe@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.updateCitizenDetails(updateRequest, email))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("update citizen fails - email not found")
    void updateCitizen_emailNotFound_throwsException() {
        String email = "ghost@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> citizenService.updateCitizenDetails(updateRequest, email))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email does not exists");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("update citizen fails - invalid local government")
    void updateCitizen_invalidLocalGovernment_throwsException() {
        String email = "john.doe@example.com";
        updateRequest.setLocalGovernment("FAKE_LGA");

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.updateCitizenDetails(updateRequest, email))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("valid Local Government Area");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("update citizen fails - invalid phone number")
    void updateCitizen_invalidPhoneNumber_throwsException() {
        String email = "john.doe@example.com";
        updateRequest.setPhoneNumber("08012345678"); // missing +234 prefix

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.updateCitizenDetails(updateRequest, email))
                .isInstanceOf(PhoneNumberException.class);

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("generate voting id fails - citizen not logged in")
    void generateVotingId_notLoggedIn_throwsException() {
        savedCitizen.setLoggedIn(false);
        String email = "john.doe@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.validateUserThenGenerateVotingId(email))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("generate voting id fails - citizen underage")
    void generateVotingId_citizenUnderage_throwsException() {
        savedCitizen.setYearOfBirth(2015); // too young
        String email = "john.doe@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.validateUserThenGenerateVotingId(email))
                .isInstanceOf(VotingException.class)
                .hasMessageContaining("Not Of Age To Vote");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("generate voting id - returns existing id without generating a new one")
    void generateVotingId_alreadyHasVotingId_returnsExistingId() {
        savedCitizen.setVotingId("2000abc123");
        savedCitizen.setYearOfBirth(1990);
        String email = "john.doe@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        VotingIdDetails result = citizenService.validateUserThenGenerateVotingId(email);

        assertThat(result.getVotingId()).isEqualTo("2000abc123");
        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("logout fails - email not found")
    void logOut_emailNotFound_throwsException() {
        String email = "ghost@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> citizenService.logCitizenOut(email))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email does not exists");

        verify(citizenRepository, never()).save(any());
    }

    @Test
    @DisplayName("logout fails - citizen already logged out")
    void logOut_alreadyLoggedOut_throwsException() {
        savedCitizen.setLoggedIn(false);
        String email = "john.doe@example.com";

        when(citizenRepository.findByEmailOrPhoneNumber(email))
                .thenReturn(Optional.of(savedCitizen));

        assertThatThrownBy(() -> citizenService.logCitizenOut(email))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        verify(citizenRepository, never()).save(any());
    }
}