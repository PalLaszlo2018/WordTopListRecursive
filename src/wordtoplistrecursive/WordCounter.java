/*


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
package wordtoplistrecursive;

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
    private final Set<Character> separators;
    private final static int LENGTH_OF_TOPLIST = 10;
    private final Map<String, Integer> freq = new HashMap<>();

    public WordCounter(List<URL> urlList) {
        this.urlList = urlList;
        this.skipWords = new HashSet<>(Arrays.asList("an", "and", "as", "by", "if", "in", "is", "it", "of", "on", "not", "that",
                "the", "to", "with"));
        this.skipTags = new HashSet<>(Arrays.asList("head", "style")); // texts between these tags are ignored
        this.separators = new HashSet<>(Arrays.asList(' ','*', '<', '.', ':', '?', '!', ';', '-', '–', '=', '{', '}'));
    }

    public Map<String, Integer> createTopList() throws IOException {
        for (int i = 0; i < urlList.size(); i++) {
            URL url = urlList.get(i);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder nextTag = new StringBuilder();
            int value;
            while ((value = reader.read()) != -1) {
                char character = (char) value;
                if (character == '<') {
                    while ((value = reader.read()) != -1) {
                        char tagChar = (char) value;
                        if (tagChar == '>') {
                            break;
                        }
                        nextTag.append(tagChar);
                    }
                    eatTag(nextTag.toString(), reader); //assumption: skipTags can not be the first ones on a webpage
                }
            }
        }
        Map <String, Integer> topList = buildTopList(freq, LENGTH_OF_TOPLIST);
        return topList;
    }

    private void eatTag(String tag, BufferedReader reader) throws IOException {
        int value;
        StringBuilder word = new StringBuilder();
        while ((value = reader.read()) != -1) {
            char character = (char) value;
            if (character == '<') {
                int tagValue;
                if (word.length() > 1 && !skipWords.contains(word.toString()) && !skipTags.contains(tag)) {
                    freq.put(word.toString().toLowerCase(), freq.getOrDefault(word.toString().toLowerCase(), 0) + 1);
                }
                StringBuilder nextTag = new StringBuilder();
                while ((tagValue = reader.read()) != -1) {
                    char tagChar = (char) tagValue;
                    if (tagChar == '>') {
                        break;
                    }
                    nextTag.append(tagChar);
                }
                String nextTagString = nextTag.toString();
                if (('/' + tag).equals(nextTagString)) {
                    return;
                }
                if (!skipTags.contains(tag) && !nextTagString.startsWith("/")) {
                    eatTag(nextTagString, reader);
                }
            }
            if (separators.contains(character) || Character.isWhitespace(character)) {
                if (word.length() > 1 && !skipWords.contains(word.toString()) && !skipTags.contains(tag)) {
                    freq.put(word.toString().toLowerCase(), freq.getOrDefault(word.toString().toLowerCase(), 0) + 1);
                }
                word.setLength(0);
                continue;
            }
            word.append(character);
        }
    }

    private Map<String, Integer> buildTopList(Map<String, Integer> freq, int topListLength) {
        Map<String, Integer> topList = new HashMap<>();
        for (int i = 0; i < topListLength; i++) {
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
