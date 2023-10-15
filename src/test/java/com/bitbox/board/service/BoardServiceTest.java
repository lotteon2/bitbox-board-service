package com.bitbox.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bitbox.board.dto.request.BoardModifyRequestDto;
import com.bitbox.board.dto.request.BoardRegisterRequestDto;
import com.bitbox.board.dto.response.BoardDetailResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
import com.bitbox.board.entity.Comment;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private BoardService boardService;

  private Pageable pageable;
  private Category category;
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

    category = Category.builder()
        .categoryName("category")
        .build();

    categoryRepository.save(category);

    List<Board> listB = new ArrayList<>();
    List<Comment> listC = new ArrayList<>();

    // 제목, 내용 1 ~ 10
    int cnt = 0;
    while (cnt++ < SIZE) {
      Board tmpBoard = Board.builder()
          .boardTitle("title " + cnt)
          .boardContents("contents " + cnt)
          .category(category)
          .memberId(memberId)
          .memberName(memberName)
          .build();
      listB.add(tmpBoard);

      // 게시글마다 댓글 내용 : contents 1~5
      for (int i = 1; i <= 5; i++) {
        Comment tmpComment = Comment.builder()
            .board(tmpBoard)
            .memberId(memberId)
            .memberName(memberName)
            .commentContents("contents " + i)
            .build();
        listC.add(tmpComment);
      }
    }

    boardRepository.saveAll(listB);
    commentRepository.saveAll(listC);
  }

  @Test
  @Order(1)
  public void 게시글_조회_테스트() throws Exception {
    Page<BoardResponseDto> boardList = boardService.getBoardList(pageable, category.getId());

    assertEquals(category.getId(), boardList.getContent().get(0).getCategoryId());
    assertEquals(category.getCategoryName(), boardList.getContent().get(0).getCategoryName());

    int cnt = 1;
    for (BoardResponseDto boardResponseDto : boardList.getContent()) {
      assertEquals("title " + cnt, boardResponseDto.getBoardTitle());
      assertEquals("contents " + cnt, boardResponseDto.getBoardContents());
      cnt++;
    }
  }

  @Test
  @Order(2)
  public void 게시글_상세조회_테스트() throws Exception {
    Long id = (testCount - 1) * SIZE + 1;

    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, memberId, authority);

    assertEquals("title 1", boardDetail.getBoardResponse().getBoardTitle());
    assertEquals("contents 1", boardDetail.getBoardResponse().getBoardContents());
    assertEquals("contents 1", boardDetail.getCommentList().get(0).getCommentContents());
  }

  @Test
  @Order(3)
  public void 게시글_수정_테스트() throws Exception {
    Long id = (testCount - 1) * SIZE + 1;
    BoardModifyRequestDto boardModifyRequestDto = BoardModifyRequestDto.builder()
        .boardId(id)
        .categoryId(category.getId())
        .memberId(memberId)
        .boardTitle("update title")
        .boardContents("update contents")
        .build();

    boardService.modifyBoard(boardModifyRequestDto, memberId);

    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, memberId, authority);

    assertEquals("update title", boardDetail.getBoardResponse().getBoardTitle());
    assertEquals("update contents", boardDetail.getBoardResponse().getBoardContents());
  }

  @Test
  @Order(4)
  public void 게시글_삭제_테스트() throws Exception {
    Long id = (testCount - 1) * SIZE + 1;
    boardService.removeBoard(id, memberId, authority);
    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, memberId, authority);

    assertTrue(boardDetail.getBoardResponse().isDeleted());
  }

  @Test
  @Order(5)
  public void 게시글_작성_테스트() throws Exception {
    BoardRegisterRequestDto boardRegisterRequestDto = BoardRegisterRequestDto.builder()
        .categoryId(category.getId())
        .boardTitle("new title")
        .boardContents("new contents")
        .build();
    boardService.registerBoard(boardRegisterRequestDto, memberId, memberName);

    // 기존 번호 + 1
    Long id = testCount * SIZE + 1;
    BoardDetailResponseDto boardDetail = boardService.getBoardDetail(id, memberId, authority);
    assertEquals("new title", boardDetail.getBoardResponse().getBoardTitle());
    assertEquals("new contents", boardDetail.getBoardResponse().getBoardContents());
    log.info("생성시각 : " + boardDetail.getBoardResponse().getCreatedAt());
  }

  @Test
  @Order(6)
  public void 게시글_제목_검색_테스트() throws Exception {
    Page<BoardResponseDto> boardListResponse = boardService.searchBoardList(pageable,
        category.getId(), "title 1");

    assertEquals("contents 1", boardListResponse.getContent().get(0).getBoardContents());
  }
}
