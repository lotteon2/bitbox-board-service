package com.bitbox.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.request.CommentRegisterRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.Comment;
import com.bitbox.board.exception.BoardNotFoundException;
import com.bitbox.board.exception.CategoryNotFoundException;
import com.bitbox.board.exception.NotPermissionException;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.ClassCategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import io.github.bitbox.bitbox.dto.AdminBoardRegisterDto;
import io.github.bitbox.bitbox.dto.AdminMemberBoardDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BoardServiceTest {

  @Autowired private BoardRepository boardRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private CommentRepository commentRepository;

  @Autowired private ClassCategoryRepository classCategoryRepository;

  @Autowired private BoardService boardService;

  @Autowired EntityManager entityManager;

  private Pageable pageable;
  private List<Category> categoryList;
  private List<Board> boardList;
  private List<Comment> commentList;
  // 게시글 생성 개수
  private static final int SIZE = 10;
  // 페이지 크기
  private static final int PAGE_SIZE = 3;
  // 테스트 진행 갯수
  private static long testCount = 0L;

  private final String memberId = "member_id";
  private final String memberName = "member_name";
  private final String authority = "GENERAL";

  @BeforeEach
  public void before() {
    testCount++;
    pageable = PageRequest.of(0, PAGE_SIZE);

    categoryList = new ArrayList<>();
    categoryList.add(Category.builder().categoryName("community").build());
    categoryList.add(Category.builder().categoryName("question").build());
    categoryList.add(Category.builder().categoryName("share").build());
    categoryRepository.saveAll(categoryList);

    boardList = new ArrayList<>();
    boardList.add(
        Board.builder()
            .boardTitle("면접 질문 있어요")
            .boardContents("면접쉬움?")
            .category(categoryList.get(1))
            .memberName("준비생")
            .memberId("member_100")
            .build());

    boardList.add(
        Board.builder()
            .boardTitle("밥집 어디감?")
            .boardContents("한뷔ㄱ?")
            .category(categoryList.get(1))
            .memberName("준비준비생")
            .memberId("member_101")
            .build());

    boardList.add(
        Board.builder()
            .boardTitle("꿀팁 공유합니다")
            .boardContents("칠리새우 맛있음")
            .category(categoryList.get(2))
            .memberName("1기 선배님")
            .memberId("member_1")
            .build());

    boardRepository.saveAll(boardList);

    commentList = new ArrayList<>();

    commentList.add(
        Comment.builder()
            .board(boardList.get(0))
            .commentContents("ㅇㅇ")
            .memberName("1기 선배님")
            .memberId("member_1")
            .build());

    commentList.add(
        Comment.builder()
            .board(boardList.get(0))
            .masterComment(commentList.get(0))
            .commentContents("ㄴㄴ")
            .memberName("1기 망나니")
            .memberId("member_2")
            .build());

    commentList.add(
        Comment.builder()
            .board(boardList.get(2))
            .commentContents("ㄳ")
            .memberName("준비준비생")
            .memberId("member_101")
            .build());

    commentRepository.saveAll(commentList);
    entityManager.clear();
  }

  @Test
  @Order(1)
  public void 게시글_조회_테스트() throws Exception {
    Category category = categoryList.get(1);
    Page<BoardResponseDto> response = boardService.getBoardList(pageable, category.getId(), "question");

    assertEquals(category.getId(), response.getContent().get(0).getCategoryId());
    assertEquals(category.getCategoryName(), response.getContent().get(0).getCategoryName());
    assertEquals("면접 질문 있어요", response.getContent().get(0).getBoardTitle());
    assertEquals("면접쉬움?", response.getContent().get(0).getBoardContents());
  }

  @Test
  @Order(2)
  public void 게시글_조회_범위_테스트() throws Exception {
    assertThrows(
        InvalidDataAccessApiUsageException.class,
        () -> {
          Category category = categoryList.get(1);
          Pageable pageable1 = PageRequest.of(50, 100);

          Page<BoardResponseDto> response = boardService.getBoardList(pageable1, category.getId(), "question");

          assertEquals(category.getId(), response.getContent().get(0).getCategoryId());
          assertEquals(category.getCategoryName(), response.getContent().get(0).getCategoryName());
          assertEquals("면접 질문 있어요", response.getContent().get(0).getBoardTitle());
          assertEquals("면접쉬움?", response.getContent().get(0).getBoardContents());
        });
  }

  @Test
  @Order(3)
  public void 게시글_상세조회_테스트() throws Exception {
    BoardDetailResponseDto boardDetail =
        boardService.getBoardDetail(boardList.get(0).getId(), memberId, authority);

    assertEquals("면접 질문 있어요", boardDetail.getBoardResponse().getBoardTitle());
    assertEquals("면접쉬움?", boardDetail.getBoardResponse().getBoardContents());
    assertEquals("ㅇㅇ", boardDetail.getCommentList().get(0).getCommentContents());
  }

  @Test
  @Order(4)
  public void 게시글_수정_테스트() throws Exception {
    Long id = boardList.get(0).getId();
    BoardModifyRequestDto boardModifyRequestDto =
        BoardModifyRequestDto.builder()
            .boardId(id)
            .categoryId(categoryList.get(0).getId())
            .boardTitle("update title")
            .boardContents("update contents")
            .build();

    boardService.modifyBoard(boardModifyRequestDto, "member_100", authority);

    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, memberId, authority);

    assertEquals("update title", boardDetail.getBoardResponse().getBoardTitle());
    assertEquals("update contents", boardDetail.getBoardResponse().getBoardContents());
  }

  @Test
  @Order(5)
  public void 게시글_수정_권한_테스트() throws Exception {
    assertThrows(
        NotPermissionException.class,
        () -> {
          Long id = boardList.get(0).getId();
          BoardModifyRequestDto boardModifyRequestDto =
              BoardModifyRequestDto.builder()
                  .boardId(id)
                  .categoryId(categoryList.get(0).getId())
                  .boardTitle("update title")
                  .boardContents("update contents")
                  .build();

          boardService.modifyBoard(boardModifyRequestDto, "member_Error", authority);
        });
  }

  @Test
  @Order(6)
  public void 게시글_삭제_테스트() throws Exception {
    Long id = boardList.get(0).getId();
    boardService.removeBoard(id, "member_100", authority);
    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, "member_100", authority);
    assertTrue(boardDetail.getBoardResponse().isDeleted());

    Long id2 = boardList.get(1).getId();
    boardService.removeBoard(id2, memberId, "MANAGER");
    BoardDetailResponseDto boardDetail2 = boardService.getBoardDetail(id2, memberId, "MANAGER");
    assertTrue(boardDetail2.getBoardResponse().isDeleted());
  }

  @Test
  @Order(7)
  public void 게시글_작성_테스트() throws Exception {
    BoardRegisterRequestDto boardRegisterRequestDto =
        BoardRegisterRequestDto.builder()
            .categoryId(categoryList.get(0).getId())
            .boardTitle("새로운 게시글 작성")
            .boardContents("새로운 게시글 내용임")
            .build();

//    boardService.registerBoard(boardRegisterRequestDto, memberId, memberName, "member_profile");

    Long id =
        boardRepository
            .findByBoardTitle("새로운 게시글 작성")
            .orElseThrow(BoardNotFoundException::new)
            .getId();

    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, memberId, authority);
    assertEquals("새로운 게시글 작성", boardDetail.getBoardResponse().getBoardTitle());
    assertEquals("새로운 게시글 내용임", boardDetail.getBoardResponse().getBoardContents());
  }

  @Test
  @Order(8)
  public void 게시글_제목_검색_테스트() throws Exception {
    Page<BoardResponseDto> boardListResponse =
        boardService.searchBoardList(pageable, categoryList.get(1).getId(), "면접 질문 있어요", "question");

    assertEquals("면접쉬움?", boardListResponse.getContent().get(0).getBoardContents());
  }

  @Test
  @Order(9)
  public void 댓글_작성_테스트() throws Exception {
    CommentRegisterRequestDto commentRegisterRequestDto = CommentRegisterRequestDto.builder()
        .boardId(boardList.get(0).getId())
        .commentContents("새로운 댓글 입니다")
        .build();
    boardService.registerComment(commentRegisterRequestDto, memberId, memberName, "test_profile");
  }

  @Test
  @Order(10)
  public void 반_생성_테스트() throws Exception {
    AdminBoardRegisterDto request = AdminBoardRegisterDto.builder()
        .classId(10L)
        .classCode("JX411")
        .build();

    boardService.registerAdminCategory(request);

    assertEquals(request.getClassCode(), classCategoryRepository.findByClassId(request.getClassId()).orElseThrow(
        CategoryNotFoundException::new).getCategory().getCategoryName());
  }

  @Test
  @Order(11)
  public void 반_삭제_테스트() throws Exception {
    AdminBoardRegisterDto request = AdminBoardRegisterDto.builder()
        .classId(10L)
        .classCode("JX411")
        .build();

    boardService.registerAdminCategory(request);

    AdminMemberBoardDto adminMemberBoardDto = AdminMemberBoardDto.builder()
        .classId(request.getClassId())
        .requestDate(LocalDateTime.now())
        .build();

    boardService.removeAdminCategory(adminMemberBoardDto);

    assertTrue(classCategoryRepository.findByClassId(request.getClassId()).orElseThrow(
        CategoryNotFoundException::new).getCategory().isDeleted());
  }
}
