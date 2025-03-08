package com.sharafutdinov.bank_lending_api.credit_condition;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditCondition;
import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditConditionRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditRequestRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.UserRepository;
import com.sharafutdinov.bank_lending_api.client_information.ClientInformationService;
import com.sharafutdinov.bank_lending_api.credit_request.ProcessingStatus;
import com.sharafutdinov.bank_lending_api.email.EmailService;
import com.sharafutdinov.bank_lending_api.email.SendApprovedMailRequest;
import com.sharafutdinov.bank_lending_api.email.SendRefuseMailRequest;
import com.sharafutdinov.bank_lending_api.exception.CreditException;
import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditConditionServiceImpl implements CreditConditionService {

    private final ClientInformationService clientInformationService;
    private final CreditConditionRepository repository;
    private final CreditConditionMapper mapper;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CreditRequestRepository creditRequestRepository;

    @Override
    public CreditConditionResponse selectBestConditions(Long userId) throws MessagingException {
        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        int score = clientInformationService.countCreditScore(request.getCreditQueryInfo().getId());

        List<String> probablyCreditNames = checkOnIssuanceLoan(score);
        if (probablyCreditNames != null) {
            List<CreditCondition> creditConditions = new ArrayList<>();
            for (String creditName : probablyCreditNames) {
                CreditCondition credit = Optional.ofNullable(repository.findByProductName(creditName))
                        .orElseThrow(() -> new ResourceNotFoundException("Такого имени кредита нет в бд"));

                creditConditions.add(credit);
            }

            double monthlyIncome = Double.parseDouble( request.getCreditQueryInfo().getFinancialSituation().get("средний ежемесячный доход").toString());
            double monthlyPrevCreditPayment = request.getCurrentLoans() == null? 0 :  Double.parseDouble(request.getCurrentLoans().get("ежемесячная оплата").toString());
            double totalIncome = monthlyIncome - monthlyPrevCreditPayment;

            CreditCondition bestCondition = null;
            double monthlyPaymentByCredit;

            for (CreditCondition creditCondition : creditConditions) {
                if (creditCondition.getMaxAmount() >= request.getSum() && creditCondition.getMinAmount() <= request.getSum()) {
                    monthlyPaymentByCredit = getMonthlyCreditPayment(
                            creditCondition.getInterestRate(),
                            request.getSum(),
                            creditCondition.getTermMax());
                    if (totalIncome >= monthlyPaymentByCredit) {
                        if (bestCondition == null)
                            bestCondition = creditCondition;
                        else if ((bestCondition.getMaxAmount() - creditCondition.getMaxAmount()) > 0)
                            bestCondition = creditCondition;
                    }
                }
            }

            if (bestCondition == null) {
                sendRefusedMail(request.getUser(), request.getSum());
                saveRefusedData(request);
                throw new CreditException("Невозможно выдать кредит данному клиенту");
            }

            CreditConditionResponse response = mapper.toResponse(bestCondition);
            response.setMonthlyPayment(Math.round(getMonthlyCreditPayment(
                    bestCondition.getInterestRate(),
                    request.getSum(),
                    bestCondition.getTermMax())
                    * 100.0) / 100.0); // округление до 2ух знаков

            return response;
        } else {
            sendRefusedMail(request.getUser(), request.getSum());
            saveRefusedData(request);
            throw new CreditException("Невозможно выдать кредит данному клиенту");
        }
    }

    @Override
    public Long sendBestCondition(CreditConditionRequest request, Long userId) throws MessagingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не был найден"));

        CreditCondition bestCondition = repository.findById(request.id()).get();

        Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .map(req -> {
                    req.setCreditCondition(bestCondition);
                    req.setStatus(ProcessingStatus.APPROVED);
                    req.setProcessed(true);
                    req.setDescriptionStatus("Ваш кредитный запрос был одобрен, для продолжения проверьте почту");
                    return creditRequestRepository.save(req);
                });


        emailService.sendApprovedMail(SendApprovedMailRequest.builder()
                .firstName(user.getFirstName())
                .surName(user.getSurName())
                .to(user.getEmail())
                .from("CoolBank228@mail.com")
                .creditConditionRequest(request)
                .build());

        return userId;
    }

    @Override
    public CreditConditionResponse getConditionByName(String name) {
        return Optional.ofNullable(repository.findByProductName(name))
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Такого названия нет в бд"));
    }

    @Override
    public Double recalculationOfMonthlyPayment(PaymentCalculationRequest request) {
        return Math.round(getMonthlyCreditPayment(
                request.interestRate(),
                request.amount(),
                request.term())
                * 100.0) / 100.0; // округление до 2ух знаков;
    }

    private void saveRefusedData(CreditRequest request){
        request.setProcessed(true);
        request.setStatus(ProcessingStatus.REFUSED);
        request.setDescriptionStatus("Невозможно выдать кредит данному клиенту, исходя из предложений банка");
        creditRequestRepository.save(request);
    }

    private void sendRefusedMail(User user, Integer amount) throws MessagingException {
        emailService.sendRefuseMail(SendRefuseMailRequest.builder()
                .firstName(user.getFirstName())
                .surName(user.getSurName())
                .to(user.getEmail())
                .from("CoolBank228@mail.com")
                .amount(amount)
                .build());
    }

    private double getMonthlyCreditPayment(double yearPercent, int sum, int term) {
        double monthlyPayment = yearPercent / 12 / 100;
        return (sum * (monthlyPayment) * Math.pow(1 + monthlyPayment, term))
                / (Math.pow(1 + monthlyPayment, term) - 1);
    }

    private List<String> checkOnIssuanceLoan(int score) {
        if (score >= 160)
            return List.of("Ипотека", "Агрокредит", "Автокредит", "Образовательный кредит",
                    "Кредит на ремонт", "Потребительский кредит", "Кредит на технику");
        else if (score >= 140)
            return List.of("Агрокредит", "Автокредит", "Образовательный кредит",
                    "Кредит на ремонт", "Потребительский кредит", "Кредит на технику");
        else if (score >= 120)
            return List.of("Автокредит", "Образовательный кредит",
                    "Кредит на ремонт", "Потребительский кредит", "Кредит на технику");
        else if (score >= 100)
            return List.of("Кредит на ремонт", "Потребительский кредит", "Кредит на технику");
        else if (score >= 80)
            return List.of("Кредит на технику");
        else
            return null;
    }
}
