package com.sharafutdinov.bank_lending_api.pdf;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.server.ExportException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PdfExtractor {

    private String strForRegex;
    private List<String> listForRegex = new ArrayList<>();

    @Getter
    private final static Map<String, String> solvencyHiredWorkersTaxAgent = new HashMap<>();
    @Getter
    private final static Map<String, String> solvencyHiredWorkersClientData = new HashMap<>();
    @Getter
    private final static Map<String, String> solvencyHiredWorkersClientSum = new HashMap<>();
    @Getter
    private final static Map<String, String> employmentHiredWorkersTaxAgent = new HashMap<>();
    @Getter
    private final static Map<String, String> employmentHiredWorkersClientData = new HashMap<>();

    @Getter
    private final static Map<String, String> solvencyIndividualEntrepreneurTaxAgent = new HashMap<>();
    @Getter
    private final static Map<String, String> solvencyIndividualEntrepreneurClientData = new HashMap<>();
    @Getter
    private final static Map<String, String> solvencyIndividualEntrepreneurClientSum = new HashMap<>();

    public void confirmationOf2Ndfl(String filePath) throws ExportException {

        try (PdfReader reader = new PdfReader(filePath)) {
            int numberOfPages = reader.getNumberOfPages();
            PdfTextExtractor extractor = new PdfTextExtractor(reader);

            String pageContent = extractor.getTextFromPage(1)
                    .replaceAll("\u00A0", "")
                    .replaceAll("\n", "")
                    .replaceAll("\\s{2,}", " ");

            strForRegex = findByRegExByGroup("ФИЗИЧЕСКОГО ЛИЦА за\\s+(\\d{4})", pageContent, 1);

            if (!checkAcceptableYear(strForRegex))
                throw new RuntimeException();

            strForRegex = findTextByRegEx("(ООО|АО)\\s+([«\"])(.+?)([»\"])", pageContent);
            solvencyHiredWorkersTaxAgent.put("наименование", strForRegex);

            strForRegex = findByRegExByGroup("год\\s+от\\s+\\d{2}\\.\\d{2}\\.\\d{4}\\s+(\\d{8,})\\s+(\\d{8,})\\s+(\\d{8,})\\s+(\\d{8,})", pageContent, 3);
            solvencyHiredWorkersTaxAgent.put("инн", strForRegex);
            strForRegex = findByRegExByGroup("год\\s+от\\s+\\d{2}\\.\\d{2}\\.\\d{4}\\s+(\\d{8,})\\s+(\\d{8,})\\s+(\\d{8,})\\s+(\\d{8,})", pageContent, 4);
            solvencyHiredWorkersTaxAgent.put("кпп", strForRegex);

            strForRegex = findByRegExByGroup("\\b((\\d{10})|(\\d{2}\\s\\d{2}\\s\\d{6})|(\\d{4}\\s\\d{6}))\\s+Мес", pageContent, 1);
            solvencyHiredWorkersClientData.put("серия и номер паспорта", strForRegex.replaceAll(" ", ""));

            strForRegex = findByRegExByGroup("\\d+\\s+(\\d{2}\\.\\d{2}\\.\\d{4})\\s+\\d+", pageContent, 1);
            solvencyHiredWorkersClientData.put("дата рождения", strForRegex);

            strForRegex = findByRegExByGroup("\\d{12}\\s+([А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+(?:\\s+[А-ЯЁ][а-яё]+)?)\\s+\\d+", pageContent, 1);
            solvencyHiredWorkersClientData.put("фио", strForRegex);

            listForRegex = findAllNumbersByRegEx("(?:Общая сумма дохода|Сумма налога исчисленная)\\D+(\\d[\\d\\s.,]*)", pageContent);
            solvencyHiredWorkersClientSum.put("общая сумма дохода", listForRegex.get(1));
            solvencyHiredWorkersClientSum.put("сумма налога исчисленная", listForRegex.get(2));
            solvencyHiredWorkersClientSum.put("средний ежемесячный доход", calculateAvgMonthlyIncome(listForRegex.get(1), listForRegex.get(2)));

        } catch (Exception e) {
            throw new ExportException("Ошибка при чтении PDF-файла!");
        }
    }

    public void confirmationOfEmployment(String filePath) throws ExportException {

        try (PdfReader reader = new PdfReader(filePath)) {
            int numberOfPages = reader.getNumberOfPages();
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            String pageContent = extractor.getTextFromPage(1)
                    .replaceAll("\u00A0", "")
                    .replaceAll("\n", "")
                    .replaceAll("\\s{2,}", " ");

            strForRegex = findByRegExByGroup("Дата рождения\\s+(\\d{2}\\.\\d{2}\\.\\d{4})", pageContent, 1);
            employmentHiredWorkersClientData.put("дата рождения", strForRegex);

            listForRegex = findAllByRegEx("(ООО|АО)\\s+([«\"])(.+?)([»\"])", pageContent);
            employmentHiredWorkersTaxAgent.put("наименование", listForRegex.get(1));

            if (!checkLastCompany(listForRegex))
                throw new RuntimeException();


            strForRegex = findTextByRegEx("работодателем\\s+([а-яёА-ЯЁ]+)\\s+([а-яёА-ЯЁ]+).*?Дата", pageContent)
                    .replace("работодателем", "")
                    .replace("Дата", "")
                    .replaceAll(" ", "")
                    .replaceAll("([а-яё])([А-ЯЁ])", "$1 $2")
                    .trim();

            strForRegex += " " + findTextByRegEx("([А-ЯЁ][а-яё]*(?:\\s[А-ЯЁа-яё]*)?(?:вич|вна|ич|на)?)\\s+(?=Раб)", pageContent)
                    .replaceAll(" ", "");
            employmentHiredWorkersClientData.put("фио", strForRegex);

            listForRegex = findAllByRegEx("\\b\\d{9,}\\b", pageContent);
            employmentHiredWorkersTaxAgent.put("инн", listForRegex.get(0));
            employmentHiredWorkersTaxAgent.put("кпп", listForRegex.get(1));

        } catch (Exception e) {
            throw new ExportException("Ошибка при чтении PDF-файла!");
        }
    }

    public void confirmationOfIpSolvency(String filePath) throws ExportException {
        try (PdfReader reader = new PdfReader(filePath)) {
            PdfTextExtractor extractor = new PdfTextExtractor(reader);

            String pageContent = extractor.getTextFromPage(1)
                    .replaceAll("\u00A0", "")
                    .replaceAll("\n", "")
                    .replaceAll("\\s{2,}", " ");

            strForRegex = findTextByRegEx("(?:ООО\\s*)?\"([^\"]+)\"", pageContent);
            solvencyIndividualEntrepreneurTaxAgent.put("наименование", strForRegex);

            strForRegex = findByRegExByGroup("организации:\\s*(\\d{12})", pageContent, 1);
            solvencyIndividualEntrepreneurTaxAgent.put("инн", strForRegex);

            strForRegex = findTextByRegEx("\\b\\d{15}\\b", pageContent);
            solvencyIndividualEntrepreneurTaxAgent.put("огрнип", strForRegex);

            listForRegex = findAllByRegEx("г\\.?\\s*[А-ЯЁ][а-яё]*(?:-[А-ЯЁ][а-яё]*)*,\\s*ул\\.?\\s*[А-ЯЁ][а-яё]*(?:-[А-ЯЁ][а-яё]*)*(?:\\s+[А-ЯЁа-яё\\d\\-]+)*,\\s*д\\.?\\s*\\d+[а-яё]?(?:,\\s*(?:к\\.?|корпус)\\s*\\d+[а-яё]?)?(?:,\\s*(?:офис\\s*\\d+|оф\\.?\\s*\\d+|кв\\.?\\s*\\d+))?", pageContent);
            solvencyIndividualEntrepreneurTaxAgent.put("юридический адрес", listForRegex.get(0));
            solvencyIndividualEntrepreneurTaxAgent.put("фактический адрес", listForRegex.get(1));

            strForRegex = findTextByRegEx("\\b\\+?\\d{11}\\b", pageContent);
            solvencyIndividualEntrepreneurClientData.put("телефон", "+" + strForRegex);

            strForRegex = findByRegExByGroup("Я,\\s+([А-ЯЁ][а-яё]+(?:\\s+[А-ЯЁ][а-яё]+){2})", pageContent, 1);
            solvencyIndividualEntrepreneurClientData.put("фио", strForRegex);

            strForRegex = findByRegExByGroup("Паспорт серии и номера:\\s*(\\d{4}\\s+\\d{6})\\s*,", pageContent, 1)
                    .replaceAll(" ", "");
            solvencyIndividualEntrepreneurClientData.put("серия и номер паспорта", strForRegex);

            String num1 = findByRegExByGroup("(\\d[\\d\\s]*)\\s*рублей\\s*(\\d{2})", pageContent, 1)
                    .replaceAll(" ", "");
            String num2 = findByRegExByGroup("(\\d[\\d\\s]*)\\s*рублей\\s*(\\d{2})", pageContent, 2)
                    .replaceAll(" ", "");
            String totalIncome = num1 + "." + num2;
            solvencyIndividualEntrepreneurClientSum.put("общая сумма дохода", totalIncome);
            solvencyIndividualEntrepreneurClientSum.put("средний ежемесячный доход", calculateAvgMonthlyIncome(totalIncome, 3));
        } catch (Exception e) {
            throw new ExportException("Ошибка при чтении PDF-файла!");
        }
    }

    private String calculateAvgMonthlyIncome(String sumStr, int period) {
        try {
            BigDecimal sum = new BigDecimal(sumStr);
            return (sum.divide(new BigDecimal(period), RoundingMode.CEILING)).toString();
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Ошибка обработки чисел для вычисления среднего дохода");
        }
    }

    private String calculateAvgMonthlyIncome(String sumStr, String taxStr) {
        try {
            BigDecimal sum = new BigDecimal(sumStr);
            BigDecimal tax = new BigDecimal(taxStr);
            return ((sum.subtract(tax)).divide(new BigDecimal(12), RoundingMode.CEILING)).toString();
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Ошибка обработки чисел для вычисления среднего дохода");
        }
    }

    private boolean checkLastCompany(List<String> searchedCompany) {
        for (String item : searchedCompany) {
            if (Collections.frequency(searchedCompany, item) > 1) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCurrentYear(int yearFromPdf) {
        int currentYear = LocalDate.now().getYear();
        return currentYear == yearFromPdf;
    }

    private boolean checkAcceptableYear(String yearFromPdf) {
        int currentYear = LocalDate.now().getYear() - 1;
        return currentYear == Integer.parseInt(yearFromPdf);
    }

    private List<String> findAllByRegEx(String regex, String str) {
        List<String> arr = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            arr.add(matcher.group());
        }
        return arr;
    }

    private String findTextByRegEx(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String findByRegExByGroup(String regex, String str, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            return matcher.group(group);
        }
        return "";
    }

    private List<String> findAllNumbersByRegEx(String regex, String str) {
        List<String> arr = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            arr.add(matcher.group(1).replaceAll("\\s", "").replaceAll(",", "."));
        }
        return arr;
    }

}
