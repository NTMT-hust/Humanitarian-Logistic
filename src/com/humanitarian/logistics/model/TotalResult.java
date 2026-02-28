package com.humanitarian.logistics.model;

import java.util.List;

public class TotalResult {
    StringBuffer buffer;
    List<Integer> numbers;

    public TotalResult(StringBuffer buffer, List<Integer> numbers) {
        this.buffer = buffer;
        this.numbers = numbers;
    }

    public String getString() {
        return this.buffer.toString();
    }

    public List<Integer> getTotalSentiment() {
        return this.numbers;
    }
}