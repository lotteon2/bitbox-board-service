package com.bitbox.board.service;

import com.bitbox.board.dto.request.CommentModifyRequestDto;
import com.bitbox.board.dto.request.CommentRegisterRequestDto;
import com.bitbox.board.dto.response.CommentResponseDto;
import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.ClassCategory;
import com.bitbox.board.entity.Comment;
import com.bitbox.board.repository.BoardCustomRepository;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.ClassCategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
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
  private final ClassCategoryRepository classCategoryRepository;
  private static final String ALUMNI = "alumni";

  /**
   * 게시글 목록 조회
   *
   * @param pageable
   * @param categoryId
   * @return BoardListResponseDto
   */
  public Page<BoardResponseDto> getBoardList(Pageable pageable, Long categoryId) throws Exception {
    // Todo devlog일 경우 thumbnail 추가 -> S3연결이후
    Page<Board> boardList =
        boardCustomRepository.findAllByCategoryIdFetchJoin(categoryId, pageable);

    return boardList.map(BoardResponseDto::new);
  }

  /**
   * 게시글 제목 검색
   *
   * @param pageable
   * @param categoryId
   * @param title
   * @return BoardListResponseDto
   */
  public Page<BoardResponseDto> searchBoardList(Pageable pageable, Long categoryId, String title)
      throws Exception {
    Page<Board> boardList =
        boardCustomRepository.findAllByBoardTitleAndCategoryIdFetchJoin(
            title, categoryId, pageable);

    return boardList.map(BoardResponseDto::new);
  }

  /**
   * 게시글 상세 조회
   *
   * @param boardId
   * @param memberId
   * @param authority
   * @return BoardDetailResponseDto
   */
  public BoardDetailResponseDto getBoardDetail(Long boardId, String memberId, String authority)
      throws Exception {
    Board board = boardRepository.findById(boardId).orElseThrow();
    List<Comment> comments = board.getComments();

    BoardDetailResponseDto boardDetail =
        BoardDetailResponseDto.builder()
            .boardResponse(new BoardResponseDto(board))
            .commentList(
                comments.stream().map(CommentResponseDto::new).collect(Collectors.toList()))
            .build();

    // 게시글 권한이 확인될 시 응답에 수정권한 부여
    if (memberId.equals(board.getMemberId()) || isManagementAuthority(authority)) {
      return boardDetail.toBuilder().isManagement(true).build();
    }

    return boardDetail;
  }

  /**
   * 게시글 작성
   *
   * @param boardRequestDto
   * @return boolean
   */
  @Transactional
  public boolean registerBoard(BoardRegisterRequestDto boardRequestDto) throws Exception {
    Category category = categoryRepository.findById(boardRequestDto.getCategoryId()).orElseThrow();
    Board board = boardRequestDto.toEntity(category);
    boardRepository.save(board);
    return true;
  }

  /**
   * 게시글 수정
   *
   * @param boardRequestDto
   * @return boolean
   */
  @Transactional
  public boolean modifyBoard(BoardModifyRequestDto boardRequestDto) throws Exception {
    Category category = categoryRepository.findById(boardRequestDto.getCategoryId()).orElseThrow();
    Board board = boardRepository.findById(boardRequestDto.getBoardId()).orElseThrow();

    // 수정 권한 확인, 추후 Exception 상세
    /*
    if (!board.getMemberId().equals(memberId))
      throw new Exception();
    */

    Board updateBoard =
        board.toBuilder()
            .category(category)
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
  public boolean removeBoard(Long boardId, String memberId, String authority) throws Exception {
    Board board = boardRepository.findById(boardId).orElseThrow();

    // 수정 권한 확인, 추후 Exception 상세
    /*
    if (!board.getMemberId().equals(memberId))
      throw new Exception();
    */

    boardRepository.save(board.toBuilder().isDeleted(true).build());
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

  /**
   * 사용자 게시글 조회
   *
   * @param pageable
   * @param memberId
   * @return
   * @throws Exception
   */
  public Page<BoardResponseDto> getMemberBoard(Pageable pageable, String memberId)
      throws Exception {
    return boardRepository.findAllByMemberId(memberId, pageable).map(BoardResponseDto::new);
  }

  /**
   * 사용자 댓글 조회
   *
   * @param pageable
   * @param memberId
   * @return
   * @throws Exception
   */
  public Page<CommentResponseDto> getMemberComment(Pageable pageable, String memberId)
      throws Exception {
    return commentRepository.findAllByMemberId(memberId, pageable).map(CommentResponseDto::new);
  }

  public boolean registerComment(CommentRegisterRequestDto commentRequestDto) throws Exception {
    Board board = boardRepository.findById(commentRequestDto.getBoardId()).orElseThrow();

    Comment comment = commentRequestDto.toEntity(board);

    Long masterCommentId = commentRequestDto.getMasterCommentId();
    if (masterCommentId != null || masterCommentId > 0) {
      comment =
          comment.toBuilder()
              .masterComment(commentRepository.findById(masterCommentId).orElseThrow())
              .build();
    }

    commentRepository.save(comment);

    return true;
  }

  public boolean modifyComment(CommentModifyRequestDto commentRequestDto) throws Exception {
    Board board = boardRepository.findById(commentRequestDto.getBoardId()).orElseThrow();
    Comment comment = commentRepository.findById(commentRequestDto.getCommentId()).orElseThrow();

    // 수정 권한 확인, 추후 Exception 상세
    /*
    if (!comment.getMemberId().equals(memberId))
      throw new Exception();
    */

    commentRepository.save(
        comment.toBuilder()
            .board(board)
            .commentContents(commentRequestDto.getCommentContents())
            .build());

    return true;
  }

  public boolean removeComment(Long commentId, String memberId) throws Exception {
    Comment comment = commentRepository.findById(commentId).orElseThrow();

    // 수정 권한 확인, 추후 Exception 상세
    /*
    if (!comment.getMemberId().equals(memberId))
      throw new Exception();
    */

    commentRepository.save(comment.toBuilder().isDeleted(true).build());
    return true;
  }

  // 반 생성
  //  @KafkaListener(topics = "")
  @Transactional
  public void registerCategory(String categoryName, Long classId) throws Exception {
    try {
      Category masterCategory = categoryRepository.findByCategoryName(ALUMNI).orElseThrow();
      Category category = Category.builder()
          .masterCategory(masterCategory)
          .categoryName(categoryName)
          .build();
      categoryRepository.save(category);

      ClassCategory classCategory = ClassCategory.builder()
          .category(category)
          .classId(classId)
          .build();

      classCategoryRepository.save(classCategory);
    } catch (Exception e) {
      // kafka 보상 토픽 발행
      
      throw e;
    }
  }

  //  @KafkaListener(topics = "")
  @Transactional
  public void removeCategory(Long categoryId) throws Exception {
    try {
      Category category = categoryRepository.findById(categoryId).orElseThrow();
      categoryRepository.save(category.toBuilder().isDeleted(true).build());
    } catch (Exception e) {
      // kafka 보상 토픽 발행
      throw e;
    }
  }
}
