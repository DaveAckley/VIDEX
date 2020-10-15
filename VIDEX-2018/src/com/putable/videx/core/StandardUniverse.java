package com.putable.videx.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.putable.videx.interfaces.Universe;
import com.putable.videx.interfaces.World;

public class StandardUniverse implements Universe {
    public static StandardUniverse makeOneWorldUniverse(World w) {
        StandardUniverse u = new StandardUniverse();
        u.addWorld(w);
        return u;
    }
    @Override
    public Iterator<World> iterator() {
        return mWorlds.values().iterator();
    }

    @Override
    public World getWorld(String name) {
        return mWorlds.get(name);
    }

    public void addWorld(World world) {
        String name = world.getName();
        if (this.getWorld(name) != null)
            throw new IllegalStateException("World named "+name+" already defined");
        mWorlds.put(name,world);
    }
    
    private Map<String,World> mWorlds = new HashMap<String,World>();
}
