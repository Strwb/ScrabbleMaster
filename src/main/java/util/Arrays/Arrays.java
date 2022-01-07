package util.Arrays;

import board.Field;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Arrays {

    public Field[][] deepCopyOf(Field[][] fields) {
        Field[][] copy = new Field[fields.length][];
        int copyIndex = 0;
        for (Field[] row : fields) {
            Field[] copyRow = new Field[row.length];
            for (int i = 0; i < row.length; i++) {
                copyRow[i] = row[i].clone();
            }
            copy[copyIndex] = row;
            copyIndex++;
        }
        return copy;
    }
}
