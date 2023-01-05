package com.replay.tool;

import com.replay.io.Console;
import com.replay.io.Persistence;

public class ReplayCli {
    public static void main(String[] args) {
        Console.initialize();
        Persistence.initialize();
        Console.replayln("version 1.0");
    }
}
