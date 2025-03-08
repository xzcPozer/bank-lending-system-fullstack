package com.sharafutdinov.bank_lending_api.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendApprovedMail(SendApprovedMailRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper =
                new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setFrom(request.from());
        String templateName = "mailApprovedTemplate.html";
        
        messageHelper.setSubject("Информация по кредитному запросу");

        Context context = getContext(request);

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(request.to());
            mailSender.send(mimeMessage);

            log.info(String.format("Email successfully sent to %s with template %s,", request.to(), templateName));
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", request.to());
        }
    }

    @Async
    public void sendForRevisionMail(SendForRevisionMailRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper =
                new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setFrom(request.from());
        String templateName = "mailRevisionTemplate.html";

        messageHelper.setSubject("Измените данные в кредитном запросе");

        Context context = getContext(request);

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(request.to());
            mailSender.send(mimeMessage);

            log.info(String.format("Email successfully sent to %s with template %s,", request.to(), templateName));
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", request.to());
        }
    }

    @Async
    public void sendRefuseMail(SendRefuseMailRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper =
                new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setFrom(request.from());
        String templateName = "mailRefuseTemplate.html";

        messageHelper.setSubject("Информация по кредитному запросу");

        Context context = getContext(request);

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(request.to());
            mailSender.send(mimeMessage);

            log.info(String.format("Email successfully sent to %s with template %s,", request.to(), templateName));
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", request.to());
        }
    }

    @Async
    public void sendLoginMail(String email, String password) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper =
                new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setFrom("CoolBank228@mail.com");
        String templateName = "mailLoginTemplate.html";

        messageHelper.setSubject("Ваш логин и пароль для входа в личный кабинет");

        Context context = getContext(email, password);

        try {
            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            messageHelper.setTo(email);
            mailSender.send(mimeMessage);

            log.info(String.format("Email successfully sent to %s with template %s,", email, templateName));
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", email);
        }
    }

    private Context getContext(String email, String password) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("login", email);
        variables.put("password", password);

        Context context = new Context();
        context.setVariables(variables);
        return context;
    }

    private Context getContext(SendRefuseMailRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", request.firstName());
        variables.put("surName", request.surName());
        variables.put("amount", request.amount());

        Context context = new Context();
        context.setVariables(variables);
        return context;
    }

    private Context getContext(SendApprovedMailRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", request.firstName());
        variables.put("surName", request.surName());
        variables.put("amount", request.creditConditionRequest().amount());
        variables.put("term", request.creditConditionRequest().term());
        variables.put("interestRate", request.creditConditionRequest().interestRate());
        variables.put("monthlyPayment", request.creditConditionRequest().monthlyPayment());

        Context context = new Context();
        context.setVariables(variables);
        return context;
    }

    private Context getContext(SendForRevisionMailRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("firstName", request.firstName());
        variables.put("surName", request.surName());
        variables.put("description", request.description());

        Context context = new Context();
        context.setVariables(variables);
        return context;
    }


}
