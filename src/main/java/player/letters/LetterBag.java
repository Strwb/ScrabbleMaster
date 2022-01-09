package player.letters;

import board.board.Board;
import board.board.fields.Field;
import board.words.Word;

import java.util.*;

import static java.util.stream.Collectors.toList;

public record LetterBag(Map<Character, Integer> quantities) {

    public LetterBag deleteRack(Rack rack) {
        LetterBag copy = deepCopy(this);
        rack.getLetters()
                .forEach(copy::decreaseQuantity);
        return copy;
    }

    public LetterBag deleteUsedLetters(Board board, Rack rack1, Rack rack2) {
        LetterBag copy = deepCopy(this);
        copy.deleteWords(board.getWords());
        copy.deleteRack(rack1);
        copy.deleteRack(rack2);
        return copy;
    }

    public LetterBag deleteUsedLetters(Board board, Rack rack) {
        LetterBag copy = deepCopy(this);
        copy.deleteWords(board.getWords());
        copy.deleteRack(rack);
        return copy;
    }

    public LetterBag deleteUsedLetters(Board board) {
        LetterBag copy = deepCopy(this);
        copy.deleteWords(board.getWords());
        return copy;
    }

    public LetterBag deleteUsedLetters(Rack rack) {
        LetterBag copy = deepCopy(this);
        copy.deleteRack(rack);
        return copy;
    }

    public int size() {
        return (int) this.quantities().entrySet().stream()
                .map(Map.Entry::getValue)
                .count();
    }

    public static LetterBag initialBag() {
        Map<Character, Integer> quantities = new HashMap<>();
        return new LetterBag(fillInitialQuantities(quantities));
    }

    void decreaseQuantity(Character letter) {
        int letterQuantity = this.quantities.get(letter);
        this.quantities.put(letter, letterQuantity - 1);
    }

    Character randomLetter() {
        Random random = new Random();
        List<Character> pool = this.characterPool();
        int index = random.nextInt(0, pool.size());
        while (quantities.get(pool.get(index)) <= 0) {
            index = random.nextInt(0, pool.size());
        }
        this.decreaseQuantity(pool.get(index));
        return pool.get(index);
    }

    void deleteWords(List<Word> words) {
        words.stream()
                .map(Word::getLetters)
                .flatMap(Collection::stream)
                .map(Field::getValue)
                .forEach(this::decreaseQuantity);
    }

    private List<Character> characterPool() {
        return this.quantities().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private static LetterBag deepCopy(LetterBag bag) {
        Map<Character, Integer> quantities = new HashMap<>(bag.quantities());
        return new LetterBag(quantities);
    }

    private static Map<Character, Integer> fillInitialQuantities(Map<Character, Integer> quantities) {

        quantities.put('a', 9);
        quantities.put('e', 7);
        quantities.put('i', 8);
        quantities.put('n', 5);
        quantities.put('o', 6);
        quantities.put('r', 4);
        quantities.put('s', 4);
        quantities.put('w', 4);
        quantities.put('z', 5);
        quantities.put('c', 3);
        quantities.put('d', 3);
        quantities.put('k', 3);
        quantities.put('l', 3);
        quantities.put('m', 3);
        quantities.put('p', 3);
        quantities.put('t', 3);
        quantities.put('y', 4);
        quantities.put('b', 2);
        quantities.put('g', 2);
        quantities.put('h', 2);
        quantities.put('j', 2);
        quantities.put('ł', 2);
        quantities.put('u', 2);
        quantities.put('ą', 1);
        quantities.put('ę', 1);
        quantities.put('f', 1);
        quantities.put('ó', 1);
        quantities.put('ś', 1);
        quantities.put('ż', 1);
        quantities.put('ć', 1);
        quantities.put('ń', 1);
        quantities.put('ź', 1);

        return quantities;
    }
}
