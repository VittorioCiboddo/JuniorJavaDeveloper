package com.example.quadrangolare_calcio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiniMatchDTO {

    private MiniTeamDTO home;
    private MiniTeamDTO away;

}

