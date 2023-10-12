package com.bitbox.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bitbox.board.dto.response.BoardListResponseDto;
import com.bitbox.board.dto.response.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.entity.Category;
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
              .memberName("member_name")
              .isDeleted(false)
              .build()
      );
    }

    boardRepository.saveAll(list);
  }

  @Test
  @Order(1)
  public void 게시판_조회_테스트() {
    BoardListResponseDto boardPage = boardService.getBoardList(pageable, category.getId());

    assertEquals(category.getId(), boardPage.getCategory().getCategoryId());
    assertEquals(category.getCategoryName(), boardPage.getCategory().getCategoryName());

    int cnt = 1;
    for (BoardResponseDto boardResponseDto : boardPage.getBoardList()) {
      assertEquals("title " + cnt, boardResponseDto.getBoardTitle());
      assertEquals("contents " + cnt, boardResponseDto.getBoardContents());
      cnt++;
    }

  }

}
