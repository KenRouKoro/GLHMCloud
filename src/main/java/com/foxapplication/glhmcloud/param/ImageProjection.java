package com.foxapplication.glhmcloud.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageProjection {
    private UUID id;
    private String name;
}
