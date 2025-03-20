package com.sharafutdinov.bank_lending_api.user;

import com.sharafutdinov.bank_lending_api.common.PageResponse;

public interface UserService {

    Long checkPassportData(UserRequest request);

    UserResponse getUserById(Long userId);

    PageResponse<UserResponse> getAllBankClient(int page, int size);

    PageResponse<UserResponse> getAllBankClientBySort(int page, int size, boolean isAsc);

    UserResponse getUserBySerialNumber(String serialNum);
}
