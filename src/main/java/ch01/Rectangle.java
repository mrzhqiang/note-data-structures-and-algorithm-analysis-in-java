package ch01;

import java.util.Comparator;
import java.util.StringJoiner;

public class Rectangle {

    private final int width;
    private final int length;

    public Rectangle(int width, int length) {
        this.width = width;
        this.length = length;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Rectangle.class.getSimpleName() + "[", "]")
                .add("width=" + width)
                .add("length=" + length)
                .toString();
    }

    private final static Rectangle[] RECTANGLES = {
            new Rectangle(10, 24),
            new Rectangle(12, 21),
            new Rectangle(14, 18),
            new Rectangle(16, 15),
            new Rectangle(18, 12),
            new Rectangle(20, 9),
    };

    public static void main(String[] args) {
        // max area
        Rectangle maxArea = findMax(RECTANGLES, Comparator.comparingInt(o -> (o.width * o.length)));
        System.out.println("find max area: " + maxArea);
        // max perimeter
        Rectangle maxPerimeter = findMax(RECTANGLES, Comparator.comparingInt(o -> (o.width + o.length) * 2));
        System.out.println("find max perimeter: " + maxPerimeter);
    }

    private static Rectangle findMax(@SuppressWarnings("SameParameterValue") Rectangle[] arr,
                                         Comparator<? super Rectangle> comparator) {
        int maxIndex = 0;

        for (int i = 0; i < arr.length; i++) {
            if (comparator.compare(arr[i], arr[maxIndex]) > 0) {
                maxIndex = i;
            }
        }

        return arr[maxIndex];
    }
}
