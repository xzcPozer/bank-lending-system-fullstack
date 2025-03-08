package com.sharafutdinov.bank_lending_api.client_information;

import com.sharafutdinov.bank_lending_api.bank_db.entity.ClientInformation;
import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditQueryInfo;
import com.sharafutdinov.bank_lending_api.bank_db.repository.ClientInformationRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditQueriesRepository;
import com.sharafutdinov.bank_lending_api.common.PageResponse;
import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientInformationService {

    private final ClientInformationRepository repository;
    private final CreditQueriesRepository creditQueriesRepository;
    private final ClientInformationMapper mapper;


    public PageResponse<ClientInformationResponse> getAllClientInfo(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClientInformation> clientInfo = repository.findAll(pageable);
        List<ClientInformationResponse> clientInfoList = clientInfo.stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponse<>(
                clientInfoList,
                clientInfo.getNumber(),
                clientInfo.getSize(),
                clientInfo.getTotalElements(),
                clientInfo.getTotalPages(),
                clientInfo.isFirst(),
                clientInfo.isLast()
        );
    }

    public int countCreditScore(Long creditQueryId) {
        CreditQueryInfo creditQuery = creditQueriesRepository.findById(creditQueryId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого запроса не было найдено в бд"));

        ClientInformation clientInformation = Optional.ofNullable(repository.findByUserId(creditQuery.getCreditRequest().getUser().getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Информации об этом клиенте не было найдено"));

        int sumScore = 0;

        double monthlyIncome = Double.parseDouble((String) creditQuery.getFinancialSituation().get("средний ежемесячный доход"));

        double percentPaymentOfCurLoan = 0;
        int termCurLoan = 0;
        if (creditQuery.getCreditRequest().getCurrentLoans() != null) {
            percentPaymentOfCurLoan = Double.parseDouble(creditQuery.getCreditRequest().getCurrentLoans().get("ежемесячная оплата").toString()) / monthlyIncome;
            termCurLoan = (int) creditQuery.getCreditRequest().getCurrentLoans().get("срок кредита");
        }

        boolean loansRepaid = true;
        if(clientInformation.getPreviousLoans() != null){
            for (var prevLoan : clientInformation.getPreviousLoans()) {
                boolean paymentStatus = (boolean) prevLoan.get("выплачен");
                if (!paymentStatus) {
                    loansRepaid = false;
                    break;
                }
            }
        }
        if (loansRepaid)
            sumScore += 40;

        Double clientBalance = clientInformation.getBalance() == null
                ? 0 : clientInformation.getBalance();

        sumScore += getScoreForMonthlyIncome(monthlyIncome);
        sumScore += getScoreForPaymentCurCredit(percentPaymentOfCurLoan);
        sumScore += getScoreForTermCurLoan(termCurLoan);
        sumScore += getScoreForBalance(clientBalance);

        clientInformation.setCreditScore(sumScore);
        repository.save(clientInformation);

        return sumScore;
    }

    private int getScoreForBalance(Double balance) {
        if (balance > 50_000)
            return 40;
        else if (balance > 30_000)
            return 30;
        else if(balance > 10_000)
            return 10;
        else
            return 0;
    }

    private int getScoreForTermCurLoan(int term) {
        if (term > 6 && term < 12)
            return 20;
        else if (term != 0)
            return 30;
        else
            return 40;
    }

    private int getScoreForPaymentCurCredit(double percent) {
        if (percent > 30)
            return 10;
        else if (percent > 10)
            return 20;
        else if (percent != 0)
            return 30;
        else
            return 40;
    }

    private int getScoreForMonthlyIncome(double monthlyIncome) {
        if (monthlyIncome > 100_000)
            return 40;
        else if (monthlyIncome >= 60_000)
            return 30;
        else if (monthlyIncome >= 30_000)
            return 20;
        else
            return 10;
    }


}
