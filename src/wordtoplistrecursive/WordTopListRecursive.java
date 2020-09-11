/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordtoplistrecursive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author laszlop
 */
public class WordTopListRecursive {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, IOException {
        System.out.println("WordTopList application started.");
        List<URL> urlList = new ArrayList<>();
        urlList.add(new URL("https://justinjackson.ca/words.html"));
        System.out.print("Checked URL-s: ");
        System.out.println(urlList.toString());
        // WordCounter wordCounter = new WordCounter(urlList);
        Map<String, Integer> topList = new HashMap<>();
        // topList = wordCounter.createTopList();
        System.out.println("The most frequent words: " + topList);
    }

}
