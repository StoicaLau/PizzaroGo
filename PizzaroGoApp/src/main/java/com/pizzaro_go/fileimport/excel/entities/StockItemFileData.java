package com.pizzaro_go.fileimport.excel.entities;

import com.poiji.annotation.ExcelCellName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockItemFileData {
    @ExcelCellName("Name")
    private String name;

    @ExcelCellName("Category")
    private String category;

    @ExcelCellName("Quantity")
    private Double quantity;

    @ExcelCellName("Unit")
    private String unit;
}
