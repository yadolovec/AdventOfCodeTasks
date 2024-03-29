package com.some.app.EmptyApp.Advent2023.Day10;

import com.some.app.EmptyApp.util.Utils;

import java.util.*;

/**
 * @author Volodymyr Havrylets
 * @since 28.02.2024
 **/
public class Task2 {
    static Task1.Direction directionToIgnore = Task1.Direction.NONE;

    enum Direction {
        LEFT, RIGHT, DOWN, UP, NONE
    }

    public static void main(String[] args) {
        List<String> list = Utils.getListFromText("src/main/resources/Res2023/Day10t");
        int[] start = {-1, -1};

        List<char[]> map = new ArrayList<>();
        //Find the start and Create map
        for (int i = 0; i < list.size(); i++) {
            char[] charArray = list.get(i).toCharArray();
            map.add(charArray);
            if (start[0] == -1) {
                for (int j = 0; j < charArray.length; j++) {
                    if (charArray[j] == 'S') {
                        start[0] = i;
                        start[1] = j;
                        break;
                    }
                }
            }
        }

        int[] previousTile = start.clone();
        int[] tileCoordinates = start.clone();
        int[] additionalTile;


        Set<Tile> way = new HashSet<>();
        do {
            way.add(new Tile(tileCoordinates));
            tileCoordinates = findNextTile(tileCoordinates, map);

        } while (!Arrays.equals(tileCoordinates, start));


        int sum = 0;

        int area = 0;
        for (int i = 0; i < map.size(); i++) {

            for (int j = 0; j < map.get(0).length; j++) {

                if (isInside(new Tile(i, j), map, way)) {
                    area++;
                }
            }
        }

        System.out.println(area);

    }

    public static boolean isInside(Tile tile, List<char[]> map, Set<Tile> way) {
        if (way.contains(tile))
            return false;

        int[] beforeAfterHorizontal = {0, 0};
        int[] beforeAfterVertical = {0, 0};

        char startCheck = ' ';

        for (int i = 0; i < map.size(); i++) {
            Tile testedTile = new Tile(i, tile.getJ());
            char testedTileName = getTileByCoordinates(map, new int[]{testedTile.getI(), testedTile.getJ()});

            if (way.contains(testedTile)) {

                if (testedTileName != '-') {
                    if (startCheck == ' ') {
                        startCheck = testedTileName;
                        continue;
                    } else {

                        Boolean isSameDirection = lookSameDirection(startCheck, testedTileName);
                        if (isSameDirection != null) {
                            startCheck = ' ';
                            if (isSameDirection) {
                                continue;
                            }
                        }
                    }
                }

                if (i < tile.getI()) {
                    beforeAfterHorizontal[0]++;
                } else {
                    beforeAfterHorizontal[1]++;
                }
            }
        }

        for (int j = 0; j < map.get(0).length; j++) {
            Tile testedTile = new Tile(tile.getI(), j);
            char testedTileName = getTileByCoordinates(map, new int[]{testedTile.getI(), testedTile.getJ()});

            if (way.contains(testedTile)) {

                if (testedTileName != '|') {
                    if (startCheck == ' ') {
                        startCheck = testedTileName;
                        continue;
                    } else {

                        Boolean isSameDirection = lookSameDirection(startCheck, testedTileName);
                        if (isSameDirection != null) {
                            startCheck = ' ';
                            if (isSameDirection) {
                                continue;
                            }
                        }
                    }
                }

                if (j < tile.getJ()) {
                    beforeAfterVertical[0]++;
                } else {
                    beforeAfterVertical[1]++;
                }

            }

        }


        return beforeAfterVertical[0]%2 != 0 && beforeAfterHorizontal[0]%2 != 0;
    }

    public static Boolean lookSameDirection(char a, char b) {

        if (a == '7') {
            if (b == 'J') {
                return true;
            }
            if (b == 'L') {
                return false;
            }
        }

        if (a == 'F') {
            if (b == 'L') {
                return true;
            }
            if (b == 'J') {
                return false;
            }
        }

        if (a == 'L') {
            if (b == 'J') {
                return true;
            }
            if (b == '7') {
                return false;
            }
        }

        if (a == 'F') {
            if (b == '7') {
                return true;
            }
            if (b == 'J') {
                return false;
            }
        }

        return null;
    }

