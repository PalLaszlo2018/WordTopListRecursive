/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

TASK DESCRIPTION:

1. Adott egy url lista (Egyelőre konstansként deklaráljuk). Listázzuk ki
az url-eken található html tartalmakban a 10 (vagy X) leggyakrabban
előfoduló szót, ami nem html tag vagy attribútum.
Legyen továbbá egy skipword halmaz, amit nem tekintünk szónak, ahova
kötőszavakat írjuk fel. HashMap-et és HashSet-et kell használni, hogy
elég gyors legyen. (ha ismeretlen a HashMap logikája, olvass róla itt:
https://beginnersbook.com/2013/12/hashmap-in-java-with-example/ -
továbbá google segítségével derítsd ki, mitől jó egy kulcs a HashMap esetén)
Legyen egy skipTags halmaz is, ami olyan tagokat sorol fel, amik közé
írt szavakat nem vesszük figyelembe.
(pl. <tag1> dsd dsfg ds<tag2><tag2>  dsfgdsf</tag2></tag2> dsfsd<tag1>
a skipTag-ek közé kezdetben a style, head szavakat vesszük fel.

A tag-eket rekurziv módonon dolgozza fel egy eatTag() method (paramétere lehet)
 */
package wordtoplist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author laszlop
 */
public class WordCounter {

    private final List<URL> urlList;
    private final Set<String> skipWords;
    private final Set<String> skipTags;
    private final Set<String> tags = new HashSet<>();
    private final static int LENGTH_OF_TOPLIST = 10;
    private final Map<String, Integer> freq = new HashMap<>();

    public WordCounter(List<URL> urlList) {
        this.urlList = urlList;
        this.skipWords = new HashSet<>(Arrays.asList("a", "and", "as", "in", "it", "of", "on", "not", "the", "to", "p", "with",
             "href", "hreflang", "http", "https", "html"));
        this.skipTags = new HashSet<>(Arrays.asList("head", "style")); // texts between these texts are ignored
    }

    public Map<String, Integer> createTopList() throws IOException {
        Map<String, Integer> topList = new HashMap<>();
        for (int i = 0; i < urlList.size(); i++) {
            URL url = urlList.get(i);
            StringBuilder text = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            int value;
            while ((value = reader.read()) != -1) {
                char character = (char) value;
                text.append(character);
                if (character == '>' && findClosingTag(text) != null) {
                    eatTag(text, findClosingTag(text));
                }
            }
            processValidSubtext(text, 0, text.length());
        }
        topList = buildTopList(freq, LENGTH_OF_TOPLIST);
        return topList;
    }

    private String findClosingTag(StringBuilder text) {
        int index = text.length() - 1;
        String tag;
        while (index >= 0 && text.charAt(index) != '<') {
            index--;
        }
        if (index < 0) {
            return null;
        }
        if (text.charAt(index + 1) == '/') {
            tag = text.substring(index + 2, text.length() - 1);
            tags.add(tag);
            return tag;
        }
        return null;
    }

    private void eatTag(StringBuilder text, String tag) {
        String openingTag = "<" + tag + ">";
        int index = text.lastIndexOf(openingTag);
        if (index < 0) { // no proper opening tag found
            return;
        }
        if (skipTags.contains(tag)) { 
            text.delete(index, text.length()); // ignore text between skipTags for processing
            return;
        }
        processValidSubtext(text, index + openingTag.length(), text.length() - tag.length() - 3); //3 for </ and >
        text.delete(index, text.length());
        return;
    }

    private void processValidSubtext(StringBuilder text, int start, int end) {
        String[] words = text.substring(start, end).split("[-–., :<>;=/\"\\s]+");
        for (int j = 0; j < words.length; j++) {
            if (!words[j].isEmpty()) freq.put(words[j], freq.getOrDefault(words[j], 0) + 1);
        }
    }

    private Map<String, Integer> buildTopList(Map<String, Integer> freq, int x) {
        Map<String, Integer> topList = new HashMap<>();
        for (int i = 0; i < x; i++) {
            int max = 0;
            String maxKey = null;
            for (Map.Entry<String, Integer> entry : freq.entrySet()) {
                int occured = entry.getValue();
                if (occured > max && !skipWords.contains(entry.getKey())) {
                    max = occured;
                    maxKey = entry.getKey();
                }
            }
            if (maxKey != null) {
                topList.put(maxKey, max);
                freq.remove(maxKey);
            }
        }
        return topList;
    }
}
