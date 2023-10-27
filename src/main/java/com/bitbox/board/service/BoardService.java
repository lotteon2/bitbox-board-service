package com.bitbox.board.service;

import com.bitbox.board.config.util.S3UploadUtil;
import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.request.CategoryModifyRequestDto;
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
import com.bitbox.board.exception.BoardNotFoundException;
import com.bitbox.board.exception.CategoryMissMatchException;
import com.bitbox.board.exception.CategoryNotFoundException;
import com.bitbox.board.exception.CommentNotFoundException;
import com.bitbox.board.exception.NotPermissionException;
import com.bitbox.board.repository.BoardImageRepository;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.ClassCategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import com.bitbox.board.vo.BoardImageId;
import io.github.bitbox.bitbox.dto.AdminBoardRegisterDto;
import io.github.bitbox.bitbox.dto.AdminMemberBoardDto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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
  private static final String IMG_DIR = "board_thumbnail";
  private Map<String, String> map;

  @PostConstruct
  public void settingMap() {
    map = new HashMap<>();
    map.put("devlog", "데브로그");
    map.put("alumni", "알럼나이");
    map.put("community", "커뮤니티");
    map.put("senior", "선배들의 이야기");
  }

  /**
   * 게시글 목록 조회 boardType이 devlog일 경우 글 목록에 필요한 썸네일 추가 (DynamoDB 조회, S3 경로 반환)
   *
   * @param pageable
   * @param categoryId
   * @return BoardListResponseDto
   */
  public Page<BoardResponseDto> getBoardList(Pageable pageable, Long categoryId, String boardType)
      throws Exception {
    Page<Board> boardList = boardRepository.findAllByCategoryIdFetchJoin(categoryId, pageable);
    if (!boardList.getContent().isEmpty()) {
      String masterCategoryName =
          boardList.getContent().get(0).getCategory().getMasterCategory().getCategoryName();
      if (!masterCategoryName.equals(map.get(boardType))) throw new CategoryMissMatchException();
    }

    Page<BoardResponseDto> response = boardList.map(BoardResponseDto::new);

    if (boardType.equals("devlog")) {
      response
          .getContent()
          .forEach(
              boardDto -> {
                List<BoardImage> thumbnailList =
                    boardImageRepository.findByBoardId(boardDto.getBoardId());
                String thumbnail =
                    thumbnailList.size() != 0
                        ? thumbnailList.get(thumbnailList.size() - 1).getImgUrl()
                        : "not exist";
                boardDto.toBuilder().thumbnail(thumbnail).build();
              });
    }
    return response;
  }

  /**
   * 카테고리 목록 조회 하위 카테고리일 경우 단일 리스트 반환 상위 카테고리일 경우 하위 카테고리 모두 반환
   *
   * @param categoryId
   * @return
   */
  public List<CategoryDto> getCategoryList(Long categoryId) {
    Category category =
        categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);

    if (!Objects.isNull(category.getMasterCategory()))
      return Collections.singletonList(new CategoryDto(category));

    return categoryRepository.findByMasterCategory_Id(categoryId).stream()
        .map(CategoryDto::new)
        .collect(Collectors.toList());
  }

  /**
   * 게시글 목록 검색 제목 검색
   *
   * @param pageable
   * @param categoryId
   * @param title
   * @return BoardListResponseDto
   */
  public Page<BoardResponseDto> searchBoardList(
      Pageable pageable, Long categoryId, String title, String boardType) throws Exception {

    Page<Board> boardList =
        boardRepository.findAllByBoardTitleAndCategoryIdFetchJoin(title, categoryId, pageable);

    if (boardList.getSize() > 0) {
      String masterCategoryName =
          boardList.getContent().get(0).getCategory().getMasterCategory().getCategoryName();
      if (!masterCategoryName.equals(map.get(boardType))) throw new CategoryMissMatchException();
    }

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
    if (!Objects.isNull(comments)) {
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
      BoardRegisterRequestDto boardRequestDto,
      String memberId,
      String memberName,
      String memberPrfoileImg)
      throws Exception {

    Category category =
        categoryRepository
            .findById(boardRequestDto.getCategoryId())
            .orElseThrow(CategoryNotFoundException::new);

    Board board = boardRequestDto.toEntity(category, memberId, memberName, memberPrfoileImg);
    boardRepository.save(board);

    // DDB에 Board thumnail 저장
    if (!Objects.isNull(boardRequestDto.getThumbnail())) {
      boardImageRepository.save(
          BoardImage.builder()
              .boardImageId(
                  BoardImageId.builder()
                      .boardId(board.getId())
                      .timestamp(LocalDateTime.now().toString())
                      .build())
              .imgUrl(boardRequestDto.getThumbnail())
              .build());
    }
    return true;
  }

  /**
   * 게시글 수정
   *
   * @param boardRequestDto
   * @return boolean
   */
  @Transactional
  public boolean modifyBoard(
      BoardModifyRequestDto boardRequestDto, String memberId, String authority) throws Exception {
    Category category =
        categoryRepository
            .findById(boardRequestDto.getCategoryId())
            .orElseThrow(CategoryNotFoundException::new);

    Board board =
        boardRepository
            .findById(boardRequestDto.getBoardId())
            .orElseThrow(BoardNotFoundException::new);

    // 수정 권한 확인
    if (!board.getMemberId().equals(memberId) && !isManagementAuthority(authority))
      throw new NotPermissionException();

    boardRepository.save(
        board.toBuilder()
            .category(category)
            .boardTitle(boardRequestDto.getBoardTitle())
            .boardContents(boardRequestDto.getBoardContents())
            .build());

    // DDB에 Board thumnail 저장
    if (!Objects.isNull(boardRequestDto.getThumbnail())) {
      boardImageRepository.save(
          BoardImage.builder()
              .boardImageId(
                  BoardImageId.builder()
                      .boardId(board.getId())
                      .timestamp(LocalDateTime.now().toString())
                      .build())
              .imgUrl(boardRequestDto.getThumbnail())
              .build());
    }
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
  @Transactional
  public boolean registerComment(
      CommentRegisterRequestDto commentRequestDto, String memberId, String memberName, String memberProfileImg)
      throws Exception {
    Board board =
        boardRepository
            .findById(commentRequestDto.getBoardId())
            .orElseThrow(BoardNotFoundException::new);

    Comment comment = commentRequestDto.toEntity(board, memberId, memberName, memberProfileImg);

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
  @Transactional
  public boolean modifyComment(CommentModifyRequestDto commentRequestDto, String memberId)
      throws Exception {
    Comment comment =
        commentRepository
            .findById(commentRequestDto.getCommentId())
            .orElseThrow(CommentNotFoundException::new);

    // 수정 권한 확인
    if (!comment.getMemberId().equals(memberId)) throw new NotPermissionException();

    commentRepository.save(
        comment.toBuilder()
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
  @Transactional
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
   * 카테고리 생성
   *
   * @param categoryName
   * @param boardType
   * @param authority
   * @return
   */
  @Transactional
  public boolean registerCategory(String categoryName, String boardType, String authority) {
    if (!isManagementAuthority(authority)) throw new NotPermissionException();

    Category masterCategory =
        categoryRepository
            .findByCategoryName(map.get(boardType))
            .orElseThrow(CategoryNotFoundException::new);

    Category category =
        Category.builder().categoryName(categoryName).masterCategory(masterCategory).build();

    categoryRepository.save(category);
    return true;
  }

  /**
   * 카테고리 수정
   *
   * @param request
   * @param authority
   * @return
   */
  @Transactional
  public boolean modifyCategory(CategoryModifyRequestDto request, String authority) {
    if (!isManagementAuthority(authority)) throw new NotPermissionException();

    Category category =
        categoryRepository
            .findById(request.getCategoryId())
            .orElseThrow(CategoryNotFoundException::new);

    categoryRepository.save(category.toBuilder().categoryName(request.getCategoryName()).build());

    return true;
  }

  /**
   * 카테고리 삭제
   *
   * @param categoryId
   * @param authority
   * @return
   */
  @Transactional
  public boolean deleteCategory(Long categoryId, String authority) {
    if (!isManagementAuthority(authority)) throw new NotPermissionException();

    Category category =
        categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
    categoryRepository.save(category.toBuilder().isDeleted(true).build());

    return true;
  }

  /**
   * 관리자 -> 반 생성 관리자가 반 생성 시에 알럼나이에 반별 게시판이 자동으로 생성된다
   *
   * @param request
   * @throws Exception
   */
  //  @KafkaListener(topics = "adminBoardTopic")
  @Transactional
  public void registerAdminCategory(AdminBoardRegisterDto request) throws Exception {
    try {
      Category alumni = categoryRepository.findByCategoryName("알럼나이").orElseThrow();

      Category alumniNewCategory =
          Category.builder().masterCategory(alumni).categoryName(request.getClassCode()).build();
      categoryRepository.save(alumniNewCategory);

      ClassCategory alumniClassCategory =
          ClassCategory.builder()
              .category(alumniNewCategory)
              .categoryId(alumniNewCategory.getId())
              .classId(request.getClassId())
              .build();
      classCategoryRepository.save(alumniClassCategory);
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
  public void removeAdminCategory(AdminMemberBoardDto request) throws Exception {
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
