package org.study.bbsdemo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler BDD 단위 테스트")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Given IllegalArgumentException이 발생했을 때, When 핸들러를 호출하면, Then 400 Bad Request 응답을 반환한다.")
    void handleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("잘못된 요청 인자입니다.");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("잘못된 요청 인자입니다.");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Given 일반 Exception이 발생했을 때, When 핸들러를 호출하면, Then 500 Internal Server Error 응답을 반환한다.")
    void handleGeneralException() {
        // Given
        RuntimeException exception = new RuntimeException("서버 내부 오류가 발생했습니다.");

        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGeneralException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("서버 내부 오류가 발생했습니다.");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
