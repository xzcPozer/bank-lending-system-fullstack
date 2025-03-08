package com.sharafutdinov.bank_lending_api.user;

import com.sharafutdinov.bank_lending_api.auth.AuthenticationService;
import com.sharafutdinov.bank_lending_api.bank_db.entity.ClientInformation;
import com.sharafutdinov.bank_lending_api.bank_db.entity.Role;
import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.bank_db.repository.ClientInformationRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.RoleRepository;
import com.sharafutdinov.bank_lending_api.bank_db.repository.UserRepository;
import com.sharafutdinov.bank_lending_api.common.PageResponse;
import com.sharafutdinov.bank_lending_api.email.EmailService;
import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import com.sharafutdinov.bank_lending_api.rospassport_db.repository.CitizensRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ClientInformationRepository clientInformationRepository;
    private final CitizensRepository rospassportRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional("bankTransactionManager")
    public Long checkPassportData(UserRequest request) {
        User dbUser = repository.findByPassportSerialNumber(request.passportSerialNumber());

        if(dbUser == null){
            User userFromRospassport = Optional.ofNullable(rospassportRepository
                            .findByPassportSerialNumber(request.passportSerialNumber()))
                    .map(mapper::toUser)
                    .filter(user -> validationPassportData(request, user))
                    .map(user -> {
                        boolean existCheck = Optional.ofNullable(repository
                                        .findByEmailOrPhoneNumber(request.email(), request.phoneNumber()))
                                .isPresent();
                        if (existCheck)
                            throw new EntityExistsException("Ошибка: уже есть клиент с такой почтой и/или телефоном");

                        user.setPhoneNumber(request.phoneNumber());
                        user.setEmail(request.email());
                        user.setAddressFact(request.addressFact());
                        user.setRoles(new HashSet<>());
                        user.getRoles().add(addRoleByName("ROLE_CLIENT"));

                        String password = authenticationService.getRandomPassword();
                        user.setPassword(passwordEncoder.encode(password));

                        clientInformationRepository.save(ClientInformation.builder()
                                .user(user)
                                .build());

                        try {
                            emailService.sendLoginMail(request.email(), password);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }

                        return repository.save(user);
                    })
                    .orElseThrow(() -> new EntityNotFoundException("Ошибка: некорректные данные, пожалуйста, проверьте заполненные данные"));

            return userFromRospassport.getId();
        }


        return dbUser.getId();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return repository.findById(userId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент не был найден"));
    }

    @Override
    public PageResponse<UserResponse> getAllBankClient(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = repository.findAll(pageable);
        List<UserResponse> userResponseList = users.stream()
                .map(mapper::toResponse)
                .toList();
        return new PageResponse<>(
                userResponseList,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    private Role addRoleByName(String name){
        return Optional.ofNullable(roleRepository.findByName(name))
                .orElseThrow(()-> new ResourceNotFoundException("Такой роли не было найдено"));
    }

    private boolean validationPassportData(UserRequest req, User user) {
        return Objects.equals(req.firstName(), user.getFirstName()) && Objects.equals(req.lastname(), user.getLastName())
                && Objects.equals(req.surName(), user.getSurName()) && Objects.equals(req.dateOfBirth(), user.getDateOfBirth())
                && Objects.equals(req.addressRegister(), user.getAddressRegister()) && Objects.equals(req.passportSerialNumber(), user.getPassportSerialNumber());
    }
}
