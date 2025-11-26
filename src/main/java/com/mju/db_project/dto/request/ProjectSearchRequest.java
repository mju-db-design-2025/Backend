package com.mju.db_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSearchRequest {

    // 1. 기간필터: "ALL", "THIS_MONTH", "THIS_QUARTER"
    private String period;

    // 2. 상태필터: "ALL", "ING", "DONE"
    private String status;
    
}
