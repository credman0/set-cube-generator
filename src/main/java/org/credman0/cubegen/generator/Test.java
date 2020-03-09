package org.credman0.cubegen.generator;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    public static void main (String[] args) {
        try {
            CubeGenerator generator = new CubeGenerator(270, 60, new Set("theros", "theros"));
            GeneratorList generatorList = generator.generate(new ArrayList<>());
            System.out.println(generatorList);
            System.out.println(generatorList.budgetUsed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
