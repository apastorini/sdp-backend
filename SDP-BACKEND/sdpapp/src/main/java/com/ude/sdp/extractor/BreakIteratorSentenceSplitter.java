package com.ude.sdp.extractor;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;


public class BreakIteratorSentenceSplitter  {

	
	//https://www.programcreek.com/java-api-examples/?code=takun2s/smile_1.5.0_java7/smile_1.5.0_java7-master/nlp/src/main/java/smile/nlp/tokenizer/BreakIteratorSentenceSplitter.java#
    /**
     * The working horse for splitting sentences.
     */
    private BreakIterator boundary;

    /**
     * Constructor for the default locale.
     */
    public BreakIteratorSentenceSplitter() {
        boundary = BreakIterator.getSentenceInstance();
    }

    /**
     * Constructor for the given locale.
     */
    public BreakIteratorSentenceSplitter(Locale locale) {
        boundary = BreakIterator.getSentenceInstance(locale);
    }

   
    public String[] split(String text) {
        boundary.setText(text);
        ArrayList<String> sentences = new ArrayList<>();
        int start = boundary.first();
        for (int end = boundary.next();
                end != BreakIterator.DONE;
                start = end, end = boundary.next()) {
            sentences.add(text.substring(start, end).trim());
        }

        String[] array = new String[sentences.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = sentences.get(i);
        }

        return array;
    }
}