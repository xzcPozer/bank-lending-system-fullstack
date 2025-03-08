package com.sharafutdinov.bank_lending_api.credit_request;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.bank_db.repository.CreditRequestRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.UserRepository;
import com.sharafutdinov.bank_lending_api.common.PageResponse;
import com.sharafutdinov.bank_lending_api.exception.CreditException;
import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import com.sharafutdinov.bank_lending_api.pdf.PdfExporter;
import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.server.ExportException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditRequestServiceImpl implements CreditRequestService {

    private final CreditRequestRepository repository;
    private final UserRepository userRepository;
    private final PdfExporter exporter;
    private final CreditRequestMapper mapper;

    @Override
    public Long addCreditRequest(CreditRequestDto request, MultipartFile solvency, MultipartFile employment) throws ExportException {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не было найдено в бд"));

        boolean isUserCreditProcessed = Optional.ofNullable(repository.findByUserIdAndIsProcessedIs(user.getId(), false))
                .isPresent();

        if (isUserCreditProcessed)
            throw new CreditException("У вас еще не обработан предыдущий запрос, дождитесь его обработки");

        return repository.save(CreditRequest.builder()
                .user(user)
                .loanPurpose(request.getLoanPurpose())
                .sum(request.getSum())
                .immovableProperty(request.isImmovableProperty())
                .movableProperty(request.isMovableProperty())
                .type(request.getType())
                .status(ProcessingStatus.UNDER_CONSIDERATION)
                .solvencyRefPath(exporter.uploadFile(solvency, request.getUserId()))
                .employmentRefPath(exporter.uploadFile(employment, request.getUserId()))
                .isProcessed(false)
                .currentLoans(request.getCurrentLoans())
                .build()).getId();
    }

    @Override
    public Long addCreditRequest(CreditRequestDto request, MultipartFile solvency) throws ExportException {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не было найдено в бд"));

        boolean isUserCreditProcessed = Optional.ofNullable(repository.findByUserIdAndIsProcessedIs(user.getId(), false))
                .isPresent();

        if (isUserCreditProcessed)
            throw new CreditException("У вас еще не обработан предыдущий запрос, дождитесь его обработки");

        return repository.save(CreditRequest.builder()
                .user(user)
                .loanPurpose(request.getLoanPurpose())
                .sum(request.getSum())
                .immovableProperty(request.isImmovableProperty())
                .movableProperty(request.isMovableProperty())
                .type(request.getType())
                .status(ProcessingStatus.UNDER_CONSIDERATION)
                .solvencyRefPath(exporter.uploadFile(solvency, request.getUserId()))
                .isProcessed(false)
                .currentLoans(request.getCurrentLoans())
                .build()).getId();
    }

    @Override
    public PageResponse<CreditRequestResponseForDirector> getAllCreditRequestWithLendingOfficer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<CreditRequest> creditRequests = repository.findAll(pageable);
        List<CreditRequestResponseForDirector> creditRequestList = creditRequests.stream()
                .map(mapper::toDirectorResponse)
                .toList();
        return new PageResponse<>(
                creditRequestList,
                creditRequests.getNumber(),
                creditRequests.getSize(),
                creditRequests.getTotalElements(),
                creditRequests.getTotalPages(),
                creditRequests.isFirst(),
                creditRequests.isLast()
        );
    }

    @Override
    public PageResponse<CreditRequestResponse> getAllCreditRequest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<CreditRequest> creditRequests = repository.findAll(pageable);
        List<CreditRequestResponse> creditRequestList = creditRequests.stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponse<>(
                creditRequestList,
                creditRequests.getNumber(),
                creditRequests.getSize(),
                creditRequests.getTotalElements(),
                creditRequests.getTotalPages(),
                creditRequests.isFirst(),
                creditRequests.isLast()
        );
    }

    @Override
    public PageResponse<CreditRequestResponse> getAllCreditRequest(int page, int size, boolean isProcessed) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<CreditRequest> creditRequests = repository.findByIsProcessedIs(pageable, isProcessed);
        List<CreditRequestResponse> creditRequestList = creditRequests.stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponse<>(
                creditRequestList,
                creditRequests.getNumber(),
                creditRequests.getSize(),
                creditRequests.getTotalElements(),
                creditRequests.getTotalPages(),
                creditRequests.isFirst(),
                creditRequests.isLast()
        );
    }

    @Override
    public PageResponse<CreditRequestResponse> getAllCreditByUserIdRequest(int page, int size, Authentication connectedUser) {
        Long authUserId = ((BankUserDetails) connectedUser.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<CreditRequest> creditRequests = repository.findAllByUserId(pageable, authUser.getId());
        List<CreditRequestResponse> creditRequestList = creditRequests.stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponse<>(
                creditRequestList,
                creditRequests.getNumber(),
                creditRequests.getSize(),
                creditRequests.getTotalElements(),
                creditRequests.getTotalPages(),
                creditRequests.isFirst(),
                creditRequests.isLast()
        );
    }

    @Override
    public Long changeCreditRequest(MultipartFile solvency, MultipartFile employment, Authentication connectedUser, Long creditRequestId) throws ExportException {
        CreditRequest request = repository.findById(creditRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Запрос не был найден в бд"));

        Long authUserId = ((BankUserDetails) connectedUser.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        if (request.getUser().equals(authUser))
            throw new CreditException("Что-то пошло не так, обратитесь к специалисту");

        request.setSolvencyRefPath(exporter.uploadFile(solvency, authUser.getId()));
        request.setEmploymentRefPath(exporter.uploadFile(employment, authUser.getId()));
        request.setProcessed(false);

        return repository.save(request).getId();
    }

    @Override
    public Long changeCreditRequest(MultipartFile solvency, Authentication connectedUser, Long creditRequestId) throws ExportException {
        CreditRequest request = repository.findById(creditRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Запрос не был найден в бд"));

        Long authUserId = ((BankUserDetails) connectedUser.getPrincipal()).getId();
        User authUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new EntityNotFoundException("Такого пользователя не было найдено в бд"));

        if (request.getUser().equals(authUser))
            throw new CreditException("Что-то пошло не так, обратитесь к специалисту");

        request.setSolvencyRefPath(exporter.uploadFile(solvency, authUser.getId()));
        request.setProcessed(false);

        return repository.save(request).getId();
    }

}
