package com.pizzaro_go.fileimport.excel.entities;

import com.poiji.annotation.ExcelCellName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuProductFileData {
    @ExcelCellName("Name")
    private String name;

    @ExcelCellName("Category")
    private String category;

    @ExcelCellName("Price")
    private Double price;

    @ExcelCellName("Description")
    private String description;

    @ExcelCellName("Image URL")
    private String imageURL;
}
