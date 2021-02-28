package com.tam.isave.model.transaction;

import com.tam.isave.model.category.Category;
import com.tam.isave.model.category.CategoryTracker;
import com.tam.isave.utils.NumberUtils;
import com.tam.isave.utils.Date;

import java.util.ArrayList;

public class History {

    private static final int HISTORY_MAXIMUM_DAYS = 180; // Entries older than these many days will be deleted.

    private ArrayList<Transaction> historyList;

    public History() {
        this.historyList = new ArrayList<Transaction>();
    }

    // Adds and inserts transaction at correct position based on date.
    public void addTransaction(Transaction transaction) {
        if( (transaction == null) || (historyList.contains(transaction)) ) { return; }
        validateSorting();

        if(historyList.isEmpty()) {
            historyList.add(transaction);
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

    // Adds transactions in bulk and then sorts history list.
    public void addTransactions(ArrayList<Transaction> transactions) {
        if(transactions == null) { return; }
        historyList.addAll(transactions);
        validateSorting();
    }

    // Remove @transaction and also dispose it if flag is true.
    // Flag should be false if @transaction is still being used by other categories.
    public void removeTransaction(Transaction transaction) {
        if( (transaction == null) || !(historyList.contains(transaction)) ) { return; }
        historyList.remove(transaction);
    }

    public void modifyTransaction(Transaction transaction) {
        if( (transaction == null) || !(historyList.contains(transaction)) ) { return; }
        if(isTransactionOrdered(transaction)) { return; }

        // If transaction is not ordered,
        // It needs to be removed and inserted back at the right place.
        removeTransaction(transaction);
        addTransaction(transaction);
    }

    // Checks if transaction is ordered in the history list.
    // true if prevTransaction.dateValue > transaction.dateValue > nextTransaction.dateValue.
    // newTransaction comparison is ignored if transaction is first item;
    // oldTransaction comparison is ignored if transaction is last item.
    private boolean isTransactionOrdered(Transaction transaction) {
        if( (transaction == null) || !(historyList.contains(transaction)) ) { return false; }

        int tranIndex = historyList.indexOf(transaction);
        // Previous item in history list or null if param is the first item.
        Transaction prevTran = (tranIndex > 0) ? historyList.get(tranIndex - 1) : null;
        // Next item in history list or null if param is the last item.
        Transaction nextTran = ( tranIndex < (historyList.size() - 1) ) ? historyList.get(tranIndex + 1) : null;

        int tranValue = transaction.getDate().getValue();
        // If transaction is older than previous item. True if previous item is null.
        boolean olderThanPrev = (prevTran == null) || (prevTran.getDate().getValue() > tranValue);
        // If transaction is newer than next item. True if next item is null.
        boolean newerThanNext = (nextTran == null) || (nextTran.getDate().getValue() < tranValue);

        // Transaction is ordered if it's both older than previous item and newer than next item.
        return olderThanPrev && newerThanNext;
    }

    public void reset() {
        historyList.clear();
    }

    // Returns a clone of historyList.
    public ArrayList<Transaction> cloneHistoryList() {
        return new ArrayList<>(historyList);
    }

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
            if(keyValue < NumberUtils.TRANSACTION_DAYS_LIMIT) { break; }

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
                historyList.remove(targetTransaction);
            } else {
                // Valid transaction found, we can stop iterating.
                break;
            }
        }
    }

    public boolean hasTransaction(Transaction transaction) {
        if(transaction == null) { return false; }
        return historyList.contains(transaction);
    }

    public boolean isEmpty() {
        return historyList.isEmpty();
    }

    // Cleans histories in parameters by removing old transactions.
    public static void cleanHistories(CategoryTracker tracker) {
        if(tracker == null) { return; }

        tracker.getHistory().clean();
        for(Category category : tracker.getCategories()) {
            category.getHistory().clean();
        }
    }

//    public ArrayList<Transaction> getHistoryList() {
//        return historyList;
//    }
}


