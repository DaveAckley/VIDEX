package com.putable.videx.std.specialevents;

import com.putable.videx.core.events.StandardSpecialEventInfo;

public abstract class GenericSpecialEventInfo extends StandardSpecialEventInfo {

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
