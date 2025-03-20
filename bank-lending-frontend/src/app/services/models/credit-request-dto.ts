export interface CreditRequestDto {
  userId: number,
  loanPurpose: string,
  sum: number,
  immovableProperty?: boolean,
  movableProperty?: boolean,
  type: 'HIRED_WORKERS' | 'INDIVIDUAL_ENTREPRENEUR',
  currentLoans?: Record<string, any>;
}
