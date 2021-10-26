package com.tam.isave.model.transaction;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryTracker;
import com.tam.isave.utils.Constants;
import com.tam.isave.utils.Date;

import java.util.ArrayList;
import java.util.List;

public class History {

    private static final int HISTORY_MAXIMUM_DAYS = 180;

    private List<Transaction> historyList;

    public History() {
        this.historyList = new ArrayList<Transaction>();
    }

    public History(List<Transaction> transactions) {
        this.historyList = transactions;
    }

    // Adds and inserts transaction at correct position based on date.
    public void addTransaction(Transaction transaction) {
        if( (transaction == null) || hasTransaction(transaction) ) { return; }
        validateSorting();

        if(historyList.isEmpty()) {
            historyList.add(transaction);
            return;
        }

        int i = 0; // iterator starts at 0 as the new element is most likely newer than the rest.
        int transValue = transaction.getDate().getValue(); // transaction's date as int value.

        // Compare transaction with each element of history's list.
        // Stop when found one that is newer or on the same day.
        // Or when iterator is equal to the size of historyList.
        while( (i < historyList.size()) && (historyList.get(i).getDate().getValue() > transValue) ) {
            i++;
        }
        // Place transaction before the found element.
        historyList.add(i, transaction);
    }

    public void removeTransaction(Transaction transaction) {
        if( (transaction == null) ) { return; }
        historyList.removeIf(element -> element.getId() == transaction.getId());
    }

    public void reset() {
        historyList.clear();
    }

    public List<Transaction> getHistoryList() { return historyList; }

    // Checks if history is sorted and sorts it if not.
    private void validateSorting() {
        if(!isSorted()) {
            sort();
        }
    }

    // True if this.historyList is sorted.
    private boolean isSorted() {
        int histLength = historyList.size();
        if(histLength <= 1) { return true; }

        for(int i = 0; i < histLength - 1; i++) {
            Date newDate = historyList.get(i).getDate();
            Date oldDate = historyList.get(i + 1).getDate();
            if(!newDate.isNewerThan(oldDate)) {
                return false;
            }
        }

        return true;
    }

    // Sort history by date, closest date first.
    // Insertion sort as history should already be partly sorted.
    private void sort() {
        for(int j = 1; j < historyList.size(); j++) {

            // Picking up the key(Card)
            Transaction key = historyList.get(j);
            int i = j - 1;
            int keyValue = key.getDate().getValue();

            // Transactions older than the days limit for new transactions should have already been sorted when they were added.
            // At this point we can safely stop iterating.
            if(keyValue < Constants.TRANSACTION_DAYS_LIMIT) { break; }

            while(i >= 0 &&  historyList.get(i).getDate().getValue() < keyValue) {
                historyList.set(i + 1, historyList.get(i));
                i--;
            }
            // Placing the key(Card) at its correct position
            historyList.set(i + 1, key);
        }
    }

    /**
     * Removes entries from more than 6 months ago.
     */
    public void clean() {
        // First make sure history is sorted.
        validateSorting();

        // Inverse for loop through history list as only oldest transactions should be considered.
        for(int i = historyList.size() - 1; i >= 0; i--) {
            Transaction targetTransaction = historyList.get(i);
            if(targetTransaction.getDate().differenceInDays(Date.today()) >= HISTORY_MAXIMUM_DAYS) {
                removeTransaction(targetTransaction);
            } else {
                // Valid transaction found, we can stop iterating.
                break;
            }
        }
    }

    public boolean hasTransaction(Transaction transaction) {
        if(transaction == null) { return false; }
        for(Transaction element : historyList) {
            if(element.getId() == transaction.getId()) { return true; }
        }
        return false;
    }

    public boolean isEmpty() {
        return historyList.isEmpty();
    }
    public int size() { return historyList.size(); }

    // Cleans histories in parameters by removing old transactions.
    public static void cleanHistories(CategoryTracker tracker) {
        if(tracker == null) { return; }

        tracker.getHistory().clean();
        for(Category category : tracker.getCategories()) {
            category.getHistory().clean();
        }
    }

    public History getCategoryHistory(int categoryId) {
        List<Transaction> transactions = new ArrayList<>();

        for(Transaction transaction : historyList) {
            if(transaction.getParentId() == categoryId) {
                transactions.add(transaction);
            }
        }

        return new History(transactions);
    }
}


