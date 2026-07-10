package com.hrm.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for TaxRate table
 * @author admin
 */
public class TaxRateDAO {
    
    /**
     * Calculate tax based on taxable income
     * Formula: (taxable_income * Rate / 100) - Deduction
     * @param taxableIncome Taxable income amount
     * @return Calculated tax amount
     */
    public BigDecimal calculateTax(BigDecimal taxableIncome) {
        if (taxableIncome == null || taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        String sql = """
            SELECT Rate, Deduction 
            FROM TaxRate 
            WHERE ? >= IncomeMin AND ? <= IncomeMax
            LIMIT 1
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, taxableIncome);
            ps.setBigDecimal(2, taxableIncome);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal rate = rs.getBigDecimal("Rate");
                    BigDecimal deduction = rs.getBigDecimal("Deduction");
                    
                    // Tax = (taxable_income * Rate / 100) - Deduction
                    BigDecimal tax = taxableIncome.multiply(rate)
                            .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP)
                            .subtract(deduction);
                    
                    // Tax cannot be negative
                    if (tax.compareTo(BigDecimal.ZERO) < 0) {
                        return BigDecimal.ZERO;
                    }
                    return tax;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get tax bracket information for a given taxable income
     */
    public TaxBracket getTaxBracket(BigDecimal taxableIncome) {
        if (taxableIncome == null || taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        
        String sql = """
            SELECT BracketID, IncomeMin, IncomeMax, Rate, Deduction 
            FROM TaxRate 
            WHERE ? >= IncomeMin AND ? <= IncomeMax
            LIMIT 1
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, taxableIncome);
            ps.setBigDecimal(2, taxableIncome);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaxBracket(
                        rs.getInt("BracketID"),
                        rs.getBigDecimal("IncomeMin"),
                        rs.getBigDecimal("IncomeMax"),
                        rs.getBigDecimal("Rate"),
                        rs.getBigDecimal("Deduction")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Inner class to represent tax bracket
     */
    public static class TaxBracket {
        private int bracketId;
        private BigDecimal incomeMin;
        private BigDecimal incomeMax;
        private BigDecimal rate;
        private BigDecimal deduction;
        
        public TaxBracket(int bracketId, BigDecimal incomeMin, BigDecimal incomeMax, 
                         BigDecimal rate, BigDecimal deduction) {
            this.bracketId = bracketId;
            this.incomeMin = incomeMin;
            this.incomeMax = incomeMax;
            this.rate = rate;
            this.deduction = deduction;
        }
        
        // Getters
        public int getBracketId() { return bracketId; }
        public BigDecimal getIncomeMin() { return incomeMin; }
        public BigDecimal getIncomeMax() { return incomeMax; }
        public BigDecimal getRate() { return rate; }
        public BigDecimal getDeduction() { return deduction; }
    }
}

