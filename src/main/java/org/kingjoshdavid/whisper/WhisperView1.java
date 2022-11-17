package org.kingjoshdavid.whisper;

import java.util.HashMap;
import java.util.Map;

public class WhisperView1 {
    public static void main(String[] args) {
        String townsquare = "aardvark\n" +
                "bat\n" +
                "cheetah\n" +
                "dogfish\n" +
                "elephant\n" +
                "ferret\n" +
                "¯\\_(ツ)_/¯ giraffe\n" +
                "hunter\n" +
                "iguana\n";
        String dayOne = "bat - elephant: 32\n" +
                "bat - ¯\\_(ツ)_/¯ giraffe: 22\n" +
                "bat - iguana: 20\n" +
                "bat - hunter: 18\n" +
                "elephant - iguana: 18\n" +
                "ferret - iguana: 16\n" +
                "bat - ferret: 15\n" +
                "ferret - hunter: 15\n" +
                "aardvark - ferret: 14\n" +
                "elephant - ferret: 13\n" +
                "elephant - hunter: 13\n" +
                "aardvark - hunter: 12\n" +
                "cheetah - elephant: 12\n" +
                "¯\\_(ツ)_/¯ giraffe - iguana: 12\n" +
                "aardvark - elephant: 11\n" +
                "bat - dogfish: 11\n" +
                "¯\\_(ツ)_/¯ giraffe - hunter: 10\n" +
                "aardvark - iguana: 9\n" +
                "dogfish - hunter: 8\n" +
                "hunter - iguana: 7\n" +
                "cheetah - hunter: 4\n" +
                "cheetah - ferret: 3\n" +
                "dogfish - ferret: 3\n" +
                "ferret - ¯\\_(ツ)_/¯ giraffe: 3\n" +
                "aardvark - bat: 2\n" +
                "bat - cheetah: 2\n" +
                "cheetah - iguana: 1\n" +
                "dogfish - elephant: 1\n" +
                "dogfish - iguana: 1\n" +
                "ferret - storyteller: 1\n" +
                "hunter - storyteller: 1\n" +
                "iguana - storyteller: 1\n";
        String dayOneTwo = "ferret - hunter: 22\n" +
                "bat - ¯\\_(ツ)_/¯ giraffe: 14\n" +
                "bat - ferret: 13\n" +
                "bat - iguana: 10\n" +
                "¯\\_(ツ)_/¯ giraffe - elephant: 7\n" +
                "ferret - iguana: 5\n" +
                "iguana - dogfish: 4\n" +
                "iguana - hunter: 4\n" +
                "ferret - dogfish: 2\n" +
                "bat - dogfish: 2\n" +
                "hunter - elephant: 2\n" +
                "bat - hunter: 2\n" +
                "bat - aardvark: 1\n" +
                "bat - cheetah: 1\n";

        String dayOneThree = "bat - aardvark: 2\n" +
                "bat - hunter: 1\n" +
                "hunter - ¯\\_(ツ)_/¯ giraffe: 1\n" +
                "hunter - elephant: 1\n";

        String dayTwo = "dogfish - hunter: 15\n" +
                "bat - iguana: 12\n" +
                "dogfish - ferret: 11\n" +
                "ferret - iguana: 11\n" +
                "dogfish - iguana: 10\n" +
                "¯\\_(ツ)_/¯ giraffe - hunter: 10\n" +
                "bat - ¯\\_(ツ)_/¯ giraffe: 9\n" +
                "elephant - iguana: 8\n" +
                "bat - dogfish: 6\n" +
                "bat - hunter: 6\n" +
                "elephant - hunter: 6\n" +
                "ferret - hunter: 6\n" +
                "hunter - iguana: 5\n" +
                "cheetah - hunter: 4\n" +
                "aardvark - ferret: 3\n" +
                "cheetah - dogfish: 3\n" +
                "cheetah - ¯\\_(ツ)_/¯ giraffe: 3\n" +
                "cheetah - iguana: 3\n" +
                "ferret - ¯\\_(ツ)_/¯ giraffe: 3\n" +
                "cheetah - ferret: 2\n" +
                "aardvark - bat: 1\n" +
                "bat - elephant: 1\n" +
                "dogfish - storyteller: 1\n" +
                "ferret - storyteller: 1\n" +
                "hunter - storyteller: 1\n" +
                "iguana - storyteller: 1\n";

        String dayThree = "bat - hunter: 42\n" +
                "bat - ¯\\_(ツ)_/¯ giraffe: 20\n" +
                "¯\\_(ツ)_/¯ giraffe - hunter: 15\n" +
                "bat - elephant: 12\n" +
                "hunter - iguana: 12\n" +
                "cheetah - ¯\\_(ツ)_/¯ giraffe: 8\n" +
                "bat - cheetah: 6\n" +
                "bat - dogfish: 6\n" +
                "elephant - ¯\\_(ツ)_/¯ giraffe: 6\n" +
                "dogfish - iguana: 5\n" +
                "elephant - hunter: 4\n" +
                "ferret - iguana: 4\n" +
                "bat - ferret: 3\n" +
                "dogfish - ferret: 2\n" +
                "dogfish - hunter: 2\n" +
                "bat - iguana: 1\n" +
                "cheetah - dogfish: 1\n" +
                "cheetah - iguana: 1\n" +
                "ferret - hunter: 1\n" +
                "iguana - storyteller: 1\n";


        Map<String, String> namePreferences = new HashMap<>();
        namePreferences.put("¯\\_(ツ)_/¯ giraffe", "giraffe");

        new WhisperView(townsquare, dayOne, "dayOne", namePreferences);
        new WhisperView(townsquare, dayOneTwo, "dayOneTwo", namePreferences);
        new WhisperView(townsquare, dayOneThree, "dayOneThree", namePreferences);

        new WhisperView(townsquare, dayTwo, "dayTwo", namePreferences);
        new WhisperView(townsquare, dayThree, "dayThree", namePreferences);
    }
}
