package com.sharafutdinov.bank_lending_api.credit_query;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditQueryInfo;
import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditQueriesRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditRequestRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.UserRepository;
import com.sharafutdinov.bank_lending_api.common.PageResponse;
import com.sharafutdinov.bank_lending_api.credit_request.ProcessingStatus;
import com.sharafutdinov.bank_lending_api.email.EmailService;
import com.sharafutdinov.bank_lending_api.email.SendForRevisionMailRequest;
import com.sharafutdinov.bank_lending_api.exception.DataEqualsException;
import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import com.sharafutdinov.bank_lending_api.pdf.PdfCreator;
import com.sharafutdinov.bank_lending_api.pdf.PdfExporter;
import com.sharafutdinov.bank_lending_api.pdf.PdfExtractor;
import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.rmi.server.ExportException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CreditQueryServiceImpl implements CreditQueryService {

    private final CreditQueriesRepository creditQueriesRepository;
    private final UserRepository userRepository;
    private final CreditRequestRepository creditRequestRepository;
    private final PdfExtractor extractor;
    private final PdfCreator creator;
    private final PdfExporter exporter;
    private final CreditQueryInfoMapper mapper;
    private final EmailService emailService;

    @Override
    public Long confirmationOfSolvency(Long userId, Authentication authentication) throws ExportException {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .map(r -> {
                    r.setLendingOfficer(authUser);
                    return creditRequestRepository.save(r);
                })
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        Map<String, String> solvencyClientData;
        if (request.getType() == WorkerType.INDIVIDUAL_ENTREPRENEUR) {
            extractor.confirmationOfIpSolvency(request.getSolvencyRefPath());
            solvencyClientData = PdfExtractor.getSolvencyIndividualEntrepreneurClientData();
        } else {
            extractor.confirmationOf2Ndfl(request.getSolvencyRefPath());
            solvencyClientData = PdfExtractor.getSolvencyHiredWorkersClientData();
        }

        if (equalsSolvencyUserData(user, solvencyClientData)) {

            if (request.getType() == WorkerType.HIRED_WORKERS) {
                extractor.confirmationOfEmployment(request.getEmploymentRefPath());
                if (!equalsDocumentsData(solvencyClientData)) {
                    throw new DataEqualsException("данные в документах не равны, пожалуйста, проверьте корректность данных");
                }
            }

            Map<String, Object> financialState = getFinancialStateMap(request.isImmovableProperty(), request.isMovableProperty(), request.getType());

            CreditQueryInfo creditQuery = CreditQueryInfo.builder()
                    .financialSituation(financialState)
                    .creditRequest(request)
                    .build();

            if (request.getType() == WorkerType.HIRED_WORKERS)
                creator.createFinancialSituationFor2ndflPdf(creditQuery, request, authUser, false);
            else if (request.getType() == WorkerType.INDIVIDUAL_ENTREPRENEUR)
                creator.createFinancialSituationForIpPdf(creditQuery, request, authUser, false);

            return creditQueriesRepository.save(creditQuery).getId();
        } else {
            throw new DataEqualsException("данные из документа не совпадают с личными данными, " +
                    "проверьте заполненные личных данных и данных в документе");
        }
    }

    @Override
    public Long sendRefuseData(Long userId, String description) throws MessagingException {
        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));
        emailService.sendForRevisionMail(SendForRevisionMailRequest.builder()
                .to(request.getUser().getEmail())
                .from("CoolerBank228@mail.com")
                .description(description)
                .firstName(request.getUser().getFirstName())
                .surName(request.getUser().getSurName())
                .build());
        request.setProcessed(true);
        request.setStatus(ProcessingStatus.SENT_FOR_REVISION);
        request.setDescriptionStatus(description);
        return creditRequestRepository.save(request).getId();
    }

    @Override
    public Long confirmationOfAccountStatus(Long userId, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(request.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createAccountStatusPdf(request, authUser, false);
        return query.getId();
    }

    @Override
    public Long accountStatusVerify(Long userId, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(request.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createAccountStatusPdf(request, authUser, true);
        return query.getId();
    }

    @Override
    public Long solvencyVerify(Long userId, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(request.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        if (request.getType() == WorkerType.HIRED_WORKERS)
            creator.createFinancialSituationFor2ndflPdf(query, request, authUser, true);
        else if (request.getType() == WorkerType.INDIVIDUAL_ENTREPRENEUR)
            creator.createFinancialSituationForIpPdf(query, request, authUser, true);

        return query.getId();
    }

    @Override
    public Long creditQueryVerify(Long userId, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(request.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createCreditRequestPdf(query, request, authUser, true);
        return query.getId();
    }

    @Override
    public File getSolvencyPdfInfoByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не был найден"));

        return new File(exporter.getReportFilepath(user.getId()) + "/financial_report.pdf");
    }

    @Override
    public File getPaymentPdfInfoByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не был найден"));

        return new File(exporter.getReportFilepath(user.getId()) + "/account_status_report.pdf");
    }

    @Override
    public File getCreditRequestPdfInfoByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не был найден"));

        return new File(exporter.getReportFilepath(user.getId()) + "/credit_query_report.pdf");
    }


    @Override
    public File getSolvencyUploadPdfInfoByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не был найден"));

        String path = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"))
                .getSolvencyRefPath();

        String filename = path.substring(path.lastIndexOf('\\') + 1);
        return new File(exporter.getUploadFilepath(user.getId()) + "\\" + filename);
    }

    @Override
    public File getPaymentUploadPdfInfoByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не был найден"));

        String path = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"))
                .getEmploymentRefPath();

        String filename = path.substring(path.lastIndexOf('\\') + 1);
        return new File(exporter.getUploadFilepath(user.getId()) + "\\" + filename);
    }

    @Override
    public PageResponse<CreditQueryClientResponse> getAllClientCreditQueryInfo(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreditQueryInfo> creditQueriesInfo = creditQueriesRepository.findAll(pageable);
        List<CreditQueryClientResponse> creditQueryList = creditQueriesInfo.stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponse<>(
                creditQueryList,
                creditQueriesInfo.getNumber(),
                creditQueriesInfo.getSize(),
                creditQueriesInfo.getTotalElements(),
                creditQueriesInfo.getTotalPages(),
                creditQueriesInfo.isFirst(),
                creditQueriesInfo.isLast()
        );
    }

    @Override
    public Long changeSolvencyHiredWorkerTaxAgentByUserId(Long userId, CreditQueryTaxAgentHiredWorkerRequest request, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest creditRequest = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(creditRequest.getId()))
                .map(q -> {
                    q.getFinancialSituation().put("наименование", request.name());
                    q.getFinancialSituation().put("инн", request.inn());
                    q.getFinancialSituation().put("кпп", request.kpp());
                    return creditQueriesRepository.save(q);
                })
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createFinancialSituationFor2ndflPdf(query, creditRequest, authUser, true);

        return query.getId();
    }

    @Override
    public Long changeSolvencyHiredWorkerFinancialSituationByUserId(Long userId, CreditQueryFinancialSituationHiredWorkerRequest request, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest creditRequest = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(creditRequest.getId()))
                .map(q -> {
                    q.getFinancialSituation().put("общая сумма дохода", request.totalIncome());
                    q.getFinancialSituation().put("сумма налога исчисленная", request.taxCalculationAmount());
                    q.getFinancialSituation().put("средний ежемесячный доход", request.monthlyPayment());
                    return creditQueriesRepository.save(q);
                })
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createFinancialSituationFor2ndflPdf(query, creditRequest, authUser, true);

        return query.getId();
    }

    @Override
    public Long changeSolvencyIpTaxAgentByUserId(Long userId, CreditQueryTaxAgentIpRequest request, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest creditRequest = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(creditRequest.getId()))
                .map(q -> {
                    q.getFinancialSituation().put("наименование", request.name());
                    q.getFinancialSituation().put("инн", request.inn());
                    q.getFinancialSituation().put("огрнип", request.ogrnip());
                    return creditQueriesRepository.save(q);
                })
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createFinancialSituationFor2ndflPdf(query, creditRequest, authUser, true);

        return query.getId();
    }

    @Override
    public Long changeSolvencyIpFinancialSituationByUserId(Long userId, CreditQueryFinancialSituationIpRequest request, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest creditRequest = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(creditRequest.getId()))
                .map(q -> {
                    q.getFinancialSituation().put("общая сумма дохода", request.totalIncome());
                    q.getFinancialSituation().put("средний ежемесячный доход", request.monthlyPayment());
                    return creditQueriesRepository.save(q);
                })
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        creator.createFinancialSituationFor2ndflPdf(query, creditRequest, authUser, true);

        return query.getId();
    }

    @Override
    public Long generateCreditQuery(Long userId, Authentication authentication) {
        Long authUserId = ((BankUserDetails) authentication.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        CreditRequest request = Optional.ofNullable(creditRequestRepository.findByUserIdAndIsProcessedIs(userId, false))
                .orElseThrow(() -> new EntityNotFoundException("Такого запроса не было найдено в бд"));

        CreditQueryInfo query = Optional.ofNullable(creditQueriesRepository.findByCreditRequestId(request.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Информация об этом запросе не была найдена"));

        if (request.getType() == WorkerType.HIRED_WORKERS)
            creator.createCreditRequestPdf(query, request, authUser, false);
        else if (request.getType() == WorkerType.INDIVIDUAL_ENTREPRENEUR)
            creator.createCreditRequestIpPdf(query, request, authUser, false);

        return query.getId();
    }


    private Map<String, Object> getFinancialStateMap(boolean immovableProperty, boolean movableProperty, WorkerType type) {

        Map<String, Object> financialState = new HashMap<>();

        if (type == WorkerType.HIRED_WORKERS) {
            Map<String, String> solvencyTaxAgent = PdfExtractor.getSolvencyHiredWorkersTaxAgent();
            Map<String, String> solvencyClientSum = PdfExtractor.getSolvencyHiredWorkersClientSum();

            financialState.put("наименование", solvencyTaxAgent.get("наименование"));
            financialState.put("инн", solvencyTaxAgent.get("инн"));
            financialState.put("кпп", solvencyTaxAgent.get("кпп"));
            financialState.put("общая сумма дохода", solvencyClientSum.get("общая сумма дохода"));
            financialState.put("сумма налога исчисленная", solvencyClientSum.get("сумма налога исчисленная"));
            financialState.put("средний ежемесячный доход", solvencyClientSum.get("средний ежемесячный доход"));
            financialState.put("недвижимое имущество", immovableProperty);
            financialState.put("движимое имущество", movableProperty);
        } else if (type == WorkerType.INDIVIDUAL_ENTREPRENEUR) {
            Map<String, String> solvencyTaxAgent = PdfExtractor.getSolvencyIndividualEntrepreneurTaxAgent();
            Map<String, String> solvencyClientSum = PdfExtractor.getSolvencyIndividualEntrepreneurClientSum();

            financialState.put("наименование", solvencyTaxAgent.get("наименование"));
            financialState.put("инн", solvencyTaxAgent.get("инн"));
            financialState.put("огрнип", solvencyTaxAgent.get("огрнип"));
            financialState.put("общая сумма дохода", solvencyClientSum.get("общая сумма дохода"));
            financialState.put("средний ежемесячный доход", solvencyClientSum.get("средний ежемесячный доход"));
            financialState.put("недвижимое имущество", immovableProperty);
            financialState.put("движимое имущество", movableProperty);
        }
        return financialState;
    }

    private boolean equalsSolvencyUserData(User user, Map<String, String> solvencyClientData) {
        final String serialNumber = solvencyClientData.get("серия и номер паспорта");
        final String[] fio = solvencyClientData.get("фио").split(" ");
        final String dateOfBirth = Optional.ofNullable(solvencyClientData.get("дата рождения"))
                .orElse("-");

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        if (Objects.equals(serialNumber, "") || fio.length != 3)
            return false;

        final String userSerialNumber = user.getPassportSerialNumber()
                .replaceAll("-", "")
                .replaceAll(" ", "");

        LocalDate date;
        if (!dateOfBirth.equals("-"))
            date = LocalDate.parse(dateOfBirth, formatter);
        else
            date = LocalDate.now();

        return Objects.equals(userSerialNumber, serialNumber)
                && Objects.equals(user.getFirstName(), fio[1]) && Objects.equals(user.getLastName(), fio[0])
                && Objects.equals(user.getSurName(), fio[2]) && (user.getDateOfBirth().equals(date) || dateOfBirth.equals("-"));
    }

    private boolean equalsDocumentsData(Map<String, String> solvencyClientData) {
        Map<String, String> solvencyTagAgent = PdfExtractor.getSolvencyHiredWorkersTaxAgent();
        Map<String, String> employmentClientData = PdfExtractor.getEmploymentHiredWorkersClientData();
        Map<String, String> employmentTagAgent = PdfExtractor.getEmploymentHiredWorkersTaxAgent();

        final String solvencyFio = solvencyClientData.get("фио");
        final String solvencyDateOfBirth = solvencyClientData.get("дата рождения");
        final String solvencyTagAgentName = solvencyTagAgent.get("наименование");
        final String solvencyTagAgentINN = solvencyTagAgent.get("инн");
        final String solvencyTagAgentKPP = solvencyTagAgent.get("кпп");

        final String employmentTagAgentName = employmentTagAgent.get("наименование");
        final String employmentTagAgentINN = employmentTagAgent.get("инн");
        final String employmentTagAgentKPP = employmentTagAgent.get("кпп");
        final String employmentFio = employmentClientData.get("фио");
        final String employmentDateOfBirth = employmentClientData.get("дата рождения");

        return Objects.equals(solvencyFio, employmentFio)
                && Objects.equals(solvencyDateOfBirth, employmentDateOfBirth) && Objects.equals(solvencyTagAgentName, employmentTagAgentName)
                && Objects.equals(solvencyTagAgentINN, employmentTagAgentINN) && Objects.equals(solvencyTagAgentKPP, employmentTagAgentKPP);
    }
}
