package com.sharafutdinov.bank_lending_api.credit_query;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditQueryInfo;
import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditQueriesRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditRequestRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.UserRepository;
import com.sharafutdinov.bank_lending_api.pdf.PdfCreator;
import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CreditQueryServiceImplTest {


    @InjectMocks
    private CreditQueryServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreditRequestRepository creditRequestRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private PdfCreator creator;

    @Mock
    private CreditQueriesRepository creditQueriesRepository;

    @Test
    void testConfirmationOfSolvency_CreditRequestNotFound() {
        // Arrange
        Long userId = 1L;
        Long authUserId = 2L;
        BankUserDetails userDetails = mock(BankUserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(authUserId);

        User authUser = new User();
        authUser.setId(authUserId);

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(authUserId)).thenReturn(Optional.of(authUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false)).thenReturn(null);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                service.confirmationOfSolvency(userId, authentication)
        );

        assertEquals("Такого запроса не было найдено в бд", exception.getMessage());
        verify(creditRequestRepository, times(1)).findByUserIdAndIsProcessedIs(userId, false);
    }

    @Test
    void testChangeSolvencyHiredWorkerTaxAgentByUserId_Success() {
        // Arrange
        Long userId = 1L;
        Long authUserId = 2L;
        Long creditRequestId = 3L;
        Long queryId = 4L;
        String name = "ООО \"Ромашка\"";
        String inn = "1234567890";
        String kpp = "0987654321";

        CreditQueryTaxAgentHiredWorkerRequest request = new CreditQueryTaxAgentHiredWorkerRequest(
                name, inn, kpp
        );

        BankUserDetails userDetails = mock(BankUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(authUserId);

        User authUser = new User();
        authUser.setId(authUserId);
        when(userRepository.findById(authUserId)).thenReturn(Optional.of(authUser));

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);
        when(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .thenReturn(creditRequest);

        CreditQueryInfo query = new CreditQueryInfo();
        Map<String, Object> financialSituation = new HashMap<>();
        query.setFinancialSituation(financialSituation);
        when(creditQueriesRepository.findByCreditRequestId(creditRequestId))
                .thenReturn(query);
        when(creditQueriesRepository.save(query)).thenReturn(query);
        query.setId(queryId);

        // Act
        Long result = service.changeSolvencyHiredWorkerTaxAgentByUserId(
                userId, request, authentication
        );

        // Assert
        assertEquals(queryId, result);
        assertEquals(name, query.getFinancialSituation().get("наименование"));
        assertEquals(inn, query.getFinancialSituation().get("инн"));
        assertEquals(kpp, query.getFinancialSituation().get("кпп"));

        verify(authentication).getPrincipal();
        verify(userRepository).findById(authUserId);
        verify(creditRequestRepository).findByUserIdAndIsProcessedIs(userId, false);
        verify(creditQueriesRepository).findByCreditRequestId(creditRequestId);
        verify(creditQueriesRepository).save(query);
        verify(creator).createFinancialSituationFor2ndflPdf(
                query, creditRequest, authUser, true
        );
    }

}