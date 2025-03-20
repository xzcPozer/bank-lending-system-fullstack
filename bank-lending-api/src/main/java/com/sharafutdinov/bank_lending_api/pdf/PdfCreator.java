package com.sharafutdinov.bank_lending_api.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.sharafutdinov.bank_lending_api.bank_db.entity.ClientInformation;
import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditQueryInfo;
import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import com.sharafutdinov.bank_lending_api.bank_db.entity.User;
import com.sharafutdinov.bank_lending_api.bank_db.repository.ClientInformationRepository;
import com.sharafutdinov.bank_lending_api.credit_request.ProcessingStatus;
import com.sharafutdinov.bank_lending_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfCreator {

    private final ClientInformationRepository clientInformationRepository;
    private final PdfExporter exporter;


    private float yPosition;
    private final Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
    private final Font mainTextFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

    public String createFinancialSituationFor2ndflPdf(CreditQueryInfo creditQuery, CreditRequest creditRequest, User authUser, boolean isVerify) {

        Document document = new Document(PageSize.A4, 36, 36, 130, 36);
        File outputFile = new File(exporter.getReportFilepath(creditRequest.getUser().getId()) + "/financial_report.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PdfPageHelper(
                    authUser.getLastName(),
                    authUser.getFirstName(),
                    authUser.getSurName(),
                    "отчет о финансовом состоянии клиента",
                    isVerify));
            document.open();

            // Личные данные клиента
            document.add(new Paragraph("1. Личные данные клиента:", headerFont));

            //фио
            PdfPTable table = new PdfPTable(6);
            table.setTotalWidth(510);
            table.setWidths(new int[]{2, 4, 1, 2, 2, 4});

            PdfPCell lastNameTextCell = createLabelCell("Фамилия:");
            table.addCell(lastNameTextCell);
            PdfPCell lastNameCell = createDataCell(creditRequest.getUser().getLastName());
            table.addCell(lastNameCell);

            PdfPCell nameTextCell = createLabelCell("Имя:");
            table.addCell(nameTextCell);
            PdfPCell nameCell = createDataCell(creditRequest.getUser().getFirstName());
            table.addCell(nameCell);

            PdfPCell surNameTextCell = createLabelCell("Отчество:");
            table.addCell(surNameTextCell);
            PdfPCell surNameCell = createDataCell(creditRequest.getUser().getSurName());
            table.addCell(surNameCell);

            yPosition = 680;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // дата рождения
            table = new PdfPTable(2);
            table.setTotalWidth(220);
            table.setWidths(new float[]{1, 1.3F});

            PdfPCell dateOfBirthTextCell = createLabelCell("Дата рождения:");
            table.addCell(dateOfBirthTextCell);
            PdfPCell dateOfBirthCell = createDataCell(creditRequest.getUser().getDateOfBirth().toString());
            table.addCell(dateOfBirthCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // серия и номер
            table = new PdfPTable(2);
            table.setTotalWidth(240);
            table.setWidths(new float[]{2, 1.2F});

            PdfPCell serialNumberTextCell = createLabelCell("Серия и номер паспорта:");
            table.addCell(serialNumberTextCell);
            PdfPCell serialNumberCell = createDataCell(creditRequest.getUser().getPassportSerialNumber());
            table.addCell(serialNumberCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Фактический адрес
            PdfPTable factAddressTable = new PdfPTable(2);
            factAddressTable.setTotalWidth(510);
            factAddressTable.setWidths(new float[]{2, 6.3F});

            PdfPCell actualAddressLabel = createLabelCell("Фактический адрес:");
            factAddressTable.addCell(actualAddressLabel);
            PdfPCell actualAddressData = createDataCell(creditRequest.getUser().getAddressFact());
            factAddressTable.addCell(actualAddressData);

            yPosition -= 22;
            factAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Регистрационный адрес
            PdfPTable regAddressTable = new PdfPTable(2);
            regAddressTable.setTotalWidth(510);
            regAddressTable.setWidths(new int[]{2, 5});

            PdfPCell registrationAddressLabel = createLabelCell("Регистрационный адрес:");
            regAddressTable.addCell(registrationAddressLabel);
            PdfPCell registrationAddressData = createDataCell(creditRequest.getUser().getAddressRegister());
            regAddressTable.addCell(registrationAddressData);

            yPosition -= 22;
            regAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // телефон
            PdfPTable phone = new PdfPTable(2);
            phone.setTotalWidth(170);
            phone.setWidths(new float[]{1, 1.8F});

            PdfPCell phoneLabel = createLabelCell("Телефон:");
            phone.addCell(phoneLabel);
            PdfPCell phoneData = createDataCell(creditRequest.getUser().getPhoneNumber());
            phone.addCell(phoneData);

            yPosition -= 22;
            phone.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Данные о налоговом агенте
            Paragraph p1 = new Paragraph("2. Данные о налоговом агенте", headerFont);
            p1.setSpacingBefore(148);
            document.add(p1);

            // наименование
            PdfPTable taxAgentTable = new PdfPTable(2);
            taxAgentTable.setTotalWidth(510);
            taxAgentTable.setWidths(new float[]{1.2F, 5});

            PdfPCell taxAgentLabel = createLabelCell("Наименование:");
            taxAgentTable.addCell(taxAgentLabel);
            PdfPCell taxAgentData = createDataCell(creditQuery.getFinancialSituation().get("наименование").toString());
            taxAgentTable.addCell(taxAgentData);

            yPosition -= 60;
            taxAgentTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // инн и кпп
            PdfPTable innkppTable = new PdfPTable(2);
            innkppTable.setTotalWidth(110);
            innkppTable.setWidths(new int[]{1, 2});

            PdfPCell innLabel = createLabelCell("ИНН:");
            innkppTable.addCell(innLabel);
            PdfPCell innData = createDataCell(creditQuery.getFinancialSituation().get("инн").toString());
            innkppTable.addCell(innData);

            PdfPCell kppLabel = createLabelCell("КПП:");
            innkppTable.addCell(kppLabel);
            PdfPCell kppData = createDataCell(creditQuery.getFinancialSituation().get("кпп").toString());
            innkppTable.addCell(kppData);

            yPosition -= 22;
            innkppTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Финансовое состояние
            Paragraph p2 = new Paragraph("3. Финансовое состояние:", headerFont);
            p2.setSpacingBefore(80);
            document.add(p2);

            PdfPTable sumTable = new PdfPTable(2);
            sumTable.setTotalWidth(400);
            sumTable.setWidths(new float[]{3.2F, 5});

            PdfPCell sumLabel = createLabelCell("Общая сумма дохода:");
            sumTable.addCell(sumLabel);
            PdfPCell sumData = createDataCell(creditQuery.getFinancialSituation().get("общая сумма дохода").toString());
            sumTable.addCell(sumData);

            yPosition -= 80;
            sumTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable taxTable = new PdfPTable(2);
            taxTable.setTotalWidth(400);
            taxTable.setWidths(new float[]{3.3F, 5});

            PdfPCell taxLabel = createLabelCell("Сумма налога исчисления:");
            taxTable.addCell(taxLabel);
            PdfPCell taxData = createDataCell(creditQuery.getFinancialSituation().get("сумма налога исчисленная") + " руб.");
            taxTable.addCell(taxData);

            yPosition -= 22;
            taxTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setTotalWidth(400);
            salaryTable.setWidths(new int[]{4, 5});

            PdfPCell salaryLabel = createLabelCell("Средний ежемесячный доход:");
            salaryTable.addCell(salaryLabel);
            PdfPCell salaryData = createDataCell(creditQuery.getFinancialSituation().get("средний ежемесячный доход") + " руб.");
            salaryTable.addCell(salaryData);

            yPosition -= 22;
            salaryTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable immovablePropTable = new PdfPTable(2);
            immovablePropTable.setTotalWidth(220);
            immovablePropTable.setWidths(new float[]{5, 2.5F});

            PdfPCell immovablePropLabel = createLabelCell("Недвижимое имущество:");
            immovablePropTable.addCell(immovablePropLabel);
            PdfPCell immovablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("недвижимое имущество") ? "есть" : "нет");
            immovablePropTable.addCell(immovablePropData);

            yPosition -= 22;
            immovablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable movablePropTable = new PdfPTable(2);
            movablePropTable.setTotalWidth(210);
            movablePropTable.setWidths(new float[]{5, 3});

            PdfPCell movablePropLabel = createLabelCell("Движимое имущество:");
            movablePropTable.addCell(movablePropLabel);
            PdfPCell movablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("движимое имущество") ? "есть" : "нет");
            movablePropTable.addCell(movablePropData);

            yPosition -= 22;
            movablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            document.close();
            writer.close();
        } catch (IOException e) {
            setCreditRequestData(creditRequest, "2НДФЛ");
            throw new PdfException(e);
        }

        return outputFile.getAbsolutePath();
    }

    public String createAccountStatusPdf(CreditRequest creditRequest, User authUser, boolean isVerify) {

        ClientInformation clientInformation = Optional.ofNullable(clientInformationRepository.findByUserId(creditRequest.getUser().getId()))
                .orElseThrow(() -> new ResourceNotFoundException("информации об этом пользователи не было найдено"));

        Document document = new Document(PageSize.A4, 36, 36, 130, 36);
        File outputFile = new File(exporter.getReportFilepath(creditRequest.getUser().getId()) + "/account_status_report.pdf");

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PdfPageHelper(
                    authUser.getLastName(),
                    authUser.getFirstName(),
                    authUser.getSurName(),
                    "Состояние счета и предыдущие кредиты клиента",
                    isVerify));
            document.open();

            // Личные данные клиента
            document.add(new Paragraph("1. Личные данные клиента:", headerFont));

            //фио
            PdfPTable table = new PdfPTable(6);
            table.setTotalWidth(510);
            table.setWidths(new int[]{2, 4, 1, 2, 2, 4});

            PdfPCell lastNameTextCell = createLabelCell("Фамилия:");
            table.addCell(lastNameTextCell);
            PdfPCell lastNameCell = createDataCell(creditRequest.getUser().getLastName());
            table.addCell(lastNameCell);

            PdfPCell nameTextCell = createLabelCell("Имя:");
            table.addCell(nameTextCell);
            PdfPCell nameCell = createDataCell(creditRequest.getUser().getFirstName());
            table.addCell(nameCell);

            PdfPCell surNameTextCell = createLabelCell("Отчество:");
            table.addCell(surNameTextCell);
            PdfPCell surNameCell = createDataCell(creditRequest.getUser().getSurName());
            table.addCell(surNameCell);

            yPosition = 680;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // дата рождения
            table = new PdfPTable(2);
            table.setTotalWidth(220);
            table.setWidths(new float[]{1, 1.3F});

            PdfPCell dateOfBirthTextCell = createLabelCell("Дата рождения:");
            table.addCell(dateOfBirthTextCell);
            PdfPCell dateOfBirthCell = createDataCell(creditRequest.getUser().getDateOfBirth().toString());
            table.addCell(dateOfBirthCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // серия и номер
            table = new PdfPTable(2);
            table.setTotalWidth(240);
            table.setWidths(new float[]{2, 1.2F});

            PdfPCell serialNumberTextCell = createLabelCell("Серия и номер паспорта:");
            table.addCell(serialNumberTextCell);
            PdfPCell serialNumberCell = createDataCell(creditRequest.getUser().getPassportSerialNumber());
            table.addCell(serialNumberCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Фактический адрес
            PdfPTable factAddressTable = new PdfPTable(2);
            factAddressTable.setTotalWidth(510);
            factAddressTable.setWidths(new float[]{2, 6.3F});

            PdfPCell actualAddressLabel = createLabelCell("Фактический адрес:");
            factAddressTable.addCell(actualAddressLabel);
            PdfPCell actualAddressData = createDataCell(creditRequest.getUser().getAddressFact());
            factAddressTable.addCell(actualAddressData);

            yPosition -= 22;
            factAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Регистрационный адрес
            PdfPTable regAddressTable = new PdfPTable(2);
            regAddressTable.setTotalWidth(510);
            regAddressTable.setWidths(new int[]{2, 5});

            PdfPCell registrationAddressLabel = createLabelCell("Регистрационный адрес:");
            regAddressTable.addCell(registrationAddressLabel);
            PdfPCell registrationAddressData = createDataCell(creditRequest.getUser().getAddressRegister());
            regAddressTable.addCell(registrationAddressData);

            yPosition -= 22;
            regAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // телефон
            PdfPTable phone = new PdfPTable(2);
            phone.setTotalWidth(170);
            phone.setWidths(new float[]{1, 1.8F});

            PdfPCell phoneLabel = createLabelCell("Телефон:");
            phone.addCell(phoneLabel);
            PdfPCell phoneData = createDataCell(creditRequest.getUser().getPhoneNumber());
            phone.addCell(phoneData);

            yPosition -= 22;
            phone.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            Paragraph p1 = new Paragraph("2. Состояние счета", headerFont);
            p1.setSpacingBefore(148);
            document.add(p1);

            // баланс
            PdfPTable balance = new PdfPTable(2);
            balance.setTotalWidth(250);
            balance.setWidths(new float[]{1, 4});

            PdfPCell balanceLabel = createLabelCell("баланс:");
            balance.addCell(balanceLabel);
            PdfPCell balanceData = createDataCell(clientInformation.getBalance() == null
                    ? "0" : clientInformation.getBalance().toString());
            balance.addCell(balanceData);

            yPosition -= 55;
            balance.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // текущий кредит
            boolean isCurrentLoan = creditRequest.getCurrentLoans() != null;

            PdfPTable loansTable = new PdfPTable(2);
            loansTable.setTotalWidth(250);
            loansTable.setWidths(new float[]{3, 4});

            PdfPCell loansLabel = createLabelCell("Текущий кредит:");
            loansTable.addCell(loansLabel);
            PdfPCell loansData = createDataCell(isCurrentLoan ? "есть" : "нет");
            loansTable.addCell(loansData);

            yPosition -= 22;
            loansTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            int spacing = 60;
            if (isCurrentLoan) {
                // текущий кредит
                PdfPTable termCurLoanTable = new PdfPTable(2);
                termCurLoanTable.setTotalWidth(250);
                termCurLoanTable.setWidths(new float[]{4, 2.5F});

                PdfPCell termCurLoanLabel = createLabelCell("Оставшийся срок кредита:");
                termCurLoanTable.addCell(termCurLoanLabel);
                PdfPCell termCurLoanData = createDataCell(creditRequest.getCurrentLoans().get("срок кредита").toString());
                termCurLoanTable.addCell(termCurLoanData);

                yPosition -= 22;
                termCurLoanTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

                // ежемесячная оплата
                PdfPTable paymentCurLoanTable = new PdfPTable(2);
                paymentCurLoanTable.setTotalWidth(280);
                paymentCurLoanTable.setWidths(new float[]{3.4F, 4});

                PdfPCell paymentCurLoanLabel = createLabelCell("Ежемесячная оплата:");
                paymentCurLoanTable.addCell(paymentCurLoanLabel);
                PdfPCell paymentCurLoanData = createDataCell(creditRequest.getCurrentLoans().get("ежемесячная оплата").toString() + " руб.");
                paymentCurLoanTable.addCell(paymentCurLoanData);

                yPosition -= 22;
                paymentCurLoanTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

                spacing = 100;
            }

            var previousLoans = clientInformation.getPreviousLoans();
            // предыдущие кредиты
            if (previousLoans != null) {

                Paragraph p2 = new Paragraph("3. предыдущие кредиты", headerFont);
                p2.setSpacingBefore(spacing);
                document.add(p2);
                yPosition -= 40;

                for (Map<String, Object> previousLoan : previousLoans) {
                    if (yPosition > 130) {
                        writePreviousLoan(previousLoan, writer);
                    } else {
                        document.newPage();
                        yPosition = 730;
                        writePreviousLoan(previousLoan, writer);
                    }
                    writeLine(writer, document, 828 - yPosition - 90);
                }
            }

            document.close();
            writer.close();
        } catch (IOException e) {
            setCreditRequestData(creditRequest, "выписка из трудовой книжки");
            throw new PdfException(e);
        }
        return outputFile.getAbsolutePath();
    }

    public String createCreditRequestPdf(CreditQueryInfo creditQuery, CreditRequest creditRequest, User authUser, boolean isVerify) {

        ClientInformation clientInformation = Optional.ofNullable(clientInformationRepository.findByUserId(creditRequest.getUser().getId()))
                .orElseThrow(() -> new ResourceNotFoundException("информации об этом пользователи не было найдено"));

        Document document = new Document(PageSize.A4, 36, 36, 130, 36);
        File outputFile = new File(exporter.getReportFilepath(creditRequest.getUser().getId()) + "/credit_query_report.pdf");

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PdfPageHelper(
                    authUser.getLastName(),
                    authUser.getFirstName(),
                    authUser.getSurName(),
                    "Кредитный запрос клиента",
                    isVerify));
            document.open();

            // Личные данные клиента
            document.add(new Paragraph("1. Личные данные клиента:", headerFont));

            //фио
            PdfPTable table = new PdfPTable(6);
            table.setTotalWidth(510);
            table.setWidths(new int[]{2, 4, 1, 2, 2, 4});

            PdfPCell lastNameTextCell = createLabelCell("Фамилия:");
            table.addCell(lastNameTextCell);
            PdfPCell lastNameCell = createDataCell(creditRequest.getUser().getLastName());
            table.addCell(lastNameCell);

            PdfPCell nameTextCell = createLabelCell("Имя:");
            table.addCell(nameTextCell);
            PdfPCell nameCell = createDataCell(creditRequest.getUser().getFirstName());
            table.addCell(nameCell);

            PdfPCell surNameTextCell = createLabelCell("Отчество:");
            table.addCell(surNameTextCell);
            PdfPCell surNameCell = createDataCell(creditRequest.getUser().getSurName());
            table.addCell(surNameCell);

            yPosition = 680;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // дата рождения
            table = new PdfPTable(2);
            table.setTotalWidth(220);
            table.setWidths(new float[]{1, 1.3F});

            PdfPCell dateOfBirthTextCell = createLabelCell("Дата рождения:");
            table.addCell(dateOfBirthTextCell);
            PdfPCell dateOfBirthCell = createDataCell(creditRequest.getUser().getDateOfBirth().toString());
            table.addCell(dateOfBirthCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // серия и номер
            table = new PdfPTable(2);
            table.setTotalWidth(240);
            table.setWidths(new float[]{2, 1.2F});

            PdfPCell serialNumberTextCell = createLabelCell("Серия и номер паспорта:");
            table.addCell(serialNumberTextCell);
            PdfPCell serialNumberCell = createDataCell(creditRequest.getUser().getPassportSerialNumber());
            table.addCell(serialNumberCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Фактический адрес
            PdfPTable factAddressTable = new PdfPTable(2);
            factAddressTable.setTotalWidth(510);
            factAddressTable.setWidths(new float[]{2, 6.3F});

            PdfPCell actualAddressLabel = createLabelCell("Фактический адрес:");
            factAddressTable.addCell(actualAddressLabel);
            PdfPCell actualAddressData = createDataCell(creditRequest.getUser().getAddressFact());
            factAddressTable.addCell(actualAddressData);

            yPosition -= 22;
            factAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Регистрационный адрес
            PdfPTable regAddressTable = new PdfPTable(2);
            regAddressTable.setTotalWidth(510);
            regAddressTable.setWidths(new int[]{2, 5});

            PdfPCell registrationAddressLabel = createLabelCell("Регистрационный адрес:");
            regAddressTable.addCell(registrationAddressLabel);
            PdfPCell registrationAddressData = createDataCell(creditRequest.getUser().getAddressRegister());
            regAddressTable.addCell(registrationAddressData);

            yPosition -= 22;
            regAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // телефон
            PdfPTable phone = new PdfPTable(2);
            phone.setTotalWidth(170);
            phone.setWidths(new float[]{1, 1.8F});

            PdfPCell phoneLabel = createLabelCell("Телефон:");
            phone.addCell(phoneLabel);
            PdfPCell phoneData = createDataCell(creditRequest.getUser().getPhoneNumber());
            phone.addCell(phoneData);

            yPosition -= 22;
            phone.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Финансовое состояние
            Paragraph p2 = new Paragraph("2. Финансовое состояние:", headerFont);
            p2.setSpacingBefore(148);
            document.add(p2);

            PdfPTable sumTable = new PdfPTable(2);
            sumTable.setTotalWidth(400);
            sumTable.setWidths(new float[]{3.2F, 5});

            PdfPCell sumLabel = createLabelCell("Общая сумма дохода:");
            sumTable.addCell(sumLabel);
            PdfPCell sumData = createDataCell(creditQuery.getFinancialSituation().get("общая сумма дохода").toString());
            sumTable.addCell(sumData);

            yPosition -= 60;
            sumTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable taxTable = new PdfPTable(2);
            taxTable.setTotalWidth(400);
            taxTable.setWidths(new float[]{3.3F, 5});

            PdfPCell taxLabel = createLabelCell("Сумма налога исчисления:");
            taxTable.addCell(taxLabel);
            PdfPCell taxData = createDataCell(creditQuery.getFinancialSituation().get("сумма налога исчисленная") + " руб.");
            taxTable.addCell(taxData);

            yPosition -= 22;
            taxTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setTotalWidth(400);
            salaryTable.setWidths(new int[]{4, 5});

            PdfPCell salaryLabel = createLabelCell("Средний ежемесячный доход:");
            salaryTable.addCell(salaryLabel);
            PdfPCell salaryData = createDataCell(creditQuery.getFinancialSituation().get("средний ежемесячный доход") + " руб.");
            salaryTable.addCell(salaryData);

            yPosition -= 22;
            salaryTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable immovablePropTable = new PdfPTable(2);
            immovablePropTable.setTotalWidth(220);
            immovablePropTable.setWidths(new float[]{5, 2.5F});

            PdfPCell immovablePropLabel = createLabelCell("Недвижимое имущество:");
            immovablePropTable.addCell(immovablePropLabel);
            PdfPCell immovablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("недвижимое имущество") ? "есть" : "нет");
            immovablePropTable.addCell(immovablePropData);

            yPosition -= 22;
            immovablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable movablePropTable = new PdfPTable(2);
            movablePropTable.setTotalWidth(210);
            movablePropTable.setWidths(new float[]{5, 3});

            PdfPCell movablePropLabel = createLabelCell("Движимое имущество:");
            movablePropTable.addCell(movablePropLabel);
            PdfPCell movablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("движимое имущество") ? "есть" : "нет");
            movablePropTable.addCell(movablePropData);

            yPosition -= 22;
            movablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            Paragraph p1 = new Paragraph("3. Состояние счета", headerFont);
            p1.setSpacingBefore(125);
            document.add(p1);

            // баланс
            PdfPTable balance = new PdfPTable(2);
            balance.setTotalWidth(250);
            balance.setWidths(new float[]{1, 4});

            PdfPCell balanceLabel = createLabelCell("баланс:");
            balance.addCell(balanceLabel);
            PdfPCell balanceData = createDataCell(clientInformation.getBalance() == null ? "0" : clientInformation.getBalance().toString());
            balance.addCell(balanceData);

            yPosition -= 55;
            balance.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // текущий кредит
            boolean isCurrentLoan = creditRequest.getCurrentLoans() != null;

            PdfPTable loansTable = new PdfPTable(2);
            loansTable.setTotalWidth(250);
            loansTable.setWidths(new float[]{3, 4});

            PdfPCell loansLabel = createLabelCell("Текущий кредит:");
            loansTable.addCell(loansLabel);
            PdfPCell loansData = createDataCell(isCurrentLoan ? "есть" : "нет");
            loansTable.addCell(loansData);

            yPosition -= 22;
            loansTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            int spacing = 60;
            if (isCurrentLoan) {
                // текущий кредит
                PdfPTable termCurLoanTable = new PdfPTable(2);
                termCurLoanTable.setTotalWidth(250);
                termCurLoanTable.setWidths(new float[]{4, 2.5F});

                PdfPCell termCurLoanLabel = createLabelCell("Оставшийся срок кредита:");
                termCurLoanTable.addCell(termCurLoanLabel);
                PdfPCell termCurLoanData = createDataCell(creditRequest.getCurrentLoans().get("срок кредита").toString());
                termCurLoanTable.addCell(termCurLoanData);

                yPosition -= 22;
                termCurLoanTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

                // ежемесячная оплата
                PdfPTable paymentCurLoanTable = new PdfPTable(2);
                paymentCurLoanTable.setTotalWidth(280);
                paymentCurLoanTable.setWidths(new float[]{3.4F, 4});

                PdfPCell paymentCurLoanLabel = createLabelCell("Ежемесячная оплата:");
                paymentCurLoanTable.addCell(paymentCurLoanLabel);
                PdfPCell paymentCurLoanData = createDataCell(creditRequest.getCurrentLoans().get("ежемесячная оплата").toString() + " руб.");
                paymentCurLoanTable.addCell(paymentCurLoanData);

                yPosition -= 22;
                paymentCurLoanTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

                spacing = 100;
            }

            var previousLoans = clientInformation.getPreviousLoans();
            // предыдущие кредиты
            if (previousLoans != null) {

                Paragraph p3 = new Paragraph("4. предыдущие кредиты", headerFont);
                p3.setSpacingBefore(spacing);
                document.add(p3);
                yPosition -= 40;

                for (Map<String, Object> previousLoan : previousLoans) {
                    if (yPosition > 160) {
                        writePreviousLoan(previousLoan, writer);
                    } else {
                        document.newPage();
                        yPosition = 730;
                        writePreviousLoan(previousLoan, writer);
                    }
                    writeLine(writer, document, 828 - yPosition - 90);
                }
            }

            document.close();
            writer.close();
        } catch (IOException e) {
            throw new PdfException(e);
        }

        return outputFile.getAbsolutePath();
    }

    public String createFinancialSituationForIpPdf(CreditQueryInfo creditQuery, CreditRequest creditRequest, User authUser, boolean isVerify) {

        Document document = new Document(PageSize.A4, 36, 36, 130, 36);
        File outputFile = new File(exporter.getReportFilepath(creditRequest.getUser().getId()) + "/financial_report.pdf");

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PdfPageHelper(
                    authUser.getLastName(),
                    authUser.getFirstName(),
                    authUser.getSurName(),
                    "отчет о финансовом состоянии клиента",
                    isVerify));
            document.open();

            // Личные данные клиента
            document.add(new Paragraph("1. Личные данные клиента:", headerFont));

            //фио
            PdfPTable table = new PdfPTable(6);
            table.setTotalWidth(510);
            table.setWidths(new int[]{2, 4, 1, 2, 2, 4});

            PdfPCell lastNameTextCell = createLabelCell("Фамилия:");
            table.addCell(lastNameTextCell);
            PdfPCell lastNameCell = createDataCell(creditRequest.getUser().getLastName());
            table.addCell(lastNameCell);

            PdfPCell nameTextCell = createLabelCell("Имя:");
            table.addCell(nameTextCell);
            PdfPCell nameCell = createDataCell(creditRequest.getUser().getFirstName());
            table.addCell(nameCell);

            PdfPCell surNameTextCell = createLabelCell("Отчество:");
            table.addCell(surNameTextCell);
            PdfPCell surNameCell = createDataCell(creditRequest.getUser().getSurName());
            table.addCell(surNameCell);

            yPosition = 680;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // дата рождения
            table = new PdfPTable(2);
            table.setTotalWidth(220);
            table.setWidths(new float[]{1, 1.3F});

            PdfPCell dateOfBirthTextCell = createLabelCell("Дата рождения:");
            table.addCell(dateOfBirthTextCell);
            PdfPCell dateOfBirthCell = createDataCell(creditRequest.getUser().getDateOfBirth().toString());
            table.addCell(dateOfBirthCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // серия и номер
            table = new PdfPTable(2);
            table.setTotalWidth(240);
            table.setWidths(new float[]{2, 1.2F});

            PdfPCell serialNumberTextCell = createLabelCell("Серия и номер паспорта:");
            table.addCell(serialNumberTextCell);
            PdfPCell serialNumberCell = createDataCell(creditRequest.getUser().getPassportSerialNumber());
            table.addCell(serialNumberCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Фактический адрес
            PdfPTable factAddressTable = new PdfPTable(2);
            factAddressTable.setTotalWidth(510);
            factAddressTable.setWidths(new float[]{2, 6.3F});

            PdfPCell actualAddressLabel = createLabelCell("Фактический адрес:");
            factAddressTable.addCell(actualAddressLabel);
            PdfPCell actualAddressData = createDataCell(creditRequest.getUser().getAddressFact());
            factAddressTable.addCell(actualAddressData);

            yPosition -= 22;
            factAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Регистрационный адрес
            PdfPTable regAddressTable = new PdfPTable(2);
            regAddressTable.setTotalWidth(510);
            regAddressTable.setWidths(new int[]{2, 5});

            PdfPCell registrationAddressLabel = createLabelCell("Регистрационный адрес:");
            regAddressTable.addCell(registrationAddressLabel);
            PdfPCell registrationAddressData = createDataCell(creditRequest.getUser().getAddressRegister());
            regAddressTable.addCell(registrationAddressData);

            yPosition -= 22;
            regAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // телефон
            PdfPTable phone = new PdfPTable(2);
            phone.setTotalWidth(170);
            phone.setWidths(new float[]{1, 1.8F});

            PdfPCell phoneLabel = createLabelCell("Телефон:");
            phone.addCell(phoneLabel);
            PdfPCell phoneData = createDataCell(creditRequest.getUser().getPhoneNumber());
            phone.addCell(phoneData);

            yPosition -= 22;
            phone.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Данные о налоговом агенте
            Paragraph p1 = new Paragraph("2. Данные об ИП:", headerFont);
            p1.setSpacingBefore(148);
            document.add(p1);

            // наименование
            PdfPTable taxAgentTable = new PdfPTable(2);
            taxAgentTable.setTotalWidth(510);
            taxAgentTable.setWidths(new float[]{1.2F, 5});

            PdfPCell taxAgentLabel = createLabelCell("Наименование:");
            taxAgentTable.addCell(taxAgentLabel);
            PdfPCell taxAgentData = createDataCell(creditQuery.getFinancialSituation().get("наименование").toString());
            taxAgentTable.addCell(taxAgentData);

            yPosition -= 60;
            taxAgentTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable innTable = new PdfPTable(2);
            innTable.setTotalWidth(130);
            innTable.setWidths(new int[]{1, 2});

            PdfPCell innLabel = createLabelCell("ИНН:");
            innTable.addCell(innLabel);
            PdfPCell innData = createDataCell(creditQuery.getFinancialSituation().get("инн").toString());
            innTable.addCell(innData);

            yPosition -= 22;
            innTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable ogrnipTable = new PdfPTable(2);
            ogrnipTable.setTotalWidth(200);
            ogrnipTable.setWidths(new float[]{0.8F, 2});

            PdfPCell ogrnipLabel = createLabelCell("ОГРНИП:");
            ogrnipTable.addCell(ogrnipLabel);
            PdfPCell ogrnipData = createDataCell(creditQuery.getFinancialSituation().get("огрнип").toString());
            ogrnipTable.addCell(ogrnipData);

            yPosition -= 22;
            ogrnipTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Финансовое состояние
            Paragraph p2 = new Paragraph("3. Финансовое состояние:", headerFont);
            p2.setSpacingBefore(80);
            document.add(p2);

            PdfPTable sumTable = new PdfPTable(2);
            sumTable.setTotalWidth(400);
            sumTable.setWidths(new float[]{3.1F, 5});

            PdfPCell sumLabel = createLabelCell("Общая сумма дохода:");
            sumTable.addCell(sumLabel);
            PdfPCell sumData = createDataCell(creditQuery.getFinancialSituation().get("общая сумма дохода") + " руб.");
            sumTable.addCell(sumData);

            yPosition -= 58;
            sumTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable taxTable = new PdfPTable(2);
            taxTable.setTotalWidth(400);
            taxTable.setWidths(new float[]{3.9F, 5});

            PdfPCell taxLabel = createLabelCell("Средний ежемесячный доход:");
            taxTable.addCell(taxLabel);
            PdfPCell taxData = createDataCell(creditQuery.getFinancialSituation().get("средний ежемесячный доход") + " руб.");
            taxTable.addCell(taxData);

            yPosition -= 22;
            taxTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable immovablePropTable = new PdfPTable(2);
            immovablePropTable.setTotalWidth(220);
            immovablePropTable.setWidths(new float[]{5, 2.5F});

            PdfPCell immovableLabel = createLabelCell("Недвижимое имущество:");
            immovablePropTable.addCell(immovableLabel);
            PdfPCell immovablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("недвижимое имущество") ? "есть" : "нет");
            immovablePropTable.addCell(immovablePropData);

            yPosition -= 22;
            immovablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable movablePropTable = new PdfPTable(2);
            movablePropTable.setTotalWidth(210);
            movablePropTable.setWidths(new float[]{5, 3});

            PdfPCell movablePropLabel = createLabelCell("Движимое имущество:");
            movablePropTable.addCell(movablePropLabel);
            PdfPCell movablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("движимое имущество") ? "есть" : "нет");
            movablePropTable.addCell(movablePropData);

            yPosition -= 22;
            movablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            document.close();
            writer.close();
        } catch (IOException e) {
            setCreditRequestData(creditRequest, "Справка о доходах ИП");
            throw new PdfException(e);
        }

        return outputFile.getAbsolutePath();
    }

    public String createCreditRequestIpPdf(CreditQueryInfo creditQuery, CreditRequest creditRequest, User authUser, boolean isVerify) {

        ClientInformation clientInformation = Optional.ofNullable(clientInformationRepository.findByUserId(creditRequest.getUser().getId()))
                .orElseThrow(() -> new ResourceNotFoundException("информации об этом пользователи не было найдено"));

        Document document = new Document(PageSize.A4, 36, 36, 130, 36);
        File outputFile = new File(exporter.getReportFilepath(creditRequest.getUser().getId()) + "/credit_query_report.pdf");

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new PdfPageHelper(
                    authUser.getLastName(),
                    authUser.getFirstName(),
                    authUser.getSurName(),
                    "Кредитный запрос клиента",
                    isVerify));
            document.open();

            // Личные данные клиента
            document.add(new Paragraph("1. Личные данные клиента:", headerFont));

            //фио
            PdfPTable table = new PdfPTable(6);
            table.setTotalWidth(510);
            table.setWidths(new int[]{2, 4, 1, 2, 2, 4});

            PdfPCell lastNameTextCell = createLabelCell("Фамилия:");
            table.addCell(lastNameTextCell);
            PdfPCell lastNameCell = createDataCell(creditRequest.getUser().getLastName());
            table.addCell(lastNameCell);

            PdfPCell nameTextCell = createLabelCell("Имя:");
            table.addCell(nameTextCell);
            PdfPCell nameCell = createDataCell(creditRequest.getUser().getFirstName());
            table.addCell(nameCell);

            PdfPCell surNameTextCell = createLabelCell("Отчество:");
            table.addCell(surNameTextCell);
            PdfPCell surNameCell = createDataCell(creditRequest.getUser().getSurName());
            table.addCell(surNameCell);

            yPosition = 680;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // дата рождения
            table = new PdfPTable(2);
            table.setTotalWidth(220);
            table.setWidths(new float[]{1, 1.3F});

            PdfPCell dateOfBirthTextCell = createLabelCell("Дата рождения:");
            table.addCell(dateOfBirthTextCell);
            PdfPCell dateOfBirthCell = createDataCell(creditRequest.getUser().getDateOfBirth().toString());
            table.addCell(dateOfBirthCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // серия и номер
            table = new PdfPTable(2);
            table.setTotalWidth(240);
            table.setWidths(new float[]{2, 1.2F});

            PdfPCell serialNumberTextCell = createLabelCell("Серия и номер паспорта:");
            table.addCell(serialNumberTextCell);
            PdfPCell serialNumberCell = createDataCell(creditRequest.getUser().getPassportSerialNumber());
            table.addCell(serialNumberCell);

            yPosition -= 22;
            table.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Фактический адрес
            PdfPTable factAddressTable = new PdfPTable(2);
            factAddressTable.setTotalWidth(510);
            factAddressTable.setWidths(new float[]{2, 6.3F});

            PdfPCell actualAddressLabel = createLabelCell("Фактический адрес:");
            factAddressTable.addCell(actualAddressLabel);
            PdfPCell actualAddressData = createDataCell(creditRequest.getUser().getAddressFact());
            factAddressTable.addCell(actualAddressData);

            yPosition -= 22;
            factAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Регистрационный адрес
            PdfPTable regAddressTable = new PdfPTable(2);
            regAddressTable.setTotalWidth(510);
            regAddressTable.setWidths(new int[]{2, 5});

            PdfPCell registrationAddressLabel = createLabelCell("Регистрационный адрес:");
            regAddressTable.addCell(registrationAddressLabel);
            PdfPCell registrationAddressData = createDataCell(creditRequest.getUser().getAddressRegister());
            regAddressTable.addCell(registrationAddressData);

            yPosition -= 22;
            regAddressTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // телефон
            PdfPTable phone = new PdfPTable(2);
            phone.setTotalWidth(170);
            phone.setWidths(new float[]{1, 1.8F});

            PdfPCell phoneLabel = createLabelCell("Телефон:");
            phone.addCell(phoneLabel);
            PdfPCell phoneData = createDataCell(creditRequest.getUser().getPhoneNumber());
            phone.addCell(phoneData);

            yPosition -= 22;
            phone.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            // Финансовое состояние
            Paragraph p2 = new Paragraph("2. Финансовое состояние:", headerFont);
            p2.setSpacingBefore(148);
            document.add(p2);

            PdfPTable sumTable = new PdfPTable(2);
            sumTable.setTotalWidth(400);
            sumTable.setWidths(new float[]{3.2F, 5});

            PdfPCell sumLabel = createLabelCell("Общая сумма дохода:");
            sumTable.addCell(sumLabel);
            PdfPCell sumData = createDataCell(creditQuery.getFinancialSituation().get("общая сумма дохода") + " руб.");
            sumTable.addCell(sumData);

            yPosition -= 60;
            sumTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable taxTable = new PdfPTable(2);
            taxTable.setTotalWidth(400);
            taxTable.setWidths(new float[]{3.3F, 5});

            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setTotalWidth(400);
            salaryTable.setWidths(new int[]{4, 5});

            PdfPCell salaryLabel = createLabelCell("Средний ежемесячный доход:");
            salaryTable.addCell(salaryLabel);
            PdfPCell salaryData = createDataCell(creditQuery.getFinancialSituation().get("средний ежемесячный доход") + " руб.");
            salaryTable.addCell(salaryData);

            yPosition -= 22;
            salaryTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable immovablePropTable = new PdfPTable(2);
            immovablePropTable.setTotalWidth(220);
            immovablePropTable.setWidths(new float[]{5, 2.5F});

            PdfPCell immovablePropLabel = createLabelCell("Недвижимое имущество:");
            immovablePropTable.addCell(immovablePropLabel);
            PdfPCell immovablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("недвижимое имущество") ? "есть" : "нет");
            immovablePropTable.addCell(immovablePropData);

            yPosition -= 22;
            immovablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            PdfPTable movablePropTable = new PdfPTable(2);
            movablePropTable.setTotalWidth(210);
            movablePropTable.setWidths(new float[]{5, 3});

            PdfPCell movablePropLabel = createLabelCell("Движимое имущество:");
            movablePropTable.addCell(movablePropLabel);
            PdfPCell movablePropData = createDataCell((boolean) creditQuery.getFinancialSituation().get("движимое имущество") ? "есть" : "нет");
            movablePropTable.addCell(movablePropData);

            yPosition -= 22;
            movablePropTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            Paragraph p1 = new Paragraph("3. Состояние счета", headerFont);
            p1.setSpacingBefore(103);
            document.add(p1);

            // баланс
            PdfPTable balance = new PdfPTable(2);
            balance.setTotalWidth(250);
            balance.setWidths(new float[]{1, 4});

            PdfPCell balanceLabel = createLabelCell("баланс:");
            balance.addCell(balanceLabel);
            PdfPCell balanceData = createDataCell(clientInformation.getBalance() == null
                    ? "0" : clientInformation.getBalance().toString());
            balance.addCell(balanceData);

            yPosition -= 55;
            balance.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            boolean isCurrentLoan = creditRequest.getCurrentLoans() != null;

            // текущий кредит
            PdfPTable loansTable = new PdfPTable(2);
            loansTable.setTotalWidth(250);
            loansTable.setWidths(new float[]{3, 4});

            PdfPCell loansLabel = createLabelCell("Текущий кредит:");
            loansTable.addCell(loansLabel);
            PdfPCell loansData = createDataCell(isCurrentLoan ? "есть" : "нет");
            loansTable.addCell(loansData);

            yPosition -= 22;
            loansTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

            int spacing = 60;
            if (isCurrentLoan) {
                // текущий кредит
                PdfPTable termCurLoanTable = new PdfPTable(2);
                termCurLoanTable.setTotalWidth(250);
                termCurLoanTable.setWidths(new float[]{4, 2.5F});

                PdfPCell termCurLoanLabel = createLabelCell("Оставшийся срок кредита:");
                termCurLoanTable.addCell(termCurLoanLabel);
                PdfPCell termCurLoanData = createDataCell(creditRequest.getCurrentLoans().get("срок кредита").toString());
                termCurLoanTable.addCell(termCurLoanData);

                yPosition -= 22;
                termCurLoanTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

                // ежемесячная оплата
                PdfPTable paymentCurLoanTable = new PdfPTable(2);
                paymentCurLoanTable.setTotalWidth(280);
                paymentCurLoanTable.setWidths(new float[]{3.4F, 4});

                PdfPCell paymentCurLoanLabel = createLabelCell("Ежемесячная оплата:");
                paymentCurLoanTable.addCell(paymentCurLoanLabel);
                PdfPCell paymentCurLoanData = createDataCell(creditRequest.getCurrentLoans().get("ежемесячная оплата") + " руб.");
                paymentCurLoanTable.addCell(paymentCurLoanData);

                yPosition -= 22;
                paymentCurLoanTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

                spacing = 100;
            }

            var previousLoans = clientInformation.getPreviousLoans();
            // предыдущие кредиты
            if (previousLoans != null) {

                Paragraph p3 = new Paragraph("4. предыдущие кредиты", headerFont);
                p3.setSpacingBefore(spacing);
                document.add(p3);
                yPosition -= 30;

                for (Map<String, Object> previousLoan : previousLoans) {
                    if (yPosition > 170) {
                        writePreviousLoan(previousLoan, writer);
                    } else {
                        document.newPage();
                        yPosition = 730;
                        writePreviousLoan(previousLoan, writer);
                    }
                    writeLine(writer, document, 828 - yPosition - 90);
                }
            }

            document.close();
            writer.close();
        } catch (IOException e) {
            throw new PdfException(e);
        }

        return outputFile.getAbsolutePath();
    }


    private void setCreditRequestData(CreditRequest creditRequest, String name) {
        creditRequest.setProcessed(true);
        creditRequest.setStatus(ProcessingStatus.SENT_FOR_REVISION);
        creditRequest.setDescriptionStatus("неверный формат документа, прикрепите другой " + name + " документ");
    }

    private void writePreviousLoan(Map<String, Object> previousLoan, PdfWriter writer) {
        int sum = (int) previousLoan.get("сумма");
        int term = (int) previousLoan.get("срок");
        double rate = (double) previousLoan.get("процентная ставка");
        double payment = (double) previousLoan.get("ежемесячный платеж");
        boolean status = (boolean) previousLoan.get("выплачен");

        // сумма
        PdfPTable sumTable = new PdfPTable(2);
        sumTable.setTotalWidth(250);
        sumTable.setWidths(new float[]{1, 4});

        PdfPCell sumLabel = createLabelCell("Сумма:");
        sumTable.addCell(sumLabel);
        PdfPCell sumData = createDataCell(String.valueOf(sum));
        sumTable.addCell(sumData);

        yPosition -= 22;
        sumTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

        // срок
        PdfPTable termTable = new PdfPTable(2);
        termTable.setTotalWidth(75);
        termTable.setWidths(new float[]{1.6F, 1});

        PdfPCell termLabel = createLabelCell("Срок:");
        termTable.addCell(termLabel);
        PdfPCell termData = createDataCell(String.valueOf(term));
        termTable.addCell(termData);

        yPosition -= 22;
        termTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

        // процентная ставка
        PdfPTable rateTable = new PdfPTable(2);
        rateTable.setTotalWidth(160);
        rateTable.setWidths(new float[]{3, 1});

        PdfPCell rateLabel = createLabelCell("Процентная ставка:");
        rateTable.addCell(rateLabel);
        PdfPCell rateData = createDataCell(String.valueOf(rate));
        rateTable.addCell(rateData);

        yPosition -= 22;
        rateTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

        // ежемесячный платеж
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setTotalWidth(270);
        paymentTable.setWidths(new float[]{3.8F, 4});

        PdfPCell paymentLabel = createLabelCell("Ежемесячный платеж:");
        paymentTable.addCell(paymentLabel);
        PdfPCell paymentData = createDataCell(String.valueOf(payment) + " руб.");
        paymentTable.addCell(paymentData);

        yPosition -= 22;
        paymentTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());

        // выплачен
        PdfPTable statusTable = new PdfPTable(2);
        statusTable.setTotalWidth(125);
        statusTable.setWidths(new float[]{1.65F, 1});

        PdfPCell statusLabel = createLabelCell("Выплачен:");
        statusTable.addCell(statusLabel);
        PdfPCell statusData = createDataCell(status ? "да" : "нет");
        statusTable.addCell(statusData);

        yPosition -= 22;
        statusTable.writeSelectedRows(0, -1, 34, yPosition, writer.getDirectContent());
    }

    private void writeLine(PdfWriter writer, Document document, float interval) {
        PdfContentByte canvas = writer.getDirectContent();
        canvas.saveState();
        float x1 = document.left();
        float x2 = document.right();
        float y = document.top() - interval;
        canvas.moveTo(x1, y);
        canvas.lineTo(x2, y);
        canvas.setLineWidth(0.5f); // Тонкая линия
        canvas.setLineDash(5f, 5f); // Пунктир (длина штриха, длина пробела)
        canvas.stroke();
        canvas.restoreState();
    }

    private PdfPCell createLabelCell(String text) {
        Paragraph paragraph = new Paragraph(text, mainTextFont);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPaddingTop(7);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell createDataCell(String text) {
        Paragraph paragraph = new Paragraph(text, mainTextFont);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPaddingTop(7);
        cell.setBorder(Rectangle.BOTTOM);
        return cell;
    }
}
