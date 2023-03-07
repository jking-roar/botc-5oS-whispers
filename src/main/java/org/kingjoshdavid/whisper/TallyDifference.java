package org.kingjoshdavid.whisper;

import org.kingjoshdavid.whisper.domain.WhisperTally;

import java.util.*;

public class TallyDifference {
    public static void main(String[] args) {
        String firstTally = "alice - bob: 12\n" +
                "charles - bob: 5\n" +
                "dylan - bob: 3\n" +
                "dylan - edward: 2\n" +
                "edward - bob: 1";

        String secondTally = "ingrid - alice: 28\n" +
                "bob - alice: 12\n" +
                "bob - frank: 11\n" +
                "charles - bob: 5\n" +
                "dylan - alice: 5\n" +
                "frank - jeff (he\\him): 5\n" +
                "dylan - bob: 3\n" +
                "dylan - ingrid: 3\n" +
                "ingrid - edward: 3\n" +
                "frank - alice: 3\n" +
                "frank - edward: 3\n" +
                "frank - jack: 3\n" +
                "dylan - frank: 2\n" +
                "dylan - edward: 2\n" +
                "ingrid - frank: 2\n" +
                "kris - frank: 1\n" +
                "bob - edward: 1\n";


        System.out.println(whisperDiff(firstTally, secondTally));
    }

    public static String whisperDiff(String firstTally, String secondTally) {
        return diffAsString(doCounts(firstTally, secondTally));
    }


    public static Map<WhisperTally.PlayerPair, Integer> doCounts(String firstTally, String secondTally) {
        Map<WhisperTally.PlayerPair, Integer> w1 = WhisperTally.computeWhisperMap(firstTally);
        Map<WhisperTally.PlayerPair, Integer> w2 = WhisperTally.computeWhisperMap(secondTally);

        Map<WhisperTally.PlayerPair, Integer> diff = new HashMap<>();

        for (WhisperTally.PlayerPair playerPair : w2.keySet()) {
            Integer old = w1.get(playerPair);
            Integer now = w2.get(playerPair);
            if (old == null) {
                old = 0;
            }
            Integer d = now - old;
            if (d > 0) diff.put(playerPair, d);
        }

        return diff;
    }

    public static String diffAsString(Map<WhisperTally.PlayerPair, Integer> diff) {
        ArrayList<Map.Entry<WhisperTally.PlayerPair, Integer>> entries = new ArrayList<>(diff.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<WhisperTally.PlayerPair, Integer>>() {
            @Override
            public int compare(Map.Entry<WhisperTally.PlayerPair, Integer> o1, Map.Entry<WhisperTally.PlayerPair, Integer> o2) {
                return Integer.compare(o2.getValue(), o1.getValue());
            }
        });

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<WhisperTally.PlayerPair, Integer> entry : entries) {
            String out = entry.getKey() + ": " + entry.getValue();
            sb.append(out);
            sb.append('\n');
        }
        String asString = sb.toString();
        return asString;
    }

}
