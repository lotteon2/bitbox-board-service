package com.bitbox.board.exception.advice;

import com.bitbox.board.exception.BoardNotFoundException;
import com.bitbox.board.exception.CategoryNotFoundException;
import com.bitbox.board.exception.CommentNotFoundException;
import com.bitbox.board.exception.NotPermissionException;
import com.bitbox.board.exception.response.ErrorResponse;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

  @ExceptionHandler(BoardNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBoardNotFoundException(BoardNotFoundException e) {
    return getErrorResponse(e);
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleCategoryNotFoundException(CategoryNotFoundException e) {
    return getErrorResponse(e);
  }

  @ExceptionHandler(CommentNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleCommentNotFoundException(CommentNotFoundException e) {
    return getErrorResponse(e);
  }

  @ExceptionHandler(NotPermissionException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleNotPermissionException(NotPermissionException e) {
    return getErrorResponse(e);
  }

  @ExceptionHandler(InvalidDataAccessApiUsageException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidDataAccessApiUsageException(
      InvalidDataAccessApiUsageException e) {
    return ErrorResponse.builder().message("잘못된 검색 범위입니다.").build();
  }

  private ErrorResponse getErrorResponse(RuntimeException e) {
    return ErrorResponse.builder().message(e.getMessage()).build();
  }
}
