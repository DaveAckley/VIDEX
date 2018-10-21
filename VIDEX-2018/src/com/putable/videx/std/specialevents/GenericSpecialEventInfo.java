package com.putable.videx.std.specialevents;

import com.putable.videx.core.events.SpecialEventInfo;

public abstract class GenericSpecialEventInfo extends SpecialEventInfo {

    /**
     * {@inheritDoc}
     *
     * @return null unless overriden because GenericSpecialEventInfos have no payload.  
     *
     */
    @Override
    public Object getValue() {
        return null;
    }

}
