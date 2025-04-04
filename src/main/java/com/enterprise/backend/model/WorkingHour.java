package com.enterprise.backend.model;

import com.enterprise.backend.util.validator.workinghour.ValidWorkingHour;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Setter
@Getter
@ValidWorkingHour
public class WorkingHour {
    @NotNull(message = "Thời gian bắt đầu không được để trống.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime start;

    @NotNull(message = "Thời gian kết thúc không được để trống.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime end;
}
