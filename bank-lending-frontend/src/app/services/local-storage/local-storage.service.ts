import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  localValue(key: string, value: string) {
    localStorage.setItem(key, value);
  }

  getLocalValue(key:string) {
    return localStorage.getItem(key) as string;
  }

  deleteValueByKey(key:string){
    localStorage.removeItem(key)
  }
}
