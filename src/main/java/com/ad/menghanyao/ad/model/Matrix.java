package com.ad.menghanyao.ad.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Matrix implements Comparable<Matrix> {
    private Long row;
    private Long column;
    private Double operation;

    @Override
    public int compareTo(@NotNull Matrix matrix) {
        return (int) (this.operation - matrix.operation);
    }

    public double getOperation(Long row, Long column) {
        if (this.row == row && this.column == column)
            return this.operation;
        else return 0.0;
    }
}
