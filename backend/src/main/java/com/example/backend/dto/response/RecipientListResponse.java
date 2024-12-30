package com.example.backend.dto.response;

import com.example.backend.model.Recipient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipientListResponse {

    private List<Recipient> recipients;

}
