package kh.edu.istad.platform.iamapplicationservice.usecase.security;

import kh.edu.istad.platform.iamapplicationservice.result.common.PageResult;
import kh.edu.istad.platform.iamapplicationservice.result.security.LoginAttemptResult;

import java.util.UUID;

public interface ListLoginHistoryUseCase {

    PageResult<LoginAttemptResult> execute(UUID userUuid, int page, int size);
}
