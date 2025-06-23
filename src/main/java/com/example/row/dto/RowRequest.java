

package com.example.row.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RowRequest {
    @Setter
    @Getter
    @Schema(hidden = true)
    private Long id;

    private String name;
   private String content;
   private String targetDate;
   @Schema(example = "NONE")
    private String recurrence = "NONE";
     private String createdAt;
}
