package com.bitbox.board.service;

import com.bitbox.board.config.util.S3UploadUtil;
import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.request.CommentModifyRequestDto;
import com.bitbox.board.dto.request.CommentRegisterRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.dto.response.CategoryDto;
import com.bitbox.board.dto.response.CommentResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.BoardImage;
import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.ClassCategory;
import com.bitbox.board.entity.Comment;
import com.bitbox.board.entity.BoardImageId;
import com.bitbox.board.exception.BoardNotFoundException;
import com.bitbox.board.exception.CategoryNotFoundException;
import com.bitbox.board.exception.CommentNotFoundException;
import com.bitbox.board.exception.NotPermissionException;
import com.bitbox.board.repository.BoardImageRepository;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.ClassCategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import io.github.bitbox.bitbox.dto.AdminBoardRegisterDto;
import io.github.bitbox.bitbox.dto.AdminMemberBoardDto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

  private final BoardRepository boardRepository;
  private final CategoryRepository categoryRepository;
  private final CommentRepository commentRepository;
  private final ClassCategoryRepository classCategoryRepository;
  private final BoardImageRepository boardImageRepository;
  private final S3UploadUtil s3UploadUtil;

  public Boolean testImageInsert(MultipartFile image) throws IOException {
    if (!image.isEmpty()) {
      String imgUrl = s3UploadUtil.upload(image, "testImage");
      log.info(imgUrl);
    }
    return true;
  }