    public static int[] findNextTile(int[] tileCoordinates, List<char[]> map) {


        int[] rightTile = new int[]{tileCoordinates[0], tileCoordinates[1] + 1};
        int[] leftTile = new int[]{tileCoordinates[0], tileCoordinates[1] - 1};
        int[] downTile = new int[]{tileCoordinates[0] + 1, tileCoordinates[1]};
        int[] upTile = new int[]{tileCoordinates[0] - 1, tileCoordinates[1]};

        Set<Character> left = Set.of('-', 'F', 'L', 'S');
        Set<Character> right = Set.of('-', '7', 'J', 'S');
        Set<Character> up = Set.of('|', 'F', '7', 'S');
        Set<Character> down = Set.of('|', 'J', 'L', 'S');

        Map<Task1.Direction, Set<Character>> allowedCharactersByDirection = new HashMap<>();

        allowedCharactersByDirection.put(Task1.Direction.LEFT, left);
        allowedCharactersByDirection.put(Task1.Direction.RIGHT, right);
        allowedCharactersByDirection.put(Task1.Direction.UP, up);
        allowedCharactersByDirection.put(Task1.Direction.DOWN, down);

        char tileName = getTileByCoordinates(map, tileCoordinates);


        if (isInBounds(map, upTile) && directionToIgnore != Task1.Direction.UP
                && allowedCharactersByDirection.get(Task1.Direction.UP).contains(getTileByCoordinates(map, upTile))
                && isConnectable(tileName, getTileByCoordinates(map, upTile), Task1.Direction.UP)) {
            directionToIgnore = Task1.Direction.DOWN;
            return upTile;
        }


        if (isInBounds(map, downTile) && directionToIgnore != Task1.Direction.DOWN
                && allowedCharactersByDirection.get(Task1.Direction.DOWN).contains(getTileByCoordinates(map, downTile))
                && isConnectable(tileName, getTileByCoordinates(map, downTile), Task1.Direction.DOWN)) {
            directionToIgnore = Task1.Direction.UP;
            return downTile;
        }


        if (isInBounds(map, leftTile) && directionToIgnore != Task1.Direction.LEFT
                && allowedCharactersByDirection.get(Task1.Direction.LEFT).contains(getTileByCoordinates(map, leftTile))
                && isConnectable(tileName, getTileByCoordinates(map, leftTile), Task1.Direction.LEFT)) {
            directionToIgnore = Task1.Direction.RIGHT;
            return leftTile;
        }


        if (isInBounds(map, rightTile) && directionToIgnore != Task1.Direction.RIGHT
                && allowedCharactersByDirection.get(Task1.Direction.RIGHT).contains(getTileByCoordinates(map, rightTile))
                && isConnectable(tileName, getTileByCoordinates(map, rightTile), Task1.Direction.RIGHT)) {
            directionToIgnore = Task1.Direction.LEFT;
            return rightTile;
        }

        return null;
    }

    public static boolean isConnectable(char first, char second, Task1.Direction direction) {
        if (first == 'S' || second == 'S')
            return true;

        if (first == 'L' && (direction == Task1.Direction.DOWN || direction == Task1.Direction.LEFT))
            return false;

        if (first == 'J' && (direction == Task1.Direction.RIGHT || direction == Task1.Direction.DOWN))
            return false;


        if (first == '7' && (direction == Task1.Direction.RIGHT || direction == Task1.Direction.UP))
            return false;


        if (first == 'F' && (direction == Task1.Direction.UP || direction == Task1.Direction.LEFT))
            return false;

        if (second == '.')
            return false;

        if (first == '|') {
            if (direction == Task1.Direction.RIGHT || direction == Task1.Direction.LEFT)
                return false;

            return second != '-';
        }

        if (first == '-') {

            if (direction == Task1.Direction.UP || direction == Task1.Direction.DOWN)
                return false;
            return second != '|';
        }


        return !(first == second);
    }

    public static char getTileByCoordinates(List<char[]> map, int[] coordinates) {
        return map.get(coordinates[0])[coordinates[1]];
    }

    public static Task1.Direction checkDirection(int[] first, int[] second) {
        if (first[0] > second[0])
            return Task1.Direction.DOWN;
        if (first[0] < second[0])
            return Task1.Direction.UP;
        if (first[1] > second[1])
            return Task1.Direction.LEFT;
        if (first[1] < second[1])
            return Task1.Direction.RIGHT;

        return Task1.Direction.NONE;
    }

    public static boolean isInBounds(List<char[]> map, int[] array) {
        return !(array[0] < 0 || array[1] < 0 || array[0] >= map.size() || array[1] >= map.get(0).length);
    }

    public static class Tile {

        private int i;
        private int j;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

        public Tile(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public Tile(int[] array) {
            this.i = array[0];
            this.j = array[1];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Tile) {
                return i == ((Tile) o).getI() && j == ((Tile) o).getJ();
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return i * j + j * j * j;
        }
    }
}
