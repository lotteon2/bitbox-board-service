package com.bitbox.board.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BoardCRUDTest {

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CommentRepository commentRepository;

  private Pageable pageable;
  private Category category;
  // 게시글 생성 개수
  private static final int SIZE = 10;
  // 페이지 크기
  private static final int PAGE_SIZE = 3;
  // 테스트 진행 갯수
  private static long testCount = 0L;

  @BeforeEach
  public void before() {
    testCount++;
    pageable = PageRequest.of(0, PAGE_SIZE);

    category = Category.builder()
        .categoryName("category")
        .build();

    categoryRepository.save(category);

    List<Board> list = new ArrayList<>();

    // 1 ~ 10
    int cnt = 0;
    while (cnt++ < SIZE) {
      list.add(
          Board.builder()
              .boardTitle("title " + cnt)
              .boardContents("contents " + cnt)
              .category(category)
              .memberId("member_id")
              .memberName("member_name")
              .build()
      );
    }

    boardRepository.saveAll(list);
  }

  @Test
  @Order(1)
  public void 게시글_조회_테스트() {
    List<BoardResponseDto> boardList = boardRepository.findAll(pageable)
        .stream()
        .map(BoardResponseDto::new)
        .collect(Collectors.toList());

    int cnt = 1;
    for (BoardResponseDto board : boardList) {
      assertEquals("title " + cnt, board.getBoardTitle());
      assertEquals("contents " + cnt, board.getBoardContents());
      cnt++;
    }
  }

  @Test
  @Order(2)
  public void 게시글_삭제_테스트() {
    Long id = (testCount - 1) * SIZE + 1;
    boardRepository.deleteById(id);

    assertThrows(NoSuchElementException.class,
        () -> boardRepository.findById(id).orElseThrow());
  }

  @Test
  @Order(3)
  public void 게시글_삭제_상태변경_테스트() {
    Long id = (testCount - 1) * SIZE + 1;
    Board board = boardRepository.findById(id).orElseThrow();

    boardRepository.save(
        board.toBuilder()
            .isDeleted(true)
            .build()
    );

    Board deletedBoard = boardRepository.findById(id).orElseThrow();
    assertTrue(deletedBoard.isDeleted());
  }

  @Test
  @Order(4)
  public void 게시글_수정_테스트() {
    Long id = (testCount - 1) * SIZE + 1;
    Board board = boardRepository.findById(id).orElseThrow();
    assertEquals("title 1", board.getBoardTitle());

    board = board.toBuilder()
        .boardTitle("update title")
        .build();

    boardRepository.save(board);

    Board updateBoard = boardRepository.findById(id).orElseThrow();
    assertEquals("update title", updateBoard.getBoardTitle());
  }

  @Test
  @Order(5)
  public void 게시글_생성_테스트() {
    Board board = Board.builder()
        .boardTitle("insert Title")
        .boardContents("insert Contents")
        .category(category)
        .memberId("member_id")
        .memberName("member_name")
        .build();

    boardRepository.save(board);

    Board insertBoard = boardRepository.findAllByBoardTitle("insert Title", pageable).getContent()
        .get(0);

    assertEquals("insert Contents", insertBoard.getBoardContents());
  }
}
