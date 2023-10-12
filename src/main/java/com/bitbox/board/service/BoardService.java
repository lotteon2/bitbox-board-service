package com.bitbox.board.service;

import com.bitbox.board.dto.BoardListResponseDto;
import com.bitbox.board.dto.BoardResponseDto;
import com.bitbox.board.entity.Board;
import com.bitbox.board.repository.BoardRepository;
import com.bitbox.board.repository.CategoryRepository;
import com.bitbox.board.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final CategoryRepository categoryRepository;
  private final CommentRepository commentRepository;

  //   Pageable paging = PageRequest.of(0, 5);
//   Page<Board> page = boardRepository.findAll(paging);
  public BoardListResponseDto getBoardPage(Pageable pageable, Long categoryId) {
    String categoryName = categoryRepository.findById(categoryId).orElseThrow().getCategoryName();
    Page<Board> page = boardRepository.findAll(pageable);
    List<BoardResponseDto> list = new ArrayList<>();

    for (Board board : page) {
      list.add(
          BoardResponseDto.builder()
              .boardId(board.getId())
              .memberId(board.getMemberId())
              .boardTitle(board.getBoardTitle())
              .boardContents(board.getBoardContents())
              .build()
      );
    }

    return BoardListResponseDto.builder()
        .categoryId(categoryId)
        .categoryName(categoryName)
        .boardResponseDtoList(list)
        .build();
  }

}
