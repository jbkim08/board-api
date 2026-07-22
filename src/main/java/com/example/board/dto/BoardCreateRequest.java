package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

//게시글 등록용 DTO (유효성 검사)
@Getter
@NoArgsConstructor
public class BoardCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 넘을 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 4, message = "내용은 4자 이상 적어야 합니다.")
    private String content;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 50, message = "작성자명은 50자를 넘을 수 없습니다.")
    private String writer;
}