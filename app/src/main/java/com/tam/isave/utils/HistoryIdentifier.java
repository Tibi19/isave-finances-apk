package com.tam.isave.utils;

/**
 * Provides data for identifying a collection of transactions that constitute a history.
 */
public class HistoryIdentifier {

    public static final String HISTORY_TYPE_GLOBAL = "GLOBAL_HISTORY";
    public static final String HISTORY_TYPE_CATEGORY = "CATEGORY_HISTORY";
    public static final String HISTORY_TYPE_INTERVAL = "INTERVAL_HISTORY";

    private String historyType;
    private int categoryId;
    private int startDateValue;
    private int endDateValue;

    /**
     * Global History constructor.
     */
    public HistoryIdentifier() {
        this.historyType = HISTORY_TYPE_GLOBAL;
        this.categoryId = -1;
        this.startDateValue = -1;
        this.endDateValue = -1;
    }

    /**
     * Category History constructor.
     * @param categoryId The category id.
     */
    public HistoryIdentifier(int categoryId) {
        this.historyType = HISTORY_TYPE_CATEGORY;
        this.categoryId = categoryId;
        this.startDateValue = -1;
        this.endDateValue = -1;
    }

    /**
     * Interval History constructor
     * @param startDateValue The date value at the beginning of the Interval.
     * @param endDateValue The date value at the end of the Interval.
     */
    public HistoryIdentifier(int startDateValue, int endDateValue){
        this.historyType = HISTORY_TYPE_INTERVAL;
        this.categoryId = -1;
        this.startDateValue = startDateValue;
        this.endDateValue = endDateValue;
    }

    public String getHistoryType() {
        return historyType;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStartDateValue() {
        return startDateValue;
    }

    public void setStartDateValue(int startDateValue) {
        this.startDateValue = startDateValue;
    }

    public int getEndDateValue() {
        return endDateValue;
    }

    public void setEndDateValue(int endDateValue) {
        this.endDateValue = endDateValue;
    }
}
