package org.mackson.service.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mackson.exceptions.InvalidEmailException;
import org.mackson.exceptions.InvalidInputException;
import org.mackson.exceptions.NotAuthenticatedActionException;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.data.PolitcalParties;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.dtos.contestant.ContestantRequest;
import org.mackson.model.dtos.contestant.ContestantVoterResponse;
import org.mackson.model.entity.Citizen;
import org.mackson.model.entity.Contestant;
import org.mackson.repository.ContestantRepository;
import org.mackson.service.utils.ContestantValidator;
import org.mackson.service.utils.MapperTool;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContestantServiceTest {

    @Mock
    private ContestantRepository contestantRepository;

    @Mock
    private CitizenService citizenService;

    @Spy
    private ContestantValidator contestantValidator;

    @Spy
    private MapperTool mapperTool;

    @InjectMocks
    private ContestantService contestantService;

    private Citizen savedCitizen;
    private Contestant savedContestant;
    private final String EMAIL = "agholordavid19@gmail.com";

    @BeforeEach
    void setUp() {
        savedCitizen = new Citizen();
        savedCitizen.setId("w2qeftrge3gfrfhrnh");
        savedCitizen.setEmail(EMAIL);
        savedCitizen.setPassword("1234");
        savedCitizen.setLoggedIn(true);
        savedCitizen.setYearOfBirth(2000);

        savedContestant = new Contestant();
        savedContestant.setId("contestant-id-001");
        savedContestant.setParty(PolitcalParties.PDP);
        savedContestant.setPosition(ElectionPosition.GOVERNOR);
        savedContestant.setNumberOfVotes(0);
        savedContestant.setEmail(EMAIL);
        savedContestant.setReisteredAt(Instant.now());
    }

    @Test
    void createContestant_success() {
        ContestantRequest request = new ContestantRequest();
        request.setParty("PDP");
        request.setPosition("GOVERNOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(citizenService.getCitizenAge(savedCitizen)).thenReturn(2025);
        when(contestantRepository.save(any(Contestant.class))).thenReturn(savedContestant);

        ContestantResponse response = contestantService.resgisterContestant(request, EMAIL);

        assertThat(response).isNotNull();
        assertThat(response.getParty()).isEqualTo("PDP");
        assertThat(response.getPosition()).isEqualTo("GOVERNOR");
        assertThat(response.getReisteredAt()).isNotNull();

        verify(citizenService).verifyCitizenLogStatus(EMAIL);
        verify(citizenService).getCitizenAge(savedCitizen);
        verify(contestantRepository).save(any(Contestant.class));
    }

    @Test
    void createContestant_citizenNotLoggedIn_throwsException() {
        ContestantRequest request = new ContestantRequest();
        request.setParty("PDP");
        request.setPosition("GOVERNOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL))
                .thenThrow(new NotAuthenticatedActionException("You are not logged in"));

        assertThatThrownBy(() -> contestantService.resgisterContestant(request, EMAIL))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void createContestant_citizenNotFound_throwsException() {
        ContestantRequest request = new ContestantRequest();
        request.setParty("PDP");
        request.setPosition("GOVERNOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL))
                .thenThrow(new InvalidEmailException("Email does not exists"));

        assertThatThrownBy(() -> contestantService.resgisterContestant(request, EMAIL))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email does not exists");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void createContestant_invalidParty_throwsException() {
        ContestantRequest request = new ContestantRequest();
        request.setParty("INVALID_PARTY");
        request.setPosition("GOVERNOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(citizenService.getCitizenAge(savedCitizen)).thenReturn(2025);

        assertThatThrownBy(() -> contestantService.resgisterContestant(request, EMAIL))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("valid Political Party");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void createContestant_invalidPosition_throwsException() {
        ContestantRequest request = new ContestantRequest();
        request.setParty("PDP");
        request.setPosition("KING");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(citizenService.getCitizenAge(savedCitizen)).thenReturn(2025);

        assertThatThrownBy(() -> contestantService.resgisterContestant(request, EMAIL))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("valid political position");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void createContestant_nullParty_throwsException() {
        ContestantRequest request = new ContestantRequest();
        request.setParty(null);
        request.setPosition("GOVERNOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(citizenService.getCitizenAge(savedCitizen)).thenReturn(2025);

        assertThatThrownBy(() -> contestantService.resgisterContestant(request, EMAIL))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("cannot be null or empty");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void createContestant_nullPosition_throwsException() {
        ContestantRequest request = new ContestantRequest();
        request.setParty("PDP");
        request.setPosition(null);

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(citizenService.getCitizenAge(savedCitizen)).thenReturn(2025);

        assertThatThrownBy(() -> contestantService.resgisterContestant(request, EMAIL))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("cannot be null or empty");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void updateContestant_success() {
        ContestantRequest updateRequest = new ContestantRequest();
        updateRequest.setParty("APC");
        updateRequest.setPosition("governor");

        Contestant updatedContestant = new Contestant();
        updatedContestant.setId("contestant-id-001");
        updatedContestant.setParty(PolitcalParties.APC);
        updatedContestant.setPosition(ElectionPosition.GOVERNOR);
        updatedContestant.setNumberOfVotes(0);
        updatedContestant.setEmail(EMAIL);
        updatedContestant.setReisteredAt(savedContestant.getReisteredAt());

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedContestant));
        when(contestantRepository.save(any(Contestant.class))).thenReturn(updatedContestant);

        ContestantResponse response = contestantService.updateContestant(EMAIL, updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getParty()).isEqualTo("APC");
        assertThat(response.getPosition()).isEqualTo("GOVERNOR");
        assertThat(response.getReisteredAt()).isNotNull();

        verify(citizenService).verifyCitizenLogStatus(EMAIL);
        verify(contestantRepository).findByEmail(EMAIL);
        verify(contestantRepository).save(any(Contestant.class));
    }

    @Test
    void updateContestant_contestantEmailNotFound_throwsException() {
        ContestantRequest updateRequest = new ContestantRequest();
        updateRequest.setParty("APC");
        updateRequest.setPosition("SENATOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contestantService.updateContestant(EMAIL, updateRequest))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("No contestant with such email");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void updateContestant_citizenNotLoggedIn_throwsException() {
        ContestantRequest updateRequest = new ContestantRequest();
        updateRequest.setParty("APC");
        updateRequest.setPosition("governor");

        when(citizenService.verifyCitizenLogStatus(EMAIL))
                .thenThrow(new NotAuthenticatedActionException("You are not logged in"));

        assertThatThrownBy(() -> contestantService.updateContestant(EMAIL, updateRequest))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        // never even reaches the repository
        verify(contestantRepository, never()).findByEmail(any());
        verify(contestantRepository, never()).save(any());
    }

    @Test
    void updateContestant_invalidNewParty_throwsException() {
        ContestantRequest updateRequest = new ContestantRequest();
        updateRequest.setParty("BANANA_REPUBLIC_PARTY");
        updateRequest.setPosition("GOVERNOR");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedContestant));

        assertThatThrownBy(() -> contestantService.updateContestant(EMAIL, updateRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("valid Political Party");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    void updateContestant_invalidNewPosition_throwsException() {
        ContestantRequest updateRequest = new ContestantRequest();
        updateRequest.setParty("APC");
        updateRequest.setPosition("EMPEROR");

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedContestant));

        assertThatThrownBy(() -> contestantService.updateContestant(EMAIL, updateRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("valid political position");

        verify(contestantRepository, never()).save(any());
    }

    @Test
    @DisplayName("get contestant voters - success")
    void getContestantVotersNumber_success() {
        savedContestant.setNumberOfVotes(150);

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedContestant));

        ContestantVoterResponse response = contestantService.getContestantVotersNumber(EMAIL);

        assertThat(response).isNotNull();
        assertThat(response.getVoters()).isEqualTo(150);
        assertThat(response.getPost()).isEqualTo(ElectionPosition.GOVERNOR.name());

        verify(citizenService).verifyCitizenLogStatus(EMAIL);
        verify(contestantRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("get contestant voters - returns zero when no votes yet")
    void getContestantVotersNumber_zeroVotes_success() {
        savedContestant.setNumberOfVotes(0);

        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedContestant));

        ContestantVoterResponse response = contestantService.getContestantVotersNumber(EMAIL);

        assertThat(response).isNotNull();
        assertThat(response.getVoters()).isZero();

        verify(contestantRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("get contestant voters - citizen not logged in")
    void getContestantVotersNumber_notLoggedIn_throwsException() {
        when(citizenService.verifyCitizenLogStatus(EMAIL))
                .thenThrow(new NotAuthenticatedActionException("You are not logged in"));

        assertThatThrownBy(() -> contestantService.getContestantVotersNumber(EMAIL))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        verify(contestantRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("get contestant voters - contestant email not found")
    void getContestantVotersNumber_emailNotFound_throwsException() {
        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contestantService.getContestantVotersNumber(EMAIL))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("No contestant with such email");
    }

    @Test
    @DisplayName("delete contestant - success")
    void deleteContestant_success() {
        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedContestant));
        doNothing().when(contestantRepository).delete(savedContestant);

        contestantService.deleteContestant(EMAIL);

        verify(citizenService).verifyCitizenLogStatus(EMAIL);
        verify(contestantRepository).findByEmail(EMAIL);
        verify(contestantRepository).delete(savedContestant);
    }

    @Test
    @DisplayName("delete contestant - citizen not logged in")
    void deleteContestant_notLoggedIn_throwsException() {
        when(citizenService.verifyCitizenLogStatus(EMAIL))
                .thenThrow(new NotAuthenticatedActionException("You are not logged in"));

        assertThatThrownBy(() -> contestantService.deleteContestant(EMAIL))
                .isInstanceOf(NotAuthenticatedActionException.class)
                .hasMessageContaining("not logged in");

        verify(contestantRepository, never()).findByEmail(any());
        verify(contestantRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete contestant - contestant email not found")
    void deleteContestant_emailNotFound_throwsException() {
        when(citizenService.verifyCitizenLogStatus(EMAIL)).thenReturn(savedCitizen);
        when(contestantRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contestantService.deleteContestant(EMAIL))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("contestant email doesnt exists");

        verify(contestantRepository, never()).delete(any());
    }
}