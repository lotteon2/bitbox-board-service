package com.bitbox.board.service;

import com.bitbox.board.dto.CategoryDto;
import com.bitbox.board.dto.CommentDto;
import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardListResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.Comment;
import com.bitbox.board.repository.BoardCustomRepository;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
  private final BoardCustomRepository boardCustomRepository;
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
    // Todo devlog일 경우 thumbnail 추가 -> S3연결이후
    Page<Board> boardList = boardCustomRepository.findAllByCategoryIdFetchJoin(categoryId,
        pageable);

    return BoardListResponseDto.builder()
        .category(new CategoryDto(boardList.getContent().get(0).getCategory()))
        .boardList(boardList
            .stream()
            .map(BoardResponseDto::new)
            .collect(Collectors.toList()
            ))
        .build();
  }

  /**
   * 게시글 제목 검색
   *
   * @param pageable
   * @param categoryId
   * @param title
   * @return BoardListResponseDto
   */
  public BoardListResponseDto searchBoardList(Pageable pageable, Long categoryId, String title) {
    Page<Board> boardList = boardCustomRepository.findAllByBoardTitleAndCategoryIdFetchJoin(title,
        categoryId, pageable);

    return BoardListResponseDto.builder()
        .category(new CategoryDto(boardList.getContent().get(0).getCategory()))
        .boardList(boardList
            .stream()
            .map(BoardResponseDto::new)
            .collect(Collectors.toList()
            ))
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
    List<Comment> comments = board.getComments();

    BoardDetailResponseDto boardDetail = BoardDetailResponseDto.builder()
        .category(new CategoryDto(board.getCategory()))
        .boardResponse(new BoardResponseDto(board))
        .commentList(comments.stream().map(CommentDto::new).collect(Collectors.toList()))
        .build();

    // 게시글 권한이 확인될 시 응답에 수정권한 부여
    if (memberId.equals(board.getMemberId()) || isManagementAuthority(authority)) {
      return boardDetail.toBuilder()
          .isManagement(true)
          .build();
    }

    return boardDetail;
  }

  /**
   * 게시글 작성
   *
   * @param boardRequestDto
   * @param memberId
   * @param memberName
   * @return boolean
   */
  @Transactional
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

  /**
   * 게시글 수정
   *
   * @param boardRequestDto
   * @param memberId
   * @return boolean
   */
  @Transactional
  public boolean modifyBoard(BoardModifyRequestDto boardRequestDto, String memberId)
      throws Exception {
    Board board = boardRepository.findById(boardRequestDto.getBoardId()).orElseThrow();

    // 수정 권한 확인, 추후 Exception 상세
    /*
    if (!board.getMemberId().equals(memberId))
      throw new Exception();
    */

    Board updateBoard = board.toBuilder()
        .category(board.getCategory())
        .boardTitle(boardRequestDto.getBoardTitle())
        .boardContents(boardRequestDto.getBoardContents())
        .build();

    boardRepository.save(updateBoard);
    return true;
  }

  /**
   * 게시글 삭제
   *
   * @param boardId
   * @param memberId
   * @param authority
   * @return boolean
   */
  @Transactional
  public boolean removeBoard(Long boardId, String memberId, String authority) {
    Board board = boardRepository.findById(boardId).orElseThrow();

    // 수정 권한 확인, 추후 Exception 상세
    /*
    if (!board.getMemberId().equals(memberId))
      throw new Exception();
    */

    boardRepository.save(
        board.toBuilder()
            .isDeleted(true)
            .build()
    );
    return true;
  }

  /**
   * 게시글에 대한 추가 관리 권한 확인
   *
   * @param authority
   * @return boolean
   */
  public boolean isManagementAuthority(String authority) {
    return authority.equals("ADMIN") || authority.equals("MANAGER");
  }

}
