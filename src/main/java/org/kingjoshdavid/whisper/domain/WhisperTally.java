package org.kingjoshdavid.whisper.domain;

import org.kingjoshdavid.whisper.WhisperView;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

public class WhisperTally {
    public static class PlayerPair implements Comparable<PlayerPair>{
        public final String playerA;
        public final String playerB;

        PlayerPair(String playerA, String playerB) {
            this.playerA = playerA;
            this.playerB = playerB;
        }

        @Override
        public int compareTo(PlayerPair o) {
            int i = playerA.compareToIgnoreCase(o.playerA);
            if(i == 0) {
                i = playerB.compareTo(o.playerB);
            }
            if(i == 0 ) {
                i = playerA.compareTo(o.playerA);
            }
            if(i == 0) {
                i = playerB.compareTo(o.playerB);
            }
            return i;
        }

        @Override
        public String toString() {
            return playerA + " - "+ playerB;
        }
    }
    public static Map<PlayerPair, Integer> computeWhisperMap(String whisperLog) {
        Map<PlayerPair, Integer> playerPlayerWeights = new TreeMap<>();
        for (String whisper : whisperLog.trim().split("\n")) {
            Matcher matcher = WhisperView.WHISPER_PATTERN.matcher(whisper);
            if (!matcher.matches()) {
                System.out.println("Skipping input " + whisper);
                continue;
            }
            String a = matcher.group(1);
            String b = matcher.group(2);
            if (a.compareToIgnoreCase(b) > 0) {
                String c = a;
                a = b;
                b = c;
            }

            int count = Integer.parseInt(matcher.group(3));
//            System.out.println(a + ", " + b + ": " + count);

            PlayerPair pair = new PlayerPair(a, b);
            playerPlayerWeights.merge(pair, count, Integer::sum);
        }
        return playerPlayerWeights;
    }
}
