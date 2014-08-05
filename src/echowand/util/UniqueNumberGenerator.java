package echowand.util;

import java.util.HashSet;

public class UniqueNumberGenerator {
    
    private static long DEFAULT_MIN_NUMBER = 0;
    private static long DEFAULT_MAX_NUMBER = Integer.MAX_VALUE;

    private long nextNumber;
    private long minNumber;
    private long maxNumber;
    private HashSet<Long> usedNumberSet = new HashSet<Long>();
    
    public UniqueNumberGenerator() {
        this(0, DEFAULT_MIN_NUMBER, DEFAULT_MAX_NUMBER);
    }
    
    public UniqueNumberGenerator(long initialValue) {
        this(initialValue, DEFAULT_MIN_NUMBER, DEFAULT_MAX_NUMBER);
    }
    
    public UniqueNumberGenerator(long minValue, long maxValue) {
        this(minValue, minValue, maxValue);
    }
    
    public UniqueNumberGenerator(long initialValue, long minValue, long maxValue) {
        this.nextNumber = initialValue;
        this.minNumber = minValue;
        this.maxNumber = maxValue;
    }

    public synchronized boolean isAllocated(long id) {
        return usedNumberSet.contains(id);
    }

    public synchronized boolean release(long id) {
        return usedNumberSet.remove(id);
    }
    
    private Long findNextNumber() throws RunOutOfNumbersException {
        long newNumber;

        for (newNumber = nextNumber; newNumber <= maxNumber; newNumber++) {
            if (!isAllocated(newNumber)) {
                return newNumber;
            }
        }

        for (newNumber = minNumber; newNumber < nextNumber; newNumber++) {
            if (!isAllocated(newNumber)) {
                return newNumber;
            }
        }

        throw new RunOutOfNumbersException("Unique numbers have been exhausted");
    }
    
    private void updateNextNumber() throws RunOutOfNumbersException {
        nextNumber = findNextNumber();
    }
    
    private void incrementNextNumber() {
        if (nextNumber == maxNumber) {
            nextNumber = minNumber;
        } else {
            nextNumber += 1;
        }
    }
    
    public synchronized long allocate() throws RunOutOfNumbersException {
        if (isAllocated(nextNumber)) {
            updateNextNumber();
        }
        
        long newNumber = nextNumber;
        usedNumberSet.add(newNumber);
        
        incrementNextNumber();
        
        return newNumber;
    }
}