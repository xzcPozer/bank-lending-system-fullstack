import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {NgxMaskDirective, NgxMaskPipe, provideNgxMask} from "ngx-mask";
import {CreditRequestDto} from "../../../../services/models/credit-request-dto";
import {CreditRequestService} from "../../../../services/services/credit-request.service";
import {UserService} from "../../../../services/services/user.service";
import {UserRequest} from "../../../../services/models/user-request";
import {MenuComponent} from "../../components/menu/menu.component";

@Component({
  selector: 'app-send-request',
  templateUrl: './send-request.component.html',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgxMaskDirective,
    NgxMaskPipe,
    FormsModule,
    MenuComponent
  ],
  providers: [provideNgxMask()],
  styleUrls: ['./send-request.component.scss']
})
export class SendRequestComponent {

  errorMsg: Array<string> = [];
  creditRequest: CreditRequestDto = {
    userId: 0,
    loanPurpose: "",
    sum: 0,
    type: 'HIRED_WORKERS'
  }
  userRequest: UserRequest = {
    addressFact: "",
    addressRegister: "",
    dateOfBirth: "",
    email: "",
    firstName: "",
    lastname: "",
    passportSerialNumber: "",
    phoneNumber: ""
  }
  solvencyFile: Blob | null = null;
  employmentFile: Blob | null = null;


  hasOtherCredit: boolean = false;
  creditTerm: number = 0;
  monthlyPayment: number = 0;
  incomeConfirmation: boolean = false;
  successMessage: string | null = null;

  constructor(
    private userService: UserService,
    private creditRequestService: CreditRequestService,
  ) {
  }

  onSubmit() {
    this.errorMsg = [];

    if ((!this.incomeConfirmation && this.solvencyFile == null)
      || (this.incomeConfirmation && this.employmentFile == null && this.solvencyFile == null)) {
      this.errorMsg.push('Файлы не выбраны');
      return;
    }

    if (this.creditTerm != 0) {
      this.creditRequest.currentLoans = {
        "срок кредита": this.creditTerm,
        "ежемесячная оплата": this.monthlyPayment
      };
    }

    const formatedDate = this.formatDateToIso(this.userRequest.dateOfBirth);
    if (formatedDate == null) {
      this.errorMsg.push('Некорректная введенная дата');
      return;
    }

    this.userRequest.dateOfBirth = formatedDate;

    this.userService.checkUserData({
      body: this.userRequest
    })
      .subscribe({
        next: (userId) => {
          this.creditRequest.userId = userId;
          const jsonString = JSON.stringify(this.creditRequest, null, 2);
          if (this.creditRequest.type == 'HIRED_WORKERS') {
            if (this.solvencyFile == null)
              this.errorMsg.push('Вы не прикрепили справку о подтверждении дохода');
            else if (this.employmentFile == null)
              this.errorMsg.push('Вы не прикрепили выписку о доходах');
            else {
              this.creditRequestService.addCreditRequest1({
                body: {
                  requestStr: jsonString,
                  solvency: this.solvencyFile as Blob,
                  employment: this.employmentFile as Blob
                }
              })
                .subscribe({
                  next: () => {
                    this.sendMessage('ваш запрос был отправлен на рассмотрение');
                  },
                  error: (err) => {
                    if (err.error.validationErrors)
                      this.errorMsg = err.error.validationErrors;
                    else
                      this.errorMsg.push(err.error.error);
                  }
                });
            }
          } else if (this.creditRequest.type == 'INDIVIDUAL_ENTREPRENEUR') {
            if (this.solvencyFile == null)
              this.errorMsg.push('Вы не прикрепили справку о подтверждении дохода');
            else {
              this.creditRequestService.addCreditRequest({
                body: {
                  requestStr: jsonString,
                  solvency: this.solvencyFile as Blob,
                }
              })
                .subscribe({
                  next: () => {
                    this.sendMessage('ваш запрос был отправлен на рассмотрение')
                  },
                  error: (err) => {
                    if (err.error.validationErrors)
                      this.errorMsg = err.error.validationErrors;
                    else
                      this.errorMsg.push(err.error.error);
                  }
                });
            }
          }
        },
        error: (err) => {
          if (err.error.validationErrors)
            this.errorMsg = err.error.validationErrors;
          else
            this.errorMsg.push(err.error.error);
        }
      });

    this.setDefValue();
    this.closeMessage();
  }

  private formatDateToIso(dateStr: string | undefined | null): string | null {
    // Проверка на пустую или неопределённую строку
    if (!dateStr || dateStr.trim() === '' || !dateStr.includes('.')) {
      console.error('Некорректная дата:', dateStr);
      return null; // или выбросить ошибку, если дата обязательна
    }

    const parts = dateStr.split('.');
    if (parts.length !== 3) {
      console.error('Дата должна быть в формате дд.мм.гггг:', dateStr);
      return null;
    }

    const [day, month, year] = dateStr.split('.');
    return `${year}-${month}-${day}`; // Преобразуем в ISO-формат
  }

  private sendMessage(message: string) {
    this.successMessage = message;
  }

  private closeMessage(): void {
    this.successMessage = null;
  }

  private setDefValue() {
    // this.creditRequest.currentLoans = undefined;
    // this.creditTerm = 0;
    // this.monthlyPayment = 0;
    if (!this.incomeConfirmation)
      this.employmentFile = null;
  }

  onSolvencySelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.solvencyFile = input.files[0];
    }
  }

  onEmploymentSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.employmentFile = input.files[0];
    }
  }

}
