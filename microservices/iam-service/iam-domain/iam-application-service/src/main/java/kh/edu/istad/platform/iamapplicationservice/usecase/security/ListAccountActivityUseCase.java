package kh.edu.istad.platform.iamapplicationservice.usecase.security;

import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.security.AccountActivityResult;

import java.util.UUID;

public interface ListAccountActivityUseCase {

    PageResult<AccountActivityResult> execute(UUID userUuid, int page, int size);
}
