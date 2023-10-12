package com.bitbox.board.service;

import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.request.BoardUpdateRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardListResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.dto.CategoryDto;
import com.bitbox.board.dto.CommentDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
  private final CategoryRepository categoryRepository;
  private final CommentRepository commentRepository;

  /**
   * 게시글 목록 조회
   *
   * @param pageable
   * @param categoryId
   * @return BoardListResponseDto
   */
  public BoardListResponseDto getBoardList(Pageable pageable, Long categoryId) {
    CategoryDto categoryDto = categoryRepository.findById(categoryId).map(CategoryDto::new)
        .orElseThrow();
    List<BoardResponseDto> list = boardRepository.findAll(pageable)
        .stream()
        .map(BoardResponseDto::new)
        .collect(Collectors.toList());

    // Todo devlog일 경우 thumbnail 추가

    return BoardListResponseDto.builder()
        .category(categoryDto)
        .boardList(list)
        .build();
  }

  /**
   * 게시글 상세 조회
   *
   * @param boardId
   * @param memberId
   * @param authority
   * @return BoardDetailResponseDto
   */
  public BoardDetailResponseDto getBoardDetail(Long boardId, String memberId, String authority) {
    Board board = boardRepository.findById(boardId).orElseThrow();
    List<CommentDto> commentList = commentRepository.findByBoard_Id(boardId).stream()
        .map(CommentDto::new)
        .collect(Collectors.toList());

    // Todo 권한 확인 후 isManagement 추가

    return BoardDetailResponseDto.builder()
        .category(new CategoryDto(board.getCategory()))
        .boardResponse(new BoardResponseDto(board))
        .commentList(commentList)
        .build();
  }

  public boolean registerBoard(BoardRegisterRequestDto boardRequestDto, String memberId,
      String memberName) {
    Category category = categoryRepository.findById(boardRequestDto.getCategoryId()).orElseThrow();

    Board board = Board.builder()
        .boardTitle(boardRequestDto.getBoardTitle())
        .boardContents(boardRequestDto.getBoardContents())
        .memberId(memberId)
        .memberName(memberName)
        .category(category)
        .build();

    boardRepository.save(board);

    return true;
  }

  public boolean updateBoard(BoardUpdateRequestDto boardRequestDto, String memberId,
      String memberName) {
    Category category = categoryRepository.findById(boardRequestDto.getCategoryId()).orElseThrow();
    Board board = boardRepository.findById(boardRequestDto.getBoardId()).orElseThrow();

    Board updateBoard = board.toBuilder()
        .category(category)
        .boardTitle(boardRequestDto.getBoardTitle())
        .boardContents(boardRequestDto.getBoardContents())
        .build();

    boardRepository.save(updateBoard);

    return true;
  }

  public boolean deleteBoard(Long boardId, String memberId, String authority) {

    Board board = boardRepository.findById(boardId).orElseThrow();

    // Todo 권한 확인

    boardRepository.save(board.toBuilder()
        .isDeleted(true)
        .build()
    );

    return true;
  }

}