//  public Boolean testInsert(String test) {
//    boardImageRepository.save(BoardImage.builder()
//            .boardImageId(BoardImageId.builder()
//                .boardId("1")
//                .timestamp(LocalDateTime.now().toString())
//                .build())
//            .imgUrl("img_url")
//        .build());
//    return true;
//  }

  /**
   * 게시글 목록 조회
   *
   * @param pageable
   * @param categoryId
   * @return BoardListResponseDto
   */
  public Page<BoardResponseDto> getBoardList(Pageable pageable, Long categoryId) throws Exception {
    // Todo devlog일 경우 thumbnail 추가 -> S3연결이후
    Page<Board> boardList = boardRepository.findAllByCategoryIdFetchJoin(categoryId, pageable);

    return boardList.map(BoardResponseDto::new);
  }

  public List<CategoryDto> getCategoryList(Long categoryId) {
    Category category =
        categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);

    if (!Objects.isNull(category.getMasterCategory())) return new ArrayList<>();

    return categoryRepository.findByMasterCategory_Id(categoryId).stream()
        .map(CategoryDto::new)
        .collect(Collectors.toList());
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
        boardRepository.findAllByBoardTitleAndCategoryIdFetchJoin(title, categoryId, pageable);

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
    Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

    BoardDetailResponseDto boardDetail =
        BoardDetailResponseDto.builder().boardResponse(new BoardResponseDto(board)).build();

    List<Comment> comments = board.getComments();

    // 게시글에 댓글이 있을 경우 댓글을 포함한 결과를 반환
    if (comments != null) {
      boardDetail =
          boardDetail.toBuilder()
              .commentList(
                  comments.stream().map(CommentResponseDto::new).collect(Collectors.toList()))
              .build();
    }

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
  public boolean registerBoard(
      BoardRegisterRequestDto boardRequestDto, String memberId, String memberName)
      throws Exception {
    Category category =
        categoryRepository
            .findById(boardRequestDto.getCategoryId())
            .orElseThrow(CategoryNotFoundException::new);
    Board board = boardRequestDto.toEntity(category, memberId, memberName);
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
    Category category =
        categoryRepository
            .findById(boardRequestDto.getCategoryId())
            .orElseThrow(CategoryNotFoundException::new);
    Board board =
        boardRepository
            .findById(boardRequestDto.getBoardId())
            .orElseThrow(BoardNotFoundException::new);

    // 수정 권한 확인
    if (!board.getMemberId().equals(boardRequestDto.getMemberId()))
      throw new NotPermissionException();

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
    Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

    if (!board.getMemberId().equals(memberId) && !isManagementAuthority(authority))
      throw new NotPermissionException();

    boardRepository.save(board.toBuilder().isDeleted(true).build());
    return true;
  }

  /**
   * 사용자 게시글 조회
   *
   * @param pageable
   * @param memberId
   * @return Page<BoardResponseDto>
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
   * @return Page<CommentResponseDto>
   * @throws Exception
   */
  public Page<CommentResponseDto> getMemberComment(Pageable pageable, String memberId)
      throws Exception {
    return commentRepository.findAllByMemberId(memberId, pageable).map(CommentResponseDto::new);
  }

  /**
   * 댓글 생성
   *
   * @param commentRequestDto
   * @return boolean
   * @throws Exception
   */
  public boolean registerComment(
      CommentRegisterRequestDto commentRequestDto, String memberId, String memberName)
      throws Exception {
    Board board =
        boardRepository
            .findById(commentRequestDto.getBoardId())
            .orElseThrow(BoardNotFoundException::new);

    Comment comment = commentRequestDto.toEntity(board, memberId, memberName);

    Long masterCommentId = commentRequestDto.getMasterCommentId();
    if (masterCommentId != null && masterCommentId > 0) {
      comment =
          comment.toBuilder()
              .masterComment(
                  commentRepository
                      .findById(masterCommentId)
                      .orElseThrow(CommentNotFoundException::new))
              .build();
    }

    commentRepository.save(comment);

    return true;
  }

  /**
   * 댓글 수정
   *
   * @param commentRequestDto
   * @return boolean
   * @throws Exception
   */
  public boolean modifyComment(CommentModifyRequestDto commentRequestDto, String memberId)
      throws Exception {
    Board board =
        boardRepository
            .findById(commentRequestDto.getBoardId())
            .orElseThrow(BoardNotFoundException::new);
    Comment comment =
        commentRepository
            .findById(commentRequestDto.getCommentId())
            .orElseThrow(CommentNotFoundException::new);

    // 수정 권한 확인
    if (!comment.getMemberId().equals(memberId)) throw new NotPermissionException();

    commentRepository.save(
        comment.toBuilder()
            .board(board)
            .commentContents(commentRequestDto.getCommentContents())
            .build());

    return true;
  }

  /**
   * 댓글 삭제
   *
   * @param commentId
   * @param memberId
   * @param authority
   * @return boolean
   * @throws Exception
   */
  public boolean removeComment(Long commentId, String memberId, String authority) throws Exception {
    Comment comment =
        commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

    // 삭제 권한 확인
    if (!comment.getMemberId().equals(memberId) && !isManagementAuthority(authority))
      throw new NotPermissionException();

    commentRepository.save(comment.toBuilder().isDeleted(true).build());
    return true;
  }

  /**
   * 관리자 -> 반 생성
   *
   * @param request
   * @throws Exception
   */
  //  @KafkaListener(topics = "adminBoardTopic")
  @Transactional
  public void registerCategory(AdminBoardRegisterDto request) throws Exception {
    try {
      //      Category masterCategory = categoryRepository.findByCategoryName(ALUMNI).orElseThrow();

      Category category =
          Category.builder()
              //              .masterCategory(masterCategory)
              .categoryName(request.getClassCode())
              .build();
      categoryRepository.save(category);

      ClassCategory classCategory =
          ClassCategory.builder()
              .category(category)
              .categoryId(category.getId())
              .classId(request.getClassId())
              .build();
      classCategoryRepository.save(classCategory);
    } catch (Exception e) {
      // Todo : 반 게시판이 생성되지 않는다고 반 생성을 되돌리는 관점은 이상하다 -> 보상패턴 대신 고려?
      throw e;
    }
  }

  /**
   * 관리자 -> 반 삭제
   *
   * @param request
   * @throws Exception
   */
  //  @KafkaListener(topics = "adminMemberBoardTopic")
  @Transactional
  public void removeCategory(AdminMemberBoardDto request) throws Exception {
    try {
      ClassCategory classCategory =
          classCategoryRepository
              .findByClassId(request.getClassId())
              .orElseThrow(CategoryNotFoundException::new);

      Category category =
          categoryRepository
              .findById(classCategory.getCategoryId())
              .orElseThrow(CategoryNotFoundException::new);

      categoryRepository.save(category.toBuilder().isDeleted(true).build());
    } catch (Exception e) {
      // kafka 보상 토픽 발행
      throw e;
    }
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
