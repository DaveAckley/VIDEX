package com.putable.videx.std.vo.image;

import java.nio.file.Path;
import java.util.function.Predicate;

import com.putable.videx.std.vo.ReadOnlyDirectoryManager;

public class PNGImageDirectoryManager extends ReadOnlyDirectoryManager {

    @Override
    public Predicate<Path> getPredicate() {
        return (path) -> path.toString().endsWith(".png");
    }

}
