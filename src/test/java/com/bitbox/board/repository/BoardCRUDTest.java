package com.bitbox.board.repository;

import com.bitbox.board.dto.BoardResponseDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
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

  @BeforeEach
  public void before() {
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
              .isDeleted(false)
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
      assertEquals(cnt, board.getBoardId());
      assertEquals("title " + cnt, board.getBoardTitle());
      assertEquals("contents " + cnt, board.getBoardContents());
      assertEquals("member_id", board.getMemberId());
      cnt++;
    }
  }

  @Test
  @Order(2)
  public void 게시글_삭제_테스트() throws Exception {
    int orderValue = getOrderValue("게시글_삭제_테스트");
    Long id = (long)orderValue * SIZE + 1L;

    boardRepository.deleteById(id);

    assertThrows(NoSuchElementException.class,
        () -> boardRepository.findById(id).orElseThrow());
  }

  @Test
  @Order(3)
  public void 게시글_수정_테스트() throws Exception {
    int orderValue = getOrderValue("게시글_수정_테스트");
    Long id = (long)orderValue * SIZE + 1L;

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
  @Order(4)
  public void 게시글_생성_테스트() throws Exception {
    Board board = Board.builder()
        .boardTitle("insert Title")
        .boardContents("insert Contents")
        .category(category)
        .memberId("member_id")
        .build();

    boardRepository.save(board);

    Board insertBoard = boardRepository.findByBoardTitle("insert Title").orElseThrow();

    assertEquals("insert Contents", insertBoard.getBoardContents());
  }
  
  // 메소드의 Order 어노테이션의 값을 반환
  public int getOrderValue(String methodName) throws Exception {
    return BoardCRUDTest.class.getDeclaredMethod(methodName).getAnnotation(Order.class).value() - 1;
  }
}
