package com.example.dotcraft;

public class Level1 implements Level {

    private final int[] dotArr = new int[9];
    private final int[] containerArr = new int[9];

    public Level1() {
        dotArr[0] = 1;
        dotArr[5] = 1;
        dotArr[7] = 1;

        containerArr[1] = 1;
        containerArr[2] = 1;
        containerArr[3] = 1;
    }

    @Override
    public int[] getDotArray() {
        return dotArr;
    }

    @Override
    public int[] getContainerArray() {
        return containerArr;
    }
}
