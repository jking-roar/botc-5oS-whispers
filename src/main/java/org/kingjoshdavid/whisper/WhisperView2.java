package org.kingjoshdavid.whisper;

import java.awt.*;

public class WhisperView2 {
    public static void main(String[] args) {
        // a second game
        String townsquare = "Alice\n" +
                "Buffy\n" +
                "Chris\n" +
                "Derrick\n" +
                "edward wins\n" +
                "faith\n" +
                "Greg\n" +
                "Hill \n";

        String dayOne = "Alice - Buffy: 22\n" +
                "Buffy - faith: 17\n" +
                "Derrick - Greg: 14\n" +
                "Buffy - Derrick: 13\n" +
                "Alice - Greg: 11\n" +
                "Chris - Derrick: 8\n" +
                "Alice - Derrick: 7\n" +
                "Buffy - Chris: 7\n" +
                "Buffy - edward wins: 7\n" +
                "Derrick - edward wins: 7\n" +
                "Derrick - Hill: 7\n" +
                "Alice - edward wins: 6\n" +
                "Buffy - Greg: 6\n" +
                "Chris - Greg: 6\n" +
                "edward wins - Greg: 6\n" +
                "Chris - edward wins: 4\n" +
                "edward wins - Hill: 4\n" +
                "Derrick - faith: 3\n" +
                "Greg - Hill: 3\n" +
                "Alice - Chris: 2\n" +
                "Buffy - Hill: 2\n" +
                "Chris - faith: 1\n" +
                "edward wins - faith: 1\n";
        String dayOneTwo = "Chris - Hill: 5\n" +
                "Derrick - Hill: 1\n";

        String dayTwo = "Buffy - Chris: 31\n" +
                "faith - Greg: 27\n" +
                "Buffy - Greg: 21\n" +
                "Alice - Buffy: 15\n" +
                "Buffy - Derrick: 8\n" +
                "Buffy - faith: 7\n" +
                "Buffy - Hill: 5\n" +
                "edward wins - faith: 5\n" +
                "Chris - Derrick: 4\n" +
                "Chris - Greg: 4\n" +
                "Alice - Derrick: 3\n" +
                "Buffy - edward wins: 3\n" +
                "Alice - Greg: 1\n";
        String dayTwoTwo = "Chris - edward wins: 18\n" +
                "Derrick - Greg: 16\n" +
                "Buffy - Derrick: 10\n" +
                "Derrick - edward wins: 8\n" +
                "Buffy - Greg: 7\n" +
                "Buffy - edward wins: 5\n" +
                "Buffy - Hill: 2\n" +
                "Derrick - faith: 1\n" +
                "Derrick - Alice: 1\n" +
                "edward wins - faith: 1\n";
        String dayTwoThree = "Alice - Derrick: 4\n" +
                "Alice - Greg: 4\n" +
                "Derrick - Greg: 3\n" +
                "Derrick - faith: 2\n" +
                "Alice - Buffy: 1\n" +
                "Buffy - Derrick: 1\n" +
                "edward wins - faith: 1\n";

        String dayThree = "Chris - Derrick: 24\n" +
                "Buffy - Derrick: 22\n" +
                "Buffy - Chris: 18\n" +
                "Derrick - Greg: 15\n" +
                "Alice - Greg: 10\n" +
                "Derrick - edward wins: 10\n" +
                "edward wins - Greg: 9\n" +
                "Alice - Buffy: 7\n" +
                "Derrick - Hill: 7\n" +
                "Chris - Hill: 6\n" +
                "Alice - Derrick: 4\n" +
                "edward wins - faith: 4\n" +
                "Alice - edward wins: 2\n" +
                "Buffy - edward wins: 2\n" +
                "Buffy - Hill: 2\n" +
                "Chris - edward wins: 2\n" +
                "Chris - Greg: 2\n" +
                "Buffy - faith: 1\n" +
                "Derrick - faith: 1\n";
        String dayThreeTwo = "Derrick - faith: 14\n" +
                "edward wins - faith: 4\n" +
                "Derrick - edward wins: 3\n" +
                "Chris - faith: 1\n";

        String dayFour = "Alice - faith: 33\n" +
                "Buffy - Derrick: 17\n" +
                "Buffy - Greg: 11\n" +
                "Buffy - Hill: 8\n" +
                "Derrick - edward wins: 7\n" +
                "Buffy - Chris: 6\n" +
                "edward wins - Greg: 5\n" +
                "Buffy - edward wins: 4\n" +
                "edward wins - Hill: 4\n" +
                "Alice - Buffy: 3\n" +
                "Alice - edward wins: 3\n" +
                "Buffy - faith: 3\n" +
                "Derrick - Hill: 3\n" +
                "Alice - Greg: 2\n" +
                "faith - Greg: 2\n" +
                "Alice - Hill: 1\n" +
                "Chris - Derrick: 1\n" +
                "Chris - faith: 1\n";

        new WhisperView(townsquare, dayOne, "dayOne");
        new WhisperView(townsquare, dayOneTwo, "dayOneTwo");

        new WhisperView(townsquare, dayTwo, "dayTwo");
        new WhisperView(townsquare, dayTwoTwo, "dayTwoTwo");
        new WhisperView(townsquare, dayTwoThree, "dayTwoThree");

        new WhisperView(townsquare, dayThree, "dayThree");
        new WhisperView(townsquare, dayThreeTwo, "dayThreeTwo");

        new WhisperView(townsquare, dayFour, "dayFour");

    }

    private static Color interpolateColor(Color a, Color b, double d) {
        int re = (int) (a.getRed() + d * (b.getRed() - a.getRed()));
        int gr = (int) (a.getGreen() + d * (b.getGreen() - a.getGreen()));
        int bl = (int) (a.getBlue() + d * (b.getBlue() - a.getBlue()));
        int al = (int) (a.getAlpha() + d * (b.getAlpha() - a.getAlpha()));

        return new Color(re, gr, bl, al);

    }
}
